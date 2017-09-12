package com.yingtaohuo.wxevent

import com.thoughtworks.xstream.io.xml.StaxDriver
import org.springframework.oxm.xstream.XStreamMarshaller

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 */

fun main(args: Array<String>) {

val xml = """
<xml>
<AppId>31231231242314</AppId>
<CreateTime>1413192605</CreateTime>
<InfoType>component_verify_ticket</InfoType>
<ComponentVerifyTicket>423123142134</ComponentVerifyTicket>
</xml>
"""

    val xml1 = "<?xml version=\"1.0\" ?><xml><AppId>111111111</AppId><ComponentVerifyTicket>1213123</ComponentVerifyTicket><InfoType>asdasdasdasd</InfoType><CreateTime>444444444444</CreateTime></xml>"

    // val jaxbContext = JAXBContext.newInstance(ComponentVerifyTicket::class.java)

    // val jaxbUnmarshaller = jaxbContext.createUnmarshaller()
    // val customer = jaxbUnmarshaller.unmarshal(xml1.byteInputStream()) as ComponentVerifyTicket
    // System.out.println(customer)

    val ticket = ComponentVerifyTicket("11111", "22222", "33333", "44444")

    // jaxbContext.createMarshaller().marshal(ticket, System.out)

//     val xStream = XStream(StaxDriver())
//     xStream.processAnnotations(ComponentVerifyTicket::class.java)
//     xStream.alias("xml", ComponentVerifyTicket::class.java)
//     xStream.aliasField("AppId", ComponentVerifyTicket::class.java, "appId")
//     xStream.autodetectAnnotations(true)

//    val xml2 = xStream.toXML(ticket)
//
//    println(xml2)
//
//     val ticket1 = xStream.fromXML(xml2, ComponentVerifyTicket::class.java)
//     println(ticket1)

    val xstream = XStreamMarshaller()
    xstream.setAutodetectAnnotations(true)
    xstream.setStreamDriver(StaxDriver())
    xstream.setAliases(
            mapOf("xml" to ComponentVerifyTicket::class.java)
    )
    val aa = xstream.unmarshalInputStream(xml1.byteInputStream())
    println(aa)
}