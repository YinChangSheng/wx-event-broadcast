package com.yingtaohuo.wxevent

import com.rabbitmq.client.*
import com.yingtaohuo.wxevent.Config.BroadcastRabbitMQUrl
import org.slf4j.LoggerFactory

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 */

fun createRabbitConn() : Connection {
    val connFactory = ConnectionFactory()
    connFactory.setUri(BroadcastRabbitMQUrl)
    connFactory.requestedHeartbeat = 60
    return connFactory.newConnection()
}

// sys_wx3rd sys_order sys_wx3rd_test

fun rabbitMQExchangeQueueBind(conn: Connection, exchangeName: String, queueGroup: List<Pair<String, String>>) {

    val logger = LoggerFactory.getLogger("RabbitMQ")

    val channel = conn.createChannel()

    val isOk = channel.exchangeDeclare(exchangeName, "fanout", true)

    if (logger.isDebugEnabled) {
        if ( isOk != null ) {
            logger.debug("$exchangeName exchange declare success")
        } else {
            logger.error("$exchangeName exchange declare fail")
        }
    }

    for ((queueName, routingKey) in queueGroup) {
        try {
            val queue = channel.queueDeclare(queueName, true, false, false, null)
            if (logger.isDebugEnabled) {
                logger.debug("${queue.queue} consumer count ${queue.consumerCount}, message count ${queue.messageCount}")
            }
            logger.info("queueName: $queueName, exchangeName: $exchangeName, routingKey: $routingKey")
            val isQueueOk = channel.queueBind(queueName, exchangeName, routingKey)
            if ( logger.isDebugEnabled ) {
                logger.debug("queue ${isQueueOk.protocolMethodId()}")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}

fun dumpConsume(queueName: String) {

    println("consume ..")

    val conn = createRabbitConn()
    val channel = conn.createChannel()
    channel.basicConsume(queueName, object : Consumer {
        override fun handleRecoverOk(consumerTag: String?) {
            println("consumer handleRecoverOk")
        }

        override fun handleConsumeOk(consumerTag: String?) {
            println("consumer handleConsumeOk")
        }

        override fun handleShutdownSignal(consumerTag: String?, sig: ShutdownSignalException?) {
            println("consumer handleShutdownSignal")
        }

        override fun handleCancel(consumerTag: String?) {
            println("consumer handleCancel")
        }

        override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties?, body: ByteArray?) {
            if ( body != null ) {
                println("$consumerTag consumer1 handleDelivery ${String(body)}")
            }
            channel.basicAck(envelope.deliveryTag, false)
        }

        override fun handleCancelOk(consumerTag: String?) {
            println("consumer handleCancelOk")
        }

    })
}