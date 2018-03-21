package cc.fish.cld_ctrl.ad.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by fish on 16-12-14.
 */

public class AdWebView extends Activity {

    private WebView mWebview;
    final private static String EXTRA_URL = "URL";
    final private static String EXTRA_AD_SLOT = "AD_SLOT";

    public static void startAdWebView(Context context, String url, int app_ad_id) {
        Intent intent = new Intent(context, AdWebView.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_AD_SLOT, app_ad_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void startAdWebView(Context context, String url) {
        Intent intent = new Intent(context, AdWebView.class);
        intent.putExtra(EXTRA_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebView();
//        CldAdImpl.uploadClick(getIntent().getIntExtra(EXTRA_AD_SLOT, -1));
    }

    private void initWebView() {
        mWebview = new WebView(this);


        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        setContentView(mWebview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url != null && !url.equals("")) {
            mWebview.loadUrl(url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
