package com.yingtaohuo.wxevent

import com.rabbitmq.client.ConnectionFactory
import com.yingtaohuo.wxevent.Config.Port
import com.yingtaohuo.wxevent.Config.WX3rdComponentEntryPoint
import spark.Spark.*
import spark.Spark.initExceptionHandler



/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 */
fun main(args: Array<String>) {

    threadPool(30)
    port(Port)

    rabbitmq()

    consumers()

    initExceptionHandler { e ->
        e.printStackTrace()
        println(e.message)
    }

    val connFactory = ConnectionFactory()
    connFactory.setUri("amqp://menuxx:28181820@wx3rd.menuxx.com:5692/menuxx")
    val conn = connFactory.newConnection()
    val channel = conn.createChannel()

    post(WX3rdComponentEntryPoint, { req, resp ->

        val signature = req.params("signature")
        val timestamp = req.params("timestamp")
        val nonce = req.params("nonce")
        val encryptType = req.params("encrypt_type")
        val msgSignature = req.params("msg_signature")

        req.body()

        // val ticket = fromXml<ComponentVerifyTicket>(req.body())
        // channel.basicPublish("wxtoken", "wx3rd_ticket", null, Gson().toJson(ticket).toByteArray())
        // ticket
    }, toJson)

    init()
}