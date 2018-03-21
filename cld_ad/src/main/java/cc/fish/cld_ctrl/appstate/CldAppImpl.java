package cc.fish.cld_ctrl.appstate;

import cc.fish.cld_ctrl.ad.CldAdImpl;
import cc.fish.cld_ctrl.appstate.entity.ReqFeedback;
import cc.fish.cld_ctrl.appstate.interfaces.UpdateCallback;
import cc.fish.cld_ctrl.common.net.NetManager;
import cc.fish.cld_ctrl.common.util.AppUtils;
import cc.fish.cld_ctrl.common.util.DownloadUtils;
import cc.fish.fishhttp.thread.Done;

/**
 * Created by fish on 16-12-15.
 */

public class CldAppImpl {
    public static void download(String url, String fileName) {
        DownloadUtils.startDownService(CldAdImpl.getAppContext(), url, fileName);
    }

    public static void checkUpdate(UpdateCallback callback) {
        NetManager.getInstance().checkUpdate(AppUtils.getMetaAppId(CldAdImpl.getAppContext()),
                AppUtils.getVersionCode(CldAdImpl.getAppContext()),
                AppUtils.getMetaChannel(CldAdImpl.getAppContext()), callback);
    }

    public static void checkUpdateForString(Done<String> callback) {
        NetManager.getInstance().checkUpdate(AppUtils.getMetaAppId(CldAdImpl.getAppContext()),
                AppUtils.getVersionCode(CldAdImpl.getAppContext()),
                AppUtils.getMetaChannel(CldAdImpl.getAppContext()), callback);
    }

    public static void feedback(String content) {
        ReqFeedback reqFb = new ReqFeedback();
        reqFb.setApp_id(AppUtils.getMetaAppId(CldAdImpl.getAppContext()));
        reqFb.setChannel(AppUtils.getMetaChannel(CldAdImpl.getAppContext()));
        reqFb.setContent(content);
        reqFb.setDevice(CldAdImpl.getRequestAd().getDevice_info());
        reqFb.setVersion_code(AppUtils.getVersionCode(CldAdImpl.getAppContext()));
        NetManager.getInstance().feedback(reqFb);
    }

}
