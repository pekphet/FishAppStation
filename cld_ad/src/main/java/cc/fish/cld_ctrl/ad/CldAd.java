package cc.fish.cld_ctrl.ad;

import android.content.Context;
import android.widget.FrameLayout;

import cc.fish.cld_ctrl.ad.entity.AdDisp;
import cc.fish.cld_ctrl.ad.entity.enums.AdType;
import cc.fish.cld_ctrl.ad2.view.ViewParser;
import cc.fish.cld_ctrl.common.net.NetManager;
import cc.fish.cld_ctrl.common.util.DispUtils;

/**
 * Created by fish on 16-12-13.
 */

public class CldAd {

    public static void init(Context applicationContext) {
        NetManager.getInstance().init(applicationContext);
        CldAdImpl.initDeviceInfo(applicationContext);
        DispUtils.initImageLoader(applicationContext);
        ViewParser.init(applicationContext);
    }

    public static void AsyncShow(AdType type, int ad_slot, FrameLayout targetView, Context context) {
        CldAdImpl.show(type, ad_slot, targetView, context);
    }

    public static void DefineShow(AdType type, int ad_slot, Context context, AdCallback<AdDisp> callback) {
        CldAdImpl.show(type, ad_slot, callback, context);
    }

}
