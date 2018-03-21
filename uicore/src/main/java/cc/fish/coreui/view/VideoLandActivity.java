package cc.fish.coreui.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import cc.fish.coreui.R;

/**
 * Created by fish on 16-5-4.
 */
public class VideoLandActivity extends Activity {

    private StandardWebView mWebview;
    final static public String OPEN_URL = "url";
    private String mUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_video);
        mWebview = (StandardWebView) findViewById(R.id.web_video);
        mUrl = getIntent().getExtras().getString(OPEN_URL, "");
        initWebview();
    }

    private void initWebview() {
        mWebview.loadUrl(mUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebview.canGoBack()) {
                mWebview.goBack();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebview.onResume();
    }
}
