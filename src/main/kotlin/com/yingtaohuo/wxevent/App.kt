package com.yingtaohuo.wxevent

import com.qq.weixin.wx3rd.WXComponentApi
import com.qq.weixin.wx3rd.WXComponentClient
import com.qq.weixin.wx3rd.WorkModeProd
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.thoughtworks.xstream.io.xml.StaxDriver
import com.thoughtworks.xstream.security.NoTypePermission
import com.thoughtworks.xstream.security.NullPermission
import com.thoughtworks.xstream.security.PrimitiveTypePermission
import com.yingtaohuo.wxevent.Config.Port
import com.yingtaohuo.wxevent.entrypt.SHA1
import com.yingtaohuo.wxevent.entrypt.WXBizMsgCrypt
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.slf4j.LoggerFactory
import org.springframework.oxm.xstream.XStreamMarshaller
import org.xml.sax.InputSource
import redis.clients.jedis.JedisPool
import spark.Spark.*
import spark.Spark.initExceptionHandler
import java.io.StringReader
import java.net.URI
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 * // https://github.com/perwendel/spark-debug-tools
 */

object AppHelper {

    private val db: DocumentBuilder

    // val xStream : XStreamMarshaller
    val wxCrypt = WXBizMsgCrypt(Config.WX3rdAppToken, Config.WX3rdEncodingAesKey, Config.WX3rdAppId)

    init {
        val dbf = DocumentBuilderFactory.newInstance()
        db = dbf.newDocumentBuilder()
    }

    inline fun <reified T> fromXml(xml: String) : T {
        val xStream = XStreamMarshaller()
        xStream.setAutodetectAnnotations(true)
        xStream.setStreamDriver(StaxDriver())
        xStream.setAliases(mapOf("xml" to T::class.java))
        xStream.xStream.addPermission(NoTypePermission.NONE)
        xStream.xStream.addPermission(NullPermission.NULL)
        xStream.xStream.addPermission(PrimitiveTypePermission.PRIMITIVES)
        xStream.xStream.allowTypes(arrayOf(T::class.java))
        return xStream.unmarshalInputStream(xml.byteInputStream()) as T
    }

    inline fun <reified T: Any> decrypt(encryptText: String) : T {
        val xmlData = wxCrypt.decrypt(encryptText)
        return fromXml(xmlData)
    }

    fun decryptRaw(encryptText: String) = wxCrypt.decrypt(encryptText)

    fun getInfoTypeFromEncrypt(encrypt: String) = getInfoType(decryptRaw(encrypt))

    fun getInfoType(xmlText: String) : String {
        val sr = StringReader(xmlText)
        val isa = InputSource(sr)
        val document = db.parse(isa)
        val root = document.documentElement
        return root.getElementsByTagName("InfoType").item(0).textContent
    }

}

object App {

    private val logger = LoggerFactory.getLogger(App::class.java)

    private val channel: Channel?

    private val connection: Connection?

    private val componentApi : WXComponentApi

    private val redis: JedisPool?

    private val eventHandler : EventHandler

    init {

        // redis
        val poolConfig = GenericObjectPoolConfig()
        poolConfig.maxIdle = 5
        poolConfig.minIdle = 3
        poolConfig.maxTotal = 10
        redis = JedisPool(poolConfig, URI(Config.redisHost), 1000)

        // api
        componentApi = WXComponentClient().build(WorkModeProd)

        // rabbitmq channel create
        connection = createRabbitConn()
        channel = connection.createChannel()

        val queueGroup1 = arrayListOf(
                "sys_order",
                "sys_order_test"
        ).map { queueName -> Pair(queueName, "component_token") }

        val queueGroup2 = arrayListOf(
                EventHandler.WXAuthorize,
                EventHandler.WXUnauthorize,
                EventHandler.WXUpdateAuthorize,
                EventHandler.WXComponentVerifyTicket
        )

        // 微信令牌
        rabbitMQExchangeQueueBind(connection, "wxtoken", queueGroup1)

        // 微信第三方平台授权的事件
        rabbitMQExchangeQueueBind(connection, "wxauthorize", queueGroup2)

        eventHandler = EventHandler(redis, channel, componentApi)

    }

    fun destroy() {
        channel?.close()
        connection?.close()
        redis?.destroy()
    }

    fun start() {

        /**
         * http://wx3rd.menuxx.com/wx/3rd/notify
        signature=b1f01f792773a0c2a68a9bd2a09b1061dc1ba49c&timestamp=1505444079&nonce=236382074&encrypt_type=aes&msg_signature=2dc385ea7e474497c81dec14ab198ca828934b2b
        <xml>
        <AppId><![CDATA[wxb3d033d520d15fe7]]></AppId>
        <Encrypt><![CDATA[KMmjrgC+G45ZqQ6gW0aDANzf7x5KhhW47JqBJtf7N2uAgX6/SlZ0wcIFSkDDiTMkWBXqYOVJ/lI4u0ewaoxm9Sy11D4+S5StLGshUa8Jdef2pbqRNCgx+4dO0rCiuxfk1RwZlcu2kAKNLERRffhzK9RRglSGxEu7B1ooch+AE5HZ+hKvgBXxfL910/xrApJJdnXYEVxh1rp59j5hThPkL/DOittr4Ze9R4M5ELG1gGN+zG7tG1FI6wO1mafu1UBvDLNXMix9Sk49G1ymPNUEOjJeULHQlrBMTRvqbomy/dNDa0QQs/bwbB6MkbNwOsSRukZl3k/RqMlakgkj7hWjTA==]]></Encrypt>
        </xml>
         */
        // post("/wx/3rd/notify", { req, resp ->
        post("/wx3rd/authorize", "application/xml", { req, resp ->

            resp.type("application/json;charset=UTF-8")

            logger.debug(" route /wx3rd/authorize -> url: " + req.url())
            logger.debug(" route /wx3rd/authorize -> queryString: " + req.queryString())
            logger.debug(" route /wx3rd/authorize -> body: " + req.body())

            val (_, encrypt) = AppHelper.fromXml<EncryptMsg1>(req.body())

            val timestamp = req.queryMap().get("timestamp").value()
            val nonce = req.queryMap().get("nonce").value()
            val msgSignature = req.queryMap().get("msg_signature").value()
            val signature = SHA1.getSHA1(Config.WX3rdAppToken, timestamp, nonce, encrypt)

            if (signature == msgSignature) {
                val infoType = AppHelper.getInfoTypeFromEncrypt(encrypt)
                logger.info("InfoType: $infoType")
                when (infoType) {
                    "component_verify_ticket" -> eventHandler.handleComponentToken(AppHelper.decrypt(encrypt))
                    "authorized" -> eventHandler.handleAuthorized(AppHelper.decrypt(encrypt))
                    "updateauthorized" -> eventHandler.handleUpdateAuthorized(AppHelper.decrypt(encrypt))
                    "unauthorized" -> eventHandler.handleUnAuthorize(AppHelper.decrypt(encrypt))
                }
                "success"
            } else {
                "fail"
            }

        })

        // A wx3rd 120.132.29.90
    }

}

fun main(args: Array<String>) {

    threadPool(10)
    port(Port)

    initExceptionHandler { e ->
        e.printStackTrace()
    }

    App.start()

    init()

    // 关闭的时候释放资源
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            super.run()
            App.destroy()
        }
    })

}