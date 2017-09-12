package com.yingtaohuo.wxevent

import com.rabbitmq.client.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 */

fun rabbitmq() {

    val connFactory = ConnectionFactory()
    connFactory.setUri("amqp://menuxx:28181820@wx3rd.menuxx.com:5692/menuxx")
    val conn = connFactory.newConnection()
    val channel = conn.createChannel()

    val isOk = channel.exchangeDeclare("wxtoken", "fanout")

    println(isOk)

    val queue1 = channel.queueDeclare("sys_wx3rd", false, false, false, null)

    println("queue2: ${queue1.queue} consumer count ${queue1.consumerCount}, message count ${queue1.messageCount}")

    val queue2 = channel.queueDeclare("sys_order", false, false, false, null)

    println("queue2: ${queue2.queue} consumer count ${queue2.consumerCount}, message count ${queue2.messageCount}")

    val queue3 = channel.queueDeclare("sys_wx3rd_test", false, false, false, null)

    println("queue2: ${queue3.queue} consumer count ${queue3.consumerCount}, message count ${queue3.messageCount}")

    val isOk1 = channel.queueBind("sys_wx3rd", "wxtoken", "wx3rd_ticket")

    println(isOk1)

    val isOk2 = channel.queueBind("sys_order", "wxtoken", "wx3rd_ticket")

    println(isOk2)

    val isOk3 =  channel.queueBind("sys_wx3rd_test", "wxtoken", "wx3rd_ticket")

    println(isOk3)
}

fun consumers() {
    consumer1()
    consumer2()
    consumer3()
}

fun consumer1() {

    println("consumer1 ..")

    val connFactory = ConnectionFactory()
    connFactory.setUri("amqp://menuxx:28181820@wx3rd.menuxx.com:5692/menuxx")
    val conn = connFactory.newConnection()
    val channel = conn.createChannel()
    channel.basicConsume("sys_wx3rd", object : Consumer {
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
            channel.basicAck(envelope.deliveryTag, false)
        }

        override fun handleCancelOk(consumerTag: String?) {
            println("consumer1 handleCancelOk")
        }

    })
}

fun consumer2() {

    println("consumer2 ..")

    val connFactory = ConnectionFactory()
    connFactory.setUri("amqp://menuxx:28181820@wx3rd.menuxx.com:5692/menuxx")
    val conn = connFactory.newConnection()
    val channel = conn.createChannel()

    channel.basicConsume("sys_order", object : Consumer {
        override fun handleRecoverOk(consumerTag: String?) {
            println("consumer2 handleRecoverOk")
        }

        override fun handleConsumeOk(consumerTag: String?) {
            println("consumer2 handleConsumeOk")
        }

        override fun handleShutdownSignal(consumerTag: String?, sig: ShutdownSignalException?) {
            println("consumer2 handleShutdownSignal")
        }

        override fun handleCancel(consumerTag: String?) {
            println("consumer2 handleCancel")
        }

        override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties?, body: ByteArray?) {
            if ( body != null ) {
                println("$consumerTag consumer2 handleDelivery ${String(body)}")
            }
            channel.basicAck(envelope.deliveryTag, false)
        }

        override fun handleCancelOk(consumerTag: String?) {
            println("consumer2 handleCancelOk")
        }
    })
}

fun consumer3() {

    println("consumer3 ..")

    val connFactory = ConnectionFactory()
    connFactory.setUri("amqp://menuxx:28181820@wx3rd.menuxx.com:5692/menuxx")
    val conn = connFactory.newConnection()
    val channel = conn.createChannel()

    channel.basicConsume("sys_wx3rd_test", object : Consumer {
        override fun handleRecoverOk(consumerTag: String?) {
            println("consumer3 handleRecoverOk")
        }

        override fun handleConsumeOk(consumerTag: String?) {
            println("consumer3 handleConsumeOk")
        }

        override fun handleShutdownSignal(consumerTag: String?, sig: ShutdownSignalException?) {
            println("consumer3 handleShutdownSignal")
        }

        override fun handleCancel(consumerTag: String?) {
            println("consumer3 handleCancel")
        }

        override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties?, body: ByteArray?) {
            if ( body != null ) {
                println("$consumerTag consumer3 handleDelivery ${String(body)}")
            }
            channel.basicAck(envelope.deliveryTag, false)
        }

        override fun handleCancelOk(consumerTag: String?) {
            println("consumer3 handleCancelOk")
        }
    })
}