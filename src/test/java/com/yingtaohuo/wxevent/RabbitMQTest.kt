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
            // rabbitMQExchangeQueueBind(conn, "wxtoken", "component_token", arrayListOf("sys_order", "test_sys_order"))
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

        _channel.basicConsume("test_sys_order", object : Consumer {
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