package com.yingtaohuo.wxevent

import com.google.gson.Gson

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 */

val gson = Gson()

val toJson : ( a: Any ) -> String = { o -> gson.toJson(o) }

inline fun <reified T> fromJson(json: String): T {
    return gson.fromJson<T>(json, T::class.java)
}