package com.yingtaohuo.wxevent

import com.google.gson.Gson
import com.qq.weixin.wx3rd.WXComponentToken
import com.rabbitmq.client.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/20
 * 微信: yin80871901
 */
class RabbitMQTest {

    companion object {

        private lateinit var channel: Channel

        private lateinit var conn: Connection

        private val appId = "wxb3d033d520d15fe7"

        private val token = WXComponentToken("d93n834u3uxzi10i.293zox3mc34vc83u24ni8xi3mr2iz23orz,2o3xric98nb3nvcmo4iyvucrp32moyc", 7200)

        @BeforeClass
        @JvmStatic
        fun setup() {
            // rabbitmq channel create
            conn = createRabbitConn()
            channel = conn.createChannel()
            // 定义 exchange 和 queue

            val queueGroup1 = arrayListOf(
                    "sys_wx3rd",
                    "sys_wx3rd_test",
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
            rabbitMQExchangeQueueBind(conn, "wxtoken", queueGroup1)

            // 微信第三方平台授权的事件
            rabbitMQExchangeQueueBind(conn, "wxauthorize", queueGroup2)

        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            channel.close()
            conn.close()
        }

    }

    @Test
    fun testConn() {

    }

    @Test
    fun testPublish() {
        val consumerRabbitMq = createRabbitConn()
        val _channel = consumerRabbitMq.createChannel()
        val aaa = """
            { "appId": "1111", "authorizerAppid": "2222", "authorizationCode": "3333" }
            """
        _channel.basicPublish("wxauthorize", "wx.authorized", null, aaa.toByteArray())
    }

    @Test
    fun testBasicConsume() {

        val consumerRabbitMq = createRabbitConn()
        val _channel = consumerRabbitMq.createChannel()

        _channel.basicConsume("sys_order_test", object : Consumer {
            override fun handleRecoverOk(consumerTag: String?) {
                println("consumer1 handleRecoverOk")
            }

            override fun handleConsumeOk(consumerTag: String?) {
                println("consumer1 handleConsumeOk")
            }

            override fun handleShutdownSignal(consumerTag: String?, sig: ShutdownSignalException?) {
                println("consumer1 handleShutdownSignal")
            }

            override fun handleCancel(consumerTag: String?) {
                println("consumer1 handleCancel")
            }

            override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties?, body: ByteArray?) {
                if ( body != null ) {
                    println("$consumerTag consumer1 handleDelivery ${String(body)}")
                }
                _channel.basicAck(envelope.deliveryTag, false)
            }

            override fun handleCancelOk(consumerTag: String?) {
                println("consumer1 handleCancelOk")
            }

        })

        channel.basicPublish("wxtoken", "component_token", null, Gson().toJson(token).toByteArray())
    }

}