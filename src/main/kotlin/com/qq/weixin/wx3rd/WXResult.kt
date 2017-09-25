package com.qq.weixin.wx3rd

import com.google.gson.annotations.SerializedName

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/18
 * 微信: yin80871901
 */

data class WXComponentToken(
        @SerializedName("component_access_token") val componentAccessToken: String,
        @SerializedName("expires_in") val expiresIn: Int
)