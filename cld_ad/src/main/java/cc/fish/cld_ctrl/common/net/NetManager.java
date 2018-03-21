package cc.fish.cld_ctrl.common.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;

import cc.fish.cld_ctrl.ad.entity.AdFeedbackReq;
import cc.fish.cld_ctrl.ad.entity.BaseResponse;
import cc.fish.cld_ctrl.ad.entity.RequestAd;
import cc.fish.cld_ctrl.ad.entity.ResponseAd;
import cc.fish.cld_ctrl.ad.entity.enums.AllResponseAd;
import cc.fish.cld_ctrl.ad2.entity.Ad2RespE;
import cc.fish.cld_ctrl.ad2.entity.Ad2RespEntity;
import cc.fish.cld_ctrl.ad2.entity.AdReqEntity;
import cc.fish.cld_ctrl.appstate.entity.AllRespUpdate;
import cc.fish.cld_ctrl.appstate.entity.ReqFeedback;
import cc.fish.cld_ctrl.appstate.entity.RespUpdate;
import cc.fish.cld_ctrl.appstate.interfaces.UpdateCallback;
import cc.fish.cld_ctrl.common.util.DeviceUtils;
import cc.fish.fishhttp.net.RequestHelper;
import cc.fish.fishhttp.net.annotation.NetInject;
import cc.fish.fishhttp.net.annotation.NetMethod;
import cc.fish.fishhttp.net.annotation.NetUrl;
import cc.fish.fishhttp.net.annotation.Result;
import cc.fish.fishhttp.thread.Done;
import cc.fish.fishhttp.util.ZLog;

/**
 * Created by fish on 16-12-13.
 */

public class NetManager {
    private static NetManager instance = null;
    public static Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private NetManager() {
        NetInject.inject(this);
    }

    public static NetManager getInstance() {
        if (instance == null) {
            instance = new NetManager();
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
    }

    /*************************************************/

    final static boolean NET_TEST = false;
    final static String RESP_R_SUCCESS = "SUCCESS";
    final static String URL_MAIN = NET_TEST ? "http://yz069.com/gw/v1"
            : "http://cloud.xiaozi.mobi/v1";
    final static String URL_AD = URL_MAIN + "/ad/req";
    final static String URL_AD_SHOW = URL_MAIN + "/ad/effect";
    final static String URL_AD_CLICK = URL_MAIN + "/ad/effect";

    final static String URL_APP_UPDATE = URL_MAIN + "/app/upd";
    final static String URL_APP_FEEDBACK = URL_MAIN + "/app/feedback";

    final static String PARAM_VID = "vid";
    final static String PARAM_AD_ID = "app_ad_id";
    final static String PARAM_APP_ID = "app_id";
    final static String PARAM_VER_CODE = "version_code";
    final static String PARAM_CHANNEL = "channel";


    @NetMethod(RequestHelper.Method.GET)
    @Result(AllRespUpdate.class)
    @NetUrl(URL_APP_UPDATE)
    private RequestHelper<AllRespUpdate> updateRequest = new RequestHelper<>();

    @NetMethod(RequestHelper.Method.POST)
    @Result(Object.class)
    @NetUrl(URL_APP_FEEDBACK)
    private RequestHelper<Object> feedbackRequest = new RequestHelper<>();


    private RequestHelper<Object> getFastRequestHelper() {
        RequestHelper<Object> fastRequest = new RequestHelper<>()
                .Method(RequestHelper.Method.POST)
                .Result(ResponseAd.class);
        return fastRequest;
    }

    private RequestHelper<Object> getSimpleRH() {
        RequestHelper<Object> smp = new RequestHelper<>()
                .Method(RequestHelper.Method.GET)
                .Result(BaseResponse.class);
        return smp;
    }

    private RequestHelper<AllResponseAd> getAdRequestHelper() {
        RequestHelper<AllResponseAd> adRequest = new RequestHelper<>()
                .Url(URL_AD)
                .Method(RequestHelper.Method.GET)
                .Result(AllResponseAd.class);
        return adRequest;
    }

    private RequestHelper<Ad2RespE> getAd2RequestHelper() {
        RequestHelper<Ad2RespE> adRequest = new RequestHelper<>()
                .Url(URL_AD)
                .Method(RequestHelper.Method.POST)
                .Result(Ad2RespE.class);
        return adRequest;
    }

    public void syncAd(RequestAd requestEntity, final NetCallback<ResponseAd> callback) {
        getAdRequestHelper().UrlParam("ad_slot", requestEntity.getAd_slot() + "", true)
                .UrlParam("vid", requestEntity.getVid().replace(" ", "_"))
                .UrlParam("channel", requestEntity.getChannel().replace(" ", "_"))
                .UrlParam("ad_type", requestEntity.getAd_type().toString())
                .UrlParam("app_id", requestEntity.getApp_id() + "")
                .UrlParam("brand", requestEntity.getDevice_info().getBrand().replace(" ", "_"))
                .UrlParam("model", requestEntity.getDevice_info().getModel())
                .UrlParam("osv", requestEntity.getDevice_info().getOsv())
                .UrlParam("bright", requestEntity.getDevice_info().getBright() + "")
                .UrlParam("lat", requestEntity.getDevice_info().getLat() + "")
                .UrlParam("lnt", requestEntity.getDevice_info().getLnt() + "")
                .Success(new Done<AllResponseAd>() {
                    @Override
                    public void run(AllResponseAd result) {
                        if (result == null || result.getBody() == null || result.getBody().getAd_disp() == null) {
                            callback.noDisp();
                        } else {
                            callback.success(result.getBody());
                        }
                    }
                })
                .Failed(new Done<String>() {
                    @Override
                    public void run(String msg) {
                        ZLog.e("NET->SYNC_AD", msg);
                        callback.failed(msg);
                    }
                }).get(mContext, mHandler);
    }

    public void uploadShowAd(int app_ad_id) {
        getFastRequestHelper().Url(URL_AD_SHOW)
                .PostJson(new AdFeedbackReq("show", app_ad_id))
                .post(mContext, mHandler);
    }

    public void uploadClickAd(int app_ad_id) {
        getFastRequestHelper().Url(URL_AD_CLICK)
                .PostJson(new AdFeedbackReq("click", app_ad_id))
                .post(mContext, mHandler);
    }

    //API V2

    public void sync2Ad(final NetCallback<Ad2RespEntity> callback, AdReqEntity entity) {
        getAd2RequestHelper().PostJson(entity)
                .Success(new Done<Ad2RespE>() {
                    @Override
                    public void run(Ad2RespE result) {
                        if (result == null || result.getBody() == null || result.getBody().length <= 0) {
                            callback.noDisp();
                        } else if (result.getResult().equals(RESP_R_SUCCESS)) {
                            callback.success(result.getBody()[0]);
                        } else {
                            callback.failed(result.getMessage());
                        }
                    }
                })
                .Failed(new Done<String>() {
                    @Override
                    public void run(String s) {
                        callback.failed((String) s);
                    }
                }).post(mContext, mHandler);
    }

    public void simple(String url) {
        if (TextUtils.isEmpty(url)) return;
        getSimpleRH().Url(url).get(mContext, mHandler);
    }

    /****App Part!!!!!!!!*******************************************************************************/
    public void checkUpdate(String app_id, int ver_code, String channel, final UpdateCallback callback) {
        updateRequest.UrlParam(PARAM_APP_ID, app_id + "", true)
                .UrlParam(PARAM_VER_CODE, ver_code + "")
                .UrlParam(PARAM_CHANNEL, channel)
                .Success(new Done<AllRespUpdate>() {
                    @Override
                    public void run(AllRespUpdate result) {
                        if ((result.getBody()) == null) {
                            return;
                        }
                        RespUpdate ru = result.getBody();
                        if (ru == null) {
                            return;
                        }
                        callback.update(ru.getDownload_url(),
                                ru.getIs_force() == 1,
                                ru.getContent(),
                                ru.getVersion_name(),
                                ru.getVersion_code());
                    }
                })
                .Failed(new Done<String>() {
                    @Override
                    public void run(String msg) {
                        ZLog.e("cld app update", msg);
                    }
                })
                .get(mContext, mHandler);
    }

    public void checkUpdate(String app_id, int ver_code, String channel, final Done<String> callback) {
        updateRequest.UrlParam(PARAM_APP_ID, app_id, true)
                .UrlParam(PARAM_VER_CODE, ver_code + "")
                .UrlParam(PARAM_CHANNEL, channel)
                .Success(new Done<AllRespUpdate>() {
                    @Override
                    public void run(AllRespUpdate result) {
                        RespUpdate ru = result.getBody();
                        if (ru == null || ru.getVersion_code() == 0) {
                            callback.run(null);
                        } else {
                            callback.run(new Gson().toJson(ru));
                        }
                    }
                })
                .Failed(new Done<String>() {
                    @Override
                    public void run(String msg) {
                        callback.run(null);
                    }
                })
                .get(mContext, mHandler);
    }

    public void feedback(ReqFeedback reqFeedback) {
        feedbackRequest.UrlParam("app_id", reqFeedback.getApp_id() + "", true)
                .UrlParam("version_code", reqFeedback.getVersion_code() + "")
                .UrlParam("channel", reqFeedback.getChannel())
                .PostJson(reqFeedback).post(mContext, mHandler);
    }

    synchronized public void upload(Context context, String token, UploadAction action, UploadFrom from, String uid, String packageName, String ext) {
        upload(context, token, action, from, uid, packageName, ext, true);
    }

    synchronized public void upload(Context context, String token, UploadAction action, UploadFrom from, String uid, String packageName, String ext, boolean encrypt) {
        upload(context, token, action.name(), from.name(), uid, packageName, ext, encrypt);
    }

    synchronized public void upload(Context context, String token, String action, String from, String uid, String packageName, String ext, boolean encrypt) {
        RequestHelper<Object> req = new RequestHelper<>()
                .Url("https://phenix.adyouzi.net/v1/app/upload");
//                .Url("http://phenix.adyouzi.net/v1/app/upload");
//                .Url("http://101.201.28.127:29604/v1/app/upload");
        req.Method(RequestHelper.Method.POST).Result(Object.class)
                .HeaderParam("Authorization", token)
                .PostParam("imei", DeviceUtils.getImei(context), true)
                .PostParam("action", action)
                .PostParam("from", from)
                .PostParam("uid", uid)
                .PostParam("package_name", packageName)
                .PostParam("ext", ext.replace('&', '-'))
                .PostParam("mac", "")
                .EncryptSortedParam()
                .post(context, mHandler);
    }

    public enum UploadAction {
        //        app_start_download,
//        app_finished_download,
        app_finished_install,
        error,
    }

    public enum UploadFrom {
        wdj,
        self,
        log,
    }
}
