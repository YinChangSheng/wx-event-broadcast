package com.yingtaohuo.wxevent

import com.thoughtworks.xstream.annotations.XStreamAlias

@XStreamAlias("xml")
data class EncryptMsg1(
        @XStreamAlias("AppId") val appId: String,
        @XStreamAlias("Encrypt") val encrypt: String
)

@XStreamAlias("xml")
data class EncryptMsg2(
        @XStreamAlias("ToUserName") val toUserName: String,
        @XStreamAlias("Encrypt") val encrypt: String
)

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
data class ComponentVerifyTicketEvent(
        @XStreamAlias("AppId")
        val appId: String,
        @XStreamAlias("ComponentVerifyTicket")
        val componentVerifyTicket: String,
        @XStreamAlias("InfoType")
        val infoType: String,
        @XStreamAlias("CreateTime")
        val createTime: Int
)

/**
 * doc : https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1453779503&token=&lang=zh_CN
 *
 * url : /wx/3rd/notify?signature=0d565e505c560a4c1e5b814674182f68cfefbeb6
 *      &timestamp=1496461020&nonce=805459633&encrypt_type=aes
 *      &msg_signature=8e5a1cc3e9152f0f00fd0a50e568f3197474a1a2
 * msg :
 *  {
 *    AppId: 'wxb3d033d520d15fe7',
 *    CreateTime: '1496461020',
 *    InfoType: 'authorized',
 *    AuthorizerAppid: 'wxd101a85aa106f53e',
 *    AuthorizationCode: 'queryauthcode@@@x4BhekqBmEo3mSUXr6eu3g4xnKLlG4zK6QXJRJTP_F5Kk5Olu2PxjswYSdRuAi2Ydj4cpMHExaBVJeyrMMa8IQ',
 *    AuthorizationCodeExpiredTime: '1496464620'
 *  }
 * 字段说明：
 * 字段名称                         字段描述
 * AppId                          第三方平台appid
 * CreateTime                     时间戳
 * InfoType                       authorized是授权成功通知
 * AuthorizerAppid                公众号或小程序
 * AuthorizationCode              授权码，可用于换取公众号的接口调用凭据，详细见上面的说明
 * AuthorizationCodeExpiredTime   授权码过期时间
 *
 * 授权成功通知
 * <xml>
 *   <AppId>第三方平台appid</AppId>
 *   <CreateTime>1413192760</CreateTime>
 *   <InfoType>authorized</InfoType>
 *   <AuthorizerAppid>公众号appid</AuthorizerAppid>
 *   <AuthorizationCode>授权码（code）</AuthorizationCode>
 *   <AuthorizationCodeExpiredTime>过期时间</AuthorizationCodeExpiredTime>
 * </xml>
 **/

@XStreamAlias("xml")
data class ComponentAuthorizedEvent(
        @XStreamAlias("AppId")
        val appId: String,
        @XStreamAlias("CreateTime")
        val createTime: Int,
        @XStreamAlias("InfoType")
        val infoType: String,
        @XStreamAlias("AuthorizerAppid")
        val authorizerAppid: String,
        @XStreamAlias("AuthorizationCode")
        val authorizationCode: String,
        @XStreamAlias("AuthorizationCodeExpiredTime")
        val authorizationCodeExpiredTime: String
)

/**
 * 字段说明：
 * 字段名称                         字段描述
 * AppId                          第三方平台appid
 * CreateTime                     时间戳
 * InfoType                       updateauthorized是更新授权
 * AuthorizerAppid                公众号或小程序
 * AuthorizationCode              授权码，可用于换取公众号的接口调用凭据，详细见上面的说明
 * AuthorizationCodeExpiredTime   授权码过期时间
 *
 * 授权更新通知
 * <xml>
 *  <AppId>第三方平台appid</AppId>
 *  <CreateTime>1413192760</CreateTime>
 *  <InfoType>updateauthorized</InfoType>
 *  <AuthorizerAppid>公众号appid</AuthorizerAppid>
 *  <AuthorizationCode>授权码（code）</AuthorizationCode>
 *  <AuthorizationCodeExpiredTime>过期时间</AuthorizationCodeExpiredTime>
 * </xml>
 **/
@XStreamAlias("xml")
data class ComponentUpdateAuthorizedEvent(
        @XStreamAlias("AppId")
        val appId: String,
        @XStreamAlias("CreateTime")
        val createTime: Int,
        @XStreamAlias("InfoType")
        val infoType: Int,
        @XStreamAlias("AuthorizerAppid")
        val authorizerAppid: String,
        @XStreamAlias("AuthorizationCode")
        val authorizationCode: String,
        @XStreamAlias("AuthorizationCodeExpiredTime")
        val authorizationCodeExpiredTime: String
)

/**
 *
 * url :  /wx/3rd/notify?signature=3e3913451936b865c9409e307fd5fa8d834108e6&timestamp=1496461034&nonce=2120938598&encrypt_type=aes&msg_signature=71d49f8eb081fd260406b7d7017c49f71dc9ee95
 * msg :  { AppId: 'wxb3d033d520d15fe7',
 * CreateTime: '1496461034',
 * InfoType: 'unauthorized',
 * AuthorizerAppid: 'wxd101a85aa106f53e' }
 *
 * 字段说明：
 * 字段名称                         字段描述
 * AppId                          第三方平台appid
 * CreateTime                     时间戳
 * InfoType                       unauthorized是取消授权
 * AuthorizerAppid                公众号或小程序
 * AuthorizationCode              授权码，可用于换取公众号的接口调用凭据，详细见上面的说明
 * AuthorizationCodeExpiredTime   授权码过期时间
 * 取消授权通知
 * <xml>
 *  <AppId>第三方平台appid</AppId>
 *  <CreateTime>1413192760</CreateTime>
 *  <InfoType>unauthorized</InfoType>
 *  <AuthorizerAppid>公众号appid</AuthorizerAppid>
 * </xml>
 */

@XStreamAlias("xml")
data class ComponentUnauthorizedEvent(
        @XStreamAlias("AppId")
        val appId: String,
        @XStreamAlias("CreateTime")
        val createTime: Int,
        @XStreamAlias("InfoType")
        val infoType: String,
        @XStreamAlias("AuthorizerAppid")
        val authorizerAppid: String
)

@XStreamAlias("xml")
data class MsgEvent(
        @XStreamAlias("ToUserName")
        val toUserName: String,
        @XStreamAlias("FromUserName")
        val fromUserName: String,
        @XStreamAlias("CreateTime")
        val createTime: String,
        @XStreamAlias("MsgType")
        val msgType: String,
        @XStreamAlias("Content")
        val content: String,
        @XStreamAlias("MsgId")
        val msgId: String
)