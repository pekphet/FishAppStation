package cc.fish.cld_ctrl.ad2.view;

import android.content.Context;
import android.net.http.SslError;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cc.fish.cld_ctrl.R;
import cc.fish.cld_ctrl.ad.view.AdWebView;
import cc.fish.cld_ctrl.ad2.entity.Ad2RespEntity;
import cc.fish.cld_ctrl.common.net.NetManager;
import cc.fish.cld_ctrl.common.util.DownloadUtils;

/**
 * Created by fish on 17-1-18.
 */

public class ViewParser {

    private static LayoutInflater inflater;
    private static Context sContext;
    private static FrameLayout.LayoutParams sParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


    public static void init(Context c) {
        inflater = LayoutInflater.from(NetManager.mContext);
        sContext = c;
    }


    public static View parseAd(Ad2RespEntity e) {
        /*
        1   TEXT        纯文本类型，一般由title、description组成
        2   IMAGE       纯图片类型，一般由单张或者多张image组成
        3   TEXTICON    图文类型，一般由单张icon和title、description共同组成
        4   HTML        HTML类型，可直接使用html_snippet进行展示
        5   VIDEO       视频类型，目前尚不支持
        6   NATIVE      原生类型，可能包含title、description、sub_description、image、icon、logo、datas字段的任意组合
         */
        switch (e.getType()) {
            case 1:
                return getTextAdView(e);
            case 2:
                return getImageAdView(e);
            case 3:
                return getTextImgAdView(e);
            case 4:
                return getHTMLView(e);
            case 6:
                throw new IllegalArgumentException("type 6 is native view!!!");
        }
        return null;
    }

    private static View getHTMLView(final Ad2RespEntity e) {
        final WebView wb = new WebView(sContext);
        wb.setLayoutParams(sParam);
        wb.loadUrl(e.getHtml_url());
        wb.setBackgroundColor(0x00ffffff);
        WebSettings settings = wb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setAllowFileAccess(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setBlockNetworkImage(false);
//        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        settings.setSupportMultipleWindows(true);
//        settings.setAllowContentAccess(true);
//        settings.setDomStorageEnabled(true);
        wb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                AdWebView.startAdWebView(sContext, url);
                NetManager.getInstance().simple(e.getClk_trackers()[0]);
                wb.loadUrl("about:blank");
                return false;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        wb.setFocusable(false);
        wb.setClickable(false);
        wb.setVerticalScrollBarEnabled(false);
        wb.setScrollContainer(false);
        return wb;
    }

    private static View getTextImgAdView(Ad2RespEntity e) {
        View v = inflater.inflate(R.layout.ad2_v_2, null);
        ImageView img = (ImageView) v.findViewById(R.id.img_ad2_v2_img);
        TextView tvT = (TextView) v.findViewById(R.id.tv_ad2_v2_title);
        TextView tvS = (TextView) v.findViewById(R.id.tv_ad2_v2_sub);
        ImageLoader.getInstance().displayImage(e.getIcons()[0], img);
        tvT.setText(e.getTitle());
        tvS.setText(e.getDescription());
        addEffects(v, e);
        return v;
    }

    private static View getImageAdView(Ad2RespEntity e) {
        ImageView img = new ImageView(sContext);
        img.setScaleType(ImageView.ScaleType.CENTER);
        ImageLoader.getInstance().displayImage(e.getImages()[0], img);
        addEffects(img, e);
        return img;
    }

    private static View getTextAdView(Ad2RespEntity e) {
        View v = inflater.inflate(R.layout.ad2_v_1, null);
        TextView tvT = (TextView) v.findViewById(R.id.tv_ad2_v1_title);
        TextView tvS = (TextView) v.findViewById(R.id.tv_ad2_v1_sub);
        tvT.setText(e.getTitle());
        tvS.setText(e.getDescription());
        addEffects(v, e);
        return v;
    }

    private static void addEffects(View v, final Ad2RespEntity e) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                NetManager.getInstance().simple(e.getClk_trackers()[0]);
                switch (e.getAct_type()) {
                    case 1:
                        AdWebView.startAdWebView(sContext, e.getClick_url());
                        break;
                    case 2:
                        DownloadUtils.startDownService(sContext, e.getClick_url(), System.currentTimeMillis() + ".apk");
                        break;
                }
            }
        });
    }


}
