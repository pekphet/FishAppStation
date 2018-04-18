package com.fengye.appstore.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cc.fish.fishhttp.net.RequestHelper
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.activity.fragments.MineFragment
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.manager.AccountManager
import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.plugin.LogFilePlugin
import com.xiaozi.appstore.wxapi.WXHelper
import com.xiaozi.appstore.wxapi.WXHelper.Companion.APP_ID
import com.xiaozi.appstore.wxapi.WXHelper.Companion.APP_SECRET

/**
 * Created by fish on 18-1-15.
 */
class WXEntryActivity : IWXAPIEventHandler, Activity() {
    override fun onReq(p0: BaseReq?) {
    }

    val URL_WX_ACCESS = "https://api.weixin.qq.com/sns/oauth2/access_token"
    val URL_WX_USERINFO = "https://api.weixin.qq.com/sns/userinfo"

    var api: IWXAPI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, APP_ID)
        api?.handleIntent(getIntent(), this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }

    override fun onResp(resp: BaseResp?) {
        LogFilePlugin.RLog("WX RESP", "${resp?.type}")
        when (resp?.type) {
            ConstantsAPI.COMMAND_SENDAUTH -> {
                when (resp.errCode) {
                    BaseResp.ErrCode.ERR_OK -> loadAccess((resp as? SendAuth.Resp)?.code)   //success
                    BaseResp.ErrCode.ERR_USER_CANCEL -> ZToast("用户取消")
                    BaseResp.ErrCode.ERR_AUTH_DENIED -> ZToast("认证被拒绝")
                    BaseResp.ErrCode.ERR_SENT_FAILED -> ZToast("发送登录信息失败")
                    BaseResp.ErrCode.ERR_UNSUPPORT -> ZToast("微信无法支持此机型")
                    else -> ZToast("未知错误")
                }
                finish()
            }
            ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> {
                val succ = resp.errCode == BaseResp.ErrCode.ERR_OK
                ZToast(if (succ) "分享成功" else "分享失败")
                finish()
            }
            else -> {
                ZToast("${resp?.type ?: "none"} -> ${resp?.errStr ?: "err no msg"}")
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WXHelper.isLoggingIn = false
    }

    private fun loadAccess(code: String?) {
        RequestHelper<WXAccessResp>().Success { access -> loadWXUserInfo(access) }
                .Url(URL_WX_ACCESS)
                .Method(RequestHelper.Method.GET)
                .Result(WXAccessResp::class.java)
                .UrlParam("appid", APP_ID, true)
                .UrlParam("secret", APP_SECRET)
                .UrlParam("code", code)
                .UrlParam("grant_type", "authorization_code")
                .get(Framework._C, Framework._H)

    }

    private fun loadWXUserInfo(access: WXAccessResp) {
        RequestHelper<WXUserInfo>().Success {
            NetManager.login(it.openid, it.unionid, it.nickname, it.headimgurl, {
                AccountManager.apply {
                    storeToken(user.unionId, user.userId)
                    userHeadIcon = user.userImageUrl
                    userName = user.userName
                    MineFragment.EventPoster.notifyObs()
                    ZToast("登录成功")
                }
            }) {this@WXEntryActivity::ZToast}
        }.Url(URL_WX_USERINFO)
                .Method(RequestHelper.Method.GET)
                .Result(WXUserInfo::class.java)
                .UrlParam("access_token", access.access_token, true)
                .UrlParam("openid", access.open_id)
                .get(Framework._C, Framework._H)

    }

}

data class WXAccessResp(val access_token: String, val open_id: String, val unionid: String)
data class WXUserInfo(val nickname: String, val openid: String, val headimgurl: String, val unionid: String)
