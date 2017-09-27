package com.yingtaohuo.wxevent

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 */
object Config {

    const val Port = 9002

    const val WX3rdAuthorizeEntryPoint = "/wx3rd/entry"

    const val WX3rdMsgEventEntryPoint = "/wx3rd/:appId/callback"

    // 第三方平台相关
    const val WX3rdAppId = "wxb3d033d520d15fe7"
    const val WX3rdAppKey = "gh_e9100e798b39"
    const val WX3rdAppToken = "dg156c5719d3202f32a6619e14D0ccqd"
    const val WX3rdAppSecret = "99454ebe9eb76b704d9dfd8c34cc310b"
    const val WX3rdEncodingAesKey = "kat52d5719d320nEj2A6l19u14H21ct2aI8K08rltKl"

    const val BroadcastRabbitMQUrl = "amqp://menuxx:28181820@wx3rd.menuxx.com:5692/menuxx"

    // token 存储
    const val redisHost = "redis://:4cj34t4j9@wx3rd.menuxx.com:7389/9"

}