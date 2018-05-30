package com.xiaozi.appstore.manager

import android.app.Activity
import cc.fish.cld_ctrl.common.util.AppUtils
import cc.fish.fishhttp.net.RequestHelper
import cc.fish.fishhttp.util.MD5Utils
import com.google.gson.reflect.TypeToken
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.component.Device
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.component.GlobalData
import com.xiaozi.appstore.manager.NetManager.UrlPassNullParam
import com.xiaozi.appstore.plugin._CLIENT_ID
import com.xiaozi.appstore.plugin._DEBUG
import com.xiaozi.appstore.plugin._KEY
import java.io.Serializable

/**
 * Created by fish on 18-1-2.
 */
object NetManager {

    val SUCCESS_CODE = 0
    private val _TEST_URL = "http://tapi.yz070.com/v1"
    //    private val _TEST_URL = "http://222.128.15.95:18079/v1"
    private val _PRODUCT_URL = "http://api.yz070.com/v1"
    private val MAIN_URL = if (_DEBUG) _TEST_URL else _PRODUCT_URL

    inline fun <reified T> fastCall(url: String?, crossinline success: T.() -> Unit = {}, crossinline failed: String.() -> Unit = {}) {
        if (url == null || url.isBlank()) return
        createOri<T>(url, success, failed).get(Framework._C, Framework._H)
    }

    inline fun <reified T> fastCallBaseResp(url: String, crossinline success: T.() -> Unit = {}, crossinline failed: String.() -> Unit = {}) = createBase(url, success, failed).get(Framework._C, Framework._H)

    inline fun <reified T> createBase(url: String, crossinline success: T.() -> Unit, crossinline failed: String.() -> Unit) = RequestHelper<BaseResp<T>?>().apply {
        Url(url)
        Method(RequestHelper.Method.GET)
        System.currentTimeMillis().apply {
            UrlParam("timestamp", "${this}", true)
            UrlParam("apiToken", MD5Utils.md5Encrypt("$_CLIENT_ID$_KEY$this"))
        }
        UrlParam("androidId", Device.getAndroidID())
        UrlParam("clientVersion", "${AppUtils.getVersionCode(Framework._C)}")
        UrlParam("deviceModel", Device.DEVICE_NAME)
        UrlParam("imei", Device.getIMEI())
        UrlParam("mac", Device.getMacAddr())
        UrlParam("osVersion", Device.OS_VERSION)
        UrlParam("ip", Device.getIP())
        UrlParam("pkgName", Framework._C.packageName)
        HeadPassNullParam("userToken", AccountManager.token())
        ResultType(object : TypeToken<BaseResp<T>>() {})
        Success {
            if (it == null)
                "null response".failed()
            else if (it.code == SUCCESS_CODE)
                it.data.success()
            else
                it.msg.failed()
        }
        Failed { it.failed() }
    }

    inline fun <reified T> createOri(url: String, crossinline success: T.() -> Unit, crossinline failed: String.() -> Unit) = RequestHelper<T?>().apply {
        Url(url)
        UrlParam("ts", "${System.currentTimeMillis()}", true)
        ResultType(object : TypeToken<T?>() {})
        Method(RequestHelper.Method.GET)
        Success {
            if (it == null)
                "".failed()
            else
                it.success()
        }
        Failed { it.failed() }
    }


    /**APIS **************/

    fun loadAppConfDry() = loadAppConfig(null) {}

    fun loadAppConfig(activity: Activity?, success: () -> Unit) {
        createBase<RespAppConf>("$MAIN_URL/cconfig", {
            GlobalData.storeAppConfig(this)
            success()
        }) {
            activity?.ZToast(this)
        }.ResultType(object : TypeToken<BaseResp<RespAppConf>>() {}).get(Framework._C, Framework._H)
    }

    fun loadAppList(type: String = AppListType.ALL.str, condition: String, keyword: String = "", index: Int = 0, success: RespAppList.() -> Unit, failed: String.() -> Unit) {
        createBase<RespAppList>("$MAIN_URL/app/list", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlPassNullParam("adsType", type)
                .UrlParam("condition", condition)
                .UrlPassNullParam("keyword", keyword)
                .UrlParam("number", "20")
                .UrlParam("start", "${index + 1}")
                .ResultType(object : TypeToken<BaseResp<RespAppList>>() {}).get(Framework._C, Framework._H)
    }

    fun loadAssociateApps(appId: String, success: RespAppList.() -> Unit, failed: String.() -> Unit) {
        createBase<RespAppList>("$MAIN_URL/app/wdjlist", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("associatedAppId", appId)
                .UrlParam("condition", "associate")
                .ResultType(object : TypeToken<BaseResp<RespAppList>>() {}).get(Framework._C, Framework._H)
    }

    fun loadCommentList(appId: Int, index: Int = 0, success: RespCommentList.() -> Unit, failed: String.() -> Unit) {
        createBase<RespCommentList>("$MAIN_URL/comment/list", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("appId", "$appId")
                .UrlParam("number", "20")
                .UrlParam("start", "${index + 1}")
                .UrlPassNullParam("userId", "${AccountManager.uid()}")
                .ResultType(object : TypeToken<BaseResp<RespCommentList>>() {}).get(Framework._C, Framework._H)
    }

    fun loadAppDetail(appId: Int, success: RespAppInfo.() -> Unit, failed: String.() -> Unit) {
        createBase<RespAppInfo>("$MAIN_URL/app/info", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("appId", "$appId")
                .ResultType(object : TypeToken<BaseResp<RespAppInfo>>() {}).get(Framework._C, Framework._H)
    }

    fun loadUserInfo(activity: Activity, userId: Int, success: () -> Unit) {
        createBase<RespUserInfo>("$MAIN_URL/user/getinfo", {
            AccountManager.let {
                it.userHeadIcon = userInfo.userImageUrl
                it.userName = userInfo.userName
            }
            success()
        }, activity::ZToast)
                .Method(RequestHelper.Method.GET)
                .UrlParam("userId", "$userId")
                .ResultType(object : TypeToken<BaseResp<RespUserInfo>>() {}).get(Framework._C, Framework._H)
    }

    fun loadBanners(success: RespBanners.() -> Unit, failed: String.() -> Unit) {
        createBase<RespBanners>("$MAIN_URL/ad", success, failed)
                .Method(RequestHelper.Method.GET)
                .ResultType(object : TypeToken<BaseResp<RespBanners>>() {}).get(Framework._C, Framework._H)
    }

    fun loadHotWords(success: RespHots.() -> Unit) {
        createBase<RespHots>("$MAIN_URL/topsearchkw", success, {})
                .Method(RequestHelper.Method.GET)
                .ResultType(object : TypeToken<BaseResp<RespHots>>() {}).get(Framework._C, Framework._H)
    }

    fun applyComment(appId: Int, commentTxt: String, point: Int, userId: Int, userName: String, success: Any?.() -> Unit, failed: String.() -> Unit) {
        createBase<Any?>("$MAIN_URL/comment/add", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("appId", "$appId")
                .UrlParam("commentTxt", commentTxt)
                .UrlParam("point", "$point")
                .UrlParam("userId", "$userId")
                .UrlParam("userName", userName)
                .ResultType(object : TypeToken<BaseResp<Any?>>() {}).get(Framework._C, Framework._H)
    }

    fun applyFeedback(content: String, success: Any?.() -> Unit, failed: String.() -> Unit) {
        createBase<Any?>("$MAIN_URL/feedback", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("advise", content)
                .UrlParam("email", "")
                .UrlParam("qq", "")
                .UrlParam("userId", "")
                .ResultType(object : TypeToken<BaseResp<Any?>>() {}).get(Framework._C, Framework._H)
    }

    fun applyThumbsup(appId: Int, commentId: Int, userId: Int, isApply: Boolean, success: RespThumb?.() -> Unit, failed: String.() -> Unit) {
        createBase<RespThumb>("$MAIN_URL/comment/thumbup", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("appId", "$appId")
                .UrlParam("beThumbsup", if (isApply) "1" else "0")
                .UrlParam("commentId", "$commentId")
                .UrlParam("userId", "$userId")
                .ResultType(object : TypeToken<BaseResp<RespThumb>>() {}).get(Framework._C, Framework._H)
    }

    fun login(openId: String, unionId: String, userName: String, imgUrl: String, success: RespLoginInfo.() -> Unit, failed: String.() -> Unit) {
        createBase<RespLoginInfo>("$MAIN_URL/user/login/wechat", success, failed)
                .Method(RequestHelper.Method.GET)
                .UrlParam("openId", openId)
                .UrlParam("unionId", unionId)
                .UrlParam("userName", userName)
                .UrlParam("userImageUrl", imgUrl)
                .ResultType(object : TypeToken<BaseResp<RespLoginInfo>>() {}).get(Framework._C, Framework._H)
    }

    fun callInstalledUpload(pkg: String) {
        createBase<Any?>("$MAIN_URL/app/checkdl", {}, {})
                .Method(RequestHelper.Method.GET)
                .UrlParam("packageName", pkg)
                .get(Framework._C, Framework._H)
    }

    fun RequestHelper<*>.UrlPassNullParam(key: String, value: String) = this.apply { if (value.isNotBlank()) UrlParam(key, value) }
    fun RequestHelper<*>.HeadPassNullParam(key: String, value: String) = this.apply { if (value.isNotBlank()) HeaderParam(key, value) }
}

enum class AppListType(val str: String) {
    ALL(""),
    APP("0"),
    GAME("1")
}

enum class AppCondition(val str: String) {
    HOT("hot"),
    TOP("top"),
    SEARCH("search"),
    ASSOCIATE("associate")
}

open class BaseResp<T>(var code: Int = 0, var msg: String = "", var data: T) : Serializable
data class RespAppConf(val configs: RespAppConfEntity) : Serializable
data class RespAppList(val appNodes: RespAppListEntity) : Serializable
data class RespAppInfo(val appinfo: RespAppInfoEntity) : Serializable
data class RespUserInfo(val userInfo: RespUserInfoEntity) : Serializable
data class RespCommentList(val comments: RespCommentListEntity) : Serializable
data class RespBanners(val banners: RespBannersEntity) : Serializable
data class RespLoginInfo(val user: RespLoginUserInfo) : Serializable
data class RespHots(val hotSearchWd: Array<String>) : Serializable
data class RespThumb(val thumbsup: RespThumbsup) : Serializable

data class RespAppConfEntity(val appClass: RespConfClz, val gameClass: RespConfClz, val aboutUs: String, val timeStamp: Long)
data class RespConfClz(val `class`: Array<RespAppClass>)
data class RespAppClass(val id: Int, val name: String, val imgUrl: String, val subclass: Array<RespClassSec>)
data class RespClassSec(val id: Int, val name: String)

data class RespAppListEntity(val node: Array<RespAppListInfo>, val number: Int, val start: Int)
data class RespAppListInfo(val tips: String, val appName: String, val downloadCount: Long, val iconUrl: String, val versionCode: Int,
                           val packageName: String, val size: Int, val sn: Int, val appId: Int, val downloadUrl: String,
                           val imprUrl: String?, val downloadStartUrl: String?, val downloadFinishUrl: String?, val installFinishUrl: String?)

data class RespAppListCallUrlsDEP(val imprUrl: String, val downloadStartUrl: String, val downloadFinishUrl: String, val installFinishUrl: String) : Serializable

data class RespAppInfoEntity(val adType: Int, val appName: String, val commentCount: Int, val appDesc: String,
                             val appId: Int, val downloadUrl: String, val iconUrl: String, val imageUrls: Array<String>,
                             val packageName: String, val point: Int, val size: Int, val tips: String, val updateLog: String)

data class RespLoginUserInfo(val unionId: String, val userId: Int, val userImageUrl: String, val userName: String)
data class RespUserInfoEntity(val userId: Int, val userImageUrl: String, val userName: String)

data class RespCommentListEntity(val node: Array<RespComment>, val number: Int, val start: Int, val total: Int)
data class RespComment(val authorName: String, val thumbsupSign: Int, val content: String, val date: Long, val thumbsupCount: Int, val commentId: Int, val authorImg: String)

data class RespBannersEntity(val banner: Array<RespBanner>)
data class RespBanner(val image: String, val link: String, val sn: Int)

data class RespThumbsup(val beThumbsup: Int, val commentId: Int, val userId: Int)
