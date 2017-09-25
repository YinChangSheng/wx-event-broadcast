package com.qq.weixin.wx3rd

import com.google.gson.Gson
import feign.*
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import feign.okhttp.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/18
 * 微信: yin80871901
 */

@Headers("Accept: application/json", "Content-Type: application/json")
interface WXComponentApi {

    /**
     * response => {
     *  "component_access_token": "61W3mEpU66027wgNZ_MhGHNQDHnFATkDa9-2llqrMBjUwxRSNPbVsMmyD-yq8wZETSoE5NQgecigDrSHkPtIYA",
     *  "expires_in": 7200
     * }
     */
    @RequestLine("POST /api_component_token")
    @Throws(WXException::class)
    @Body("%7B\"component_appid\": \"{component_appid}\", \"component_appsecret\": \"{component_appsecret}\", \"component_verify_ticket\": \"{component_verify_ticket}\"%7D")
    fun apiComponentToken(
            @Param("component_appid") componentAppId: String,
            @Param("component_appsecret") componentAppSecret: String,
            @Param("component_verify_ticket") verifyTicket: String
    ) : WXComponentToken

}

val WorkModeProd = 1
val WorkModeMock = 2

class WXComponentClient {

    private val logger = LoggerFactory.getLogger(WXComponentClient::class.java)

    private val baseUrl = "https://api.weixin.qq.com/cgi-bin/component"

    private val mockBaseUrl = "http://127.0.0.1:9800/cgi-bin/component"

    private val gson = Gson()

    private val okhttp : okhttp3.OkHttpClient

    init {
        okhttp = okhttp3.OkHttpClient
                .Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor(logger::debug))
                .build()
    }

    fun build(mode: Int) : WXComponentApi {
        return Feign.builder()
                .decoder(GsonDecoder(gson))
                .encoder(GsonEncoder(gson))
                .client(OkHttpClient())
                // 使用 mock server
                .target(WXComponentApi::class.java, if ( mode == 1 ) baseUrl else mockBaseUrl)
    }

}