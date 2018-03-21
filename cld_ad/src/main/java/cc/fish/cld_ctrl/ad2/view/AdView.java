package cc.fish.cld_ctrl.ad2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.Toast;

import cc.fish.cld_ctrl.ad.CldAdImpl;
import cc.fish.cld_ctrl.ad2.entity.Ad2RespEntity;
import cc.fish.cld_ctrl.ad2.smart.DataMaker;
import cc.fish.cld_ctrl.common.net.NetCallback;
import cc.fish.cld_ctrl.common.net.NetManager;

/**
 * Created by fish on 17-2-6.
 */

public class AdView extends FrameLayout{
    private boolean lock = false;

    public AdView(Context context) {
        super(context);
    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void syncAd(int ad_slot) {
//        setBackgroundColor(0xffffcccc);
        if (lock) {
            return;
        }
        lock = true;
        this.removeAllViews();
        NetManager.getInstance().sync2Ad(new NetCallback<Ad2RespEntity>() {
            @Override
            public void success(Ad2RespEntity result) {
                setVisibility(VISIBLE);
                if (result.getImp_trackers() == null || result.getImp_trackers().length == 0) {
                    Toast.makeText(CldAdImpl.getAppContext(), "广告数据为空", Toast.LENGTH_SHORT).show();
                    lock = false;
                    return;
                }
                addView(ViewParser.parseAd(result));
                NetManager.getInstance().simple(result.getImp_trackers()[0]);
                lock = false;
            }

            @Override
            public void failed(String msg) {
                Toast.makeText(CldAdImpl.getAppContext(), "广告数据错误", Toast.LENGTH_SHORT).show();
                lock = false;
            }

            @Override
            public void noDisp() {
                setVisibility(GONE);
                lock = false;
            }
        }, DataMaker.getAdReq(ad_slot));
    }
}
