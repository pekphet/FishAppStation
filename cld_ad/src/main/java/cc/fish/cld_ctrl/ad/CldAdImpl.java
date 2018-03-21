package cc.fish.cld_ctrl.ad;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import cc.fish.cld_ctrl.ad.entity.AdDeviceInfo;
import cc.fish.cld_ctrl.ad.entity.AdDisp;
import cc.fish.cld_ctrl.ad.entity.RequestAd;
import cc.fish.cld_ctrl.ad.entity.ResponseAd;
import cc.fish.cld_ctrl.ad.entity.enums.AdType;
import cc.fish.cld_ctrl.ad.view.AdWebView;
import cc.fish.cld_ctrl.ad.view.Parser;
import cc.fish.cld_ctrl.common.net.NetCallback;
import cc.fish.cld_ctrl.common.net.NetManager;
import cc.fish.cld_ctrl.common.util.AppUtils;
import cc.fish.cld_ctrl.common.util.DeviceUtils;
import cc.fish.cld_ctrl.common.util.DownloadUtils;
import cc.fish.cld_ctrl.common.util.EntityUtils;
import cc.fish.fishhttp.util.ZLog;

/**
 * Created by fish on 16-12-14.
 */

public class CldAdImpl {

    private static AdDeviceInfo sDeviceInfo;
    private static RequestAd sRequestAd;
    private static Context sAppContext;

    public static void initDeviceInfo(Context applicationContext) {
        sAppContext = applicationContext;
        sRequestAd = new RequestAd();
        sDeviceInfo = DeviceUtils.getAdDeviceInfo(applicationContext);
        sRequestAd.setDevice_info(sDeviceInfo);
        sRequestAd.setVid(DeviceUtils.getImei(applicationContext));
        sRequestAd.setApp_id(AppUtils.getMetaAppId(applicationContext));
        sRequestAd.setChannel(AppUtils.getMetaChannel(applicationContext));
    }

    public static void show(AdType type, int ad_slot, final FrameLayout targetView, final Context context) {
        RequestAd requestAd = (RequestAd) EntityUtils.copy(sRequestAd, RequestAd.class);
        requestAd.setAd_slot(ad_slot);
        requestAd.setAd_type(type);
        NetManager.getInstance().syncAd(requestAd, new NetCallback<ResponseAd>() {
            @Override
            public void success(ResponseAd result) {
                switch (result.getAd_type()) {
                    case AreaWebAd:
                        if (targetView != null) {
                            targetView.removeAllViews();
                        }
                        targetView.addView(Parser.parseWebView(targetView, result, context));
                        break;
                    case DialogAd:
                        break;
                    case FullScreenAd:
                        break;
                    case InfoFlowAd:
                        break;
                    case LocateAd:
                        break;
                    case ScrollAd:
                        break;
                    case SuspendAd:
                        break;
                    default:
                        return;
                }
                uploadShow(result.getApp_ad_id());
                setClickAction(result, targetView, context);
            }

            @Override
            public void failed(String msg) {
                ZLog.e("sync ad", msg);
            }

            @Override
            public void noDisp() {
            }
        });
    }

    public static void show(AdType type, int ad_slot, final AdCallback<AdDisp> callback, final Context context) {
        RequestAd requestAd = (RequestAd) EntityUtils.copy(sRequestAd, RequestAd.class);
        requestAd.setAd_slot(ad_slot);
        requestAd.setAd_type(type);
        NetManager.getInstance().syncAd(requestAd, new NetCallback<ResponseAd>() {
            @Override
            public void success(ResponseAd result) {
                setClickAction(result, callback.success(result.getAd_disp(), result.getApp_ad_id()), context);
                uploadShow(result.getApp_ad_id());
            }

            @Override
            public void failed(String msg) {
                ZLog.e("sync ad define", msg);
            }

            @Override
            public void noDisp() {
                callback.noDisplay();
            }
        });
    }

    private static void setClickAction(final ResponseAd result, View targetView, final Context context) {
        switch (result.getAd_action()) {
            case Web:
                targetView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdWebView.startAdWebView(context, result.getLink_url(), result.getApp_ad_id());
                    }
                });
                break;
            case Download:
                targetView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadClick(result.getApp_ad_id());
                        DownloadUtils.startDownService(context, result.getLink_url(), result.getApp_ad_id() + "");
                    }
                });
                break;
            case AutoLoad:
                break;
            case OuterWeb:
                break;
            default:
                break;
        }
    }

    public static void uploadShow(int app_ad_id) {
        NetManager.getInstance().uploadShowAd(app_ad_id);
    }

    public static void uploadClick(int app_ad_id) {
        NetManager.getInstance().uploadClickAd(app_ad_id);
    }

    public static RequestAd getRequestAd() {
        return sRequestAd;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

}
