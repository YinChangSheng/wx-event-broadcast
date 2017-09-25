package com.yingtaohuo.wxevent

import com.google.gson.Gson
import com.qq.weixin.wx3rd.WXComponentApi
import com.rabbitmq.client.Channel
import org.slf4j.LoggerFactory
import redis.clients.jedis.JedisPool

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/20
 * 微信: yin80871901
 */

class EventHandler(
        private val redis: JedisPool,
        private val channel: Channel,
        private val componentApi: WXComponentApi
) {

    companion object {

        val WXAuthorize = Pair("wx_authorized", "wx.authorized")

        val WXUpdateAuthorize = Pair("wx_updateauthorize", "wx.updateauthorize")

        val WXUnauthorize = Pair("wx_unauthorize", "wx.unauthorize")

        val WXComponentVerifyTicket = Pair("wx_component_verify_ticket", "wx.component_verify_ticket")

    }

    private val logger = LoggerFactory.getLogger(EventHandler::class.java)

    private val gson = Gson()

    fun publishTo(jsonString: String, routingKey: String) {
        channel.basicPublish("wxauthorize", routingKey, null, jsonString.toByteArray())
    }

    fun handleAuthorized(event: ComponentAuthorizedEvent) {
        logger.info("ComponentAuthorizedEvent : " + gson.toJson(event))
        publishTo(gson.toJson(event), WXAuthorize.second)
    }

    fun handleUpdateAuthorized(event: ComponentUpdateAuthorizedEvent) {
        logger.info("ComponentUpdateAuthorizedEvent : " + gson.toJson(event))
        publishTo(gson.toJson(event), WXUpdateAuthorize.second)
    }

    fun handleUnAuthorize(event: ComponentUnauthorizedEvent) {
        logger.info("ComponentUnauthorizedEvent : " + gson.toJson(event))
        publishTo(gson.toJson(event), WXUnauthorize.second)
    }

    fun handleComponentToken(event: ComponentVerifyTicketEvent) {
        logger.info("ComponentVerifyTicketEvent : " + gson.toJson(event))
        publishTo(gson.toJson(event), WXComponentVerifyTicket.second)

        val token = componentApi.apiComponentToken(
                componentAppId = event.appId,
                componentAppSecret = Config.WX3rdAppSecret,
                verifyTicket = event.componentVerifyTicket
        )
        logger.info("token : " + gson.toJson(token))
        val tokenText = gson.toJson(token)
        // 广播 得到的 component_token
        // channel.basicPublish("wxtoken", "component_token", null, tokenText.toByteArray())
        // 存储到缓存
        redis.resource.set("component_token:${event.appId}", tokenText)
        redis.resource.close()
    }

}