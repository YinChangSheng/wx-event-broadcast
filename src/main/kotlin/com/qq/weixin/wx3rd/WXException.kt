package com.qq.weixin.wx3rd

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/18
 * 微信: yin80871901
 */
class WXException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}