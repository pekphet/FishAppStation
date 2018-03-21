package cc.fish.cld_ctrl.ad.view;

import android.content.Context;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cc.fish.cld_ctrl.ad.entity.ResponseAd;

/**
 * Created by fish on 16-12-14.
 */

public class Parser {
    public static View parseWebView(View targetView, final ResponseAd ad, final Context c) {
        final WebView wb = new WebView(c);
        wb.setLayoutParams(targetView.getLayoutParams());
        wb.loadUrl(ad.getAd_disp().getContent_url());
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
        wb.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                AdWebView.startAdWebView(c, url, ad.getApp_ad_id());
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
}
