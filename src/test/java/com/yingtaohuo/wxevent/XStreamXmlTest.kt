package com.yingtaohuo.wxevent

import com.qq.weixin.wx3rd.WXComponentClient
import com.qq.weixin.wx3rd.WorkModeMock
import com.yingtaohuo.wxevent.entrypt.SHA1
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.*


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/9/12
 * 微信: yin80871901
 */

/**
 * 测试数据:
 * url: http://wx3rd.menuxx.com/wx/3rd/notify
 * queryString: signature=5942b833e3537b540362ab6417ef90461b9f0805&timestamp=1505849204&nonce=1848583498&encrypt_type=aes&msg_signature=837958d294e42725c72cc6a4362bef2cbeb62f02
 * body: <xml>
 * <AppId><![CDATA[wxb3d033d520d15fe7]]></AppId>
 * <Encrypt><![CDATA[GNZjV6Hy3dlQutTKUGuEGhzcmA53vVbUPJbw+7au9j3xSK2UA/Dnmadikp493AAP8ImhM5rB5No/3jGA2skUFTIyWT/8wMjdN/TzYs2dktdelaefuik/QUFNOkp5OXgoTY0krFZYzn4BHkKRKx1XWWQXuFzdygCPNkPoINLZAu51g93oGvxsjbzQ+SvOiU5UqeGOoAVCMvBBAuAo5nl3IeYVuyqQATM45TKvXi8lm+8txPj46TXT57gm/Cd7saVaWhj3rosg2xmwvwZykV+B6Xz3+FWKcPq4jSUCxlDg6Rwme0atKdgt8SYM5mUZv+mjUuKBJ9xEkb7RM5VJR518zyHu2dWbGlpsIB8yqOasN0E1AajvwMHeE8/prxHnZgj/wlGaru8uG9y/+CDTvzpcfZIYj4DP7A42qTEfaV9WK4RxBAx5UYVdsL0YAMNZmQs+dq817WLhLJo4uuK3x30CxQ==]]></Encrypt>
 * </xml>
 */
class XStreamXmlTest {

    companion object {

        private lateinit var server: MockWebServer

        private val msgSignature = "837958d294e42725c72cc6a4362bef2cbeb62f02"
        private val timestamp = "1505849204"
        private val nonce = "1848583498"

        private val testXml = """
        <xml>
        <AppId><![CDATA[wxb3d033d520d15fe7]]></AppId>
        <Encrypt><![CDATA[GNZjV6Hy3dlQutTKUGuEGhzcmA53vVbUPJbw+7au9j3xSK2UA/Dnmadikp493AAP8ImhM5rB5No/3jGA2skUFTIyWT/8wMjdN/TzYs2dktdelaefuik/QUFNOkp5OXgoTY0krFZYzn4BHkKRKx1XWWQXuFzdygCPNkPoINLZAu51g93oGvxsjbzQ+SvOiU5UqeGOoAVCMvBBAuAo5nl3IeYVuyqQATM45TKvXi8lm+8txPj46TXT57gm/Cd7saVaWhj3rosg2xmwvwZykV+B6Xz3+FWKcPq4jSUCxlDg6Rwme0atKdgt8SYM5mUZv+mjUuKBJ9xEkb7RM5VJR518zyHu2dWbGlpsIB8yqOasN0E1AajvwMHeE8/prxHnZgj/wlGaru8uG9y/+CDTvzpcfZIYj4DP7A42qTEfaV9WK4RxBAx5UYVdsL0YAMNZmQs+dq817WLhLJo4uuK3x30CxQ==]]></Encrypt>
        </xml>
    """.trimIndent()

        private val acceptTicket = "ticket@@@e0pTjwtLX_hsLGHRTREIzZeAVTV0gddrrNfPjh1aGZqO9Pz_V4kBkHh_og_5Ev4BbNhvurZMu_DQpiO4mKCN7Q"

        private val acceptComponentToken = "QB1qov6IM2rZX447wdkivec25rUymn6IiXkMk6eqPlJhbt9UMSCKAAjCTBPiJ_zCDyzNItB2KvqmkK63YrQjpPQdsb-T8mC6RT-oBmRyTsmkghuwAOgVUOZmOc18-0ERALRfAIAMXD"

        @BeforeClass
        @JvmStatic
        fun setup() {
            server = MockWebServer()
            val dispatcher1 = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    if (request.method == "POST") {
                        when(request.path) {
                            "/cgi-bin/component/api_component_token" -> {
                                return MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody("""
                           {    "expires_in": 7200,
                                "component_access_token": "$acceptComponentToken"
                            }
                        """.trimIndent())
                            }
                        }
                    }
                    return MockResponse().setResponseCode(404)
                }
            }

            server.setDispatcher(dispatcher1)

            server.start(9800)

        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            server.shutdown()
        }

    }

    @Test
    fun testGetTicket() {
        // 解析出密文
        val encryptData = AppHelper.fromXml<EncryptMsg1>(testXml)
        // 验证签名
        val signature = SHA1.getSHA1(Config.WX3rdAppToken, timestamp, nonce, encryptData.encrypt)
        Assert.assertEquals(signature, msgSignature)
        // 解析出 ticket
        AppHelper.getInfoTypeFromEncrypt(encryptData.encrypt)
        //val ticket = AppHelper.decrypt<ComponentVerifyTicket>(encryptData.encrypt)
        // Assert.assertEquals(ticket.componentVerifyTicket, acceptTicket)
    }

    @Test
    fun testComponentToken() {
        val componentApi = WXComponentClient().build(WorkModeMock)
        val token = componentApi.apiComponentToken(
                componentAppId = "wxb3d033d520d15fe7",
                componentAppSecret = Config.WX3rdAppSecret,
                verifyTicket = acceptTicket
        )
        Assert.assertEquals(token.componentAccessToken, acceptComponentToken)
        val request = server.takeRequest()
        Assert.assertEquals(request.path, "/cgi-bin/component/api_component_token")
    }
}