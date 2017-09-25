package com.yingtaohuo.wxevent

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.junit.*
import redis.clients.jedis.JedisPool
import java.net.URI

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/20
 * 微信: yin80871901
 */

class RedisTest {

    companion object {

        private val testKey = "9j882jeadh8q2djqidjii"

        private val testValue = "67x87r283n8m39zr3ru2x92u3xm2u3rm2"

        private lateinit var redis: JedisPool

        @BeforeClass
        @JvmStatic
        fun setup() {
            val poolConfig = GenericObjectPoolConfig()
            poolConfig.maxIdle = 5
            poolConfig.minIdle = 3
            poolConfig.maxTotal = 10
            redis = JedisPool(poolConfig, URI(Config.redisHost), 1000)
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            redis.destroy()
        }

    }

    @Test
    fun testGet() {
        val value = redis.resource.get(testKey)
        redis.resource.close()
        println(value)
        Assert.assertEquals(value, testValue)
    }

    @Test
    fun testSet() {
        val res = redis.resource.set(testKey, testValue)
        redis.resource.close()
        Assert.assertEquals(res, "OK")
    }

    @Test
    fun testConn() {
        redis.resource.close()
        Assert.assertEquals(redis.resource.isConnected, true)
    }

}