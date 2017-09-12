package com.yingtaohuo.wxevent

import com.thoughtworks.xstream.io.xml.StaxDriver
import com.thoughtworks.xstream.security.NoTypePermission
import com.thoughtworks.xstream.security.NullPermission
import com.thoughtworks.xstream.security.PrimitiveTypePermission
import org.springframework.oxm.xstream.XStreamMarshaller

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 */

inline fun <reified T> fromXml(xml: String) : T {
    val xStream = XStreamMarshaller()
    xStream.setAutodetectAnnotations(true)
    xStream.setStreamDriver(StaxDriver())
    xStream.setAliases(mapOf("xml" to T::class.java))
    xStream.xStream.addPermission(NoTypePermission.NONE)
    xStream.xStream.addPermission(NullPermission.NULL)
    xStream.xStream.addPermission(PrimitiveTypePermission.PRIMITIVES)
    xStream.xStream.allowTypes(arrayOf(ComponentVerifyTicket::class.java))
    return xStream.unmarshalInputStream(xml.byteInputStream()) as T
}