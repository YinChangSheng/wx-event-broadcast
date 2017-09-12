package com.yingtaohuo.wxevent

import com.thoughtworks.xstream.annotations.XStreamAlias

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 *
 * url :  /wx/3rd/notify?signature=a5bd2c7f5ef89b5b18a703964157a96474a040ca&timestamp=1496461523&nonce=260040557&encrypt_type=aes&msg_signature=cab074cd1ed07bfecc9012b4912eb4249d9f958d
 * msg :
 *  {
 *    AppId: 'wxb3d033d520d15fe7',
 *    CreateTime: '1496461523',
 *    InfoType: 'component_verify_ticket',
 *    ComponentVerifyTicket: 'ticket@@@aglquGhXj06i8hOe_sTqW2enDJoI8pxH7xL-FcDOTbbjKLePiWEwr9kehVf5Oz6JDIlhk_BLsTuNw6Je-ifuVg'
 *  }
 *
 * 处理微信第三方验证票据
 * 推送component_verify_ticket协议
 * 在第三方平台创建审核通过后，微信服务器会向其
 * “授权事件接收URL”每隔10分钟定时推送component_verify_ticket。
 * 第三方平台方在收到ticket推送后也需进行解密（详细请见【消息加解密接入指引】），接收到后必须直接返回字符串success。
 * POST数据示例
 * <xml>
 *   <AppId></AppId>
 *   <CreateTime>1413192605</CreateTime>
 *   <InfoType>component_verify_ticket</InfoType>
 *   <ComponentVerifyTicket></ComponentVerifyTicket>
 * </xml>
 *
 * 字段说明
 * 字段名称                字段描述
 * AppId                 第三方平台appid
 * CreateTime            时间戳
 * InfoType              component_verify_ticket
 * ComponentVerifyTicket Ticket内容
**/
@XStreamAlias("xml")
data class ComponentVerifyTicket(
        @XStreamAlias("AppId")
        val appId: String,
        @XStreamAlias("ComponentVerifyTicket")
        val componentVerifyTicket: String,
        @XStreamAlias("InfoType")
        val infoType: String,
        @XStreamAlias("CreateTime")
        val createTime: String
)