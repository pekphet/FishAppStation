package cc.fish.coreui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import cc.fish.coreui.annotation.Injector;

/**
 * Created by fish on 16-4-25.
 */
public class BaseSplashActivity extends Activity{
    final public static String  METHOD_SET_CONFIG = "setConfig";

    private int mDelay;
    private Class<Activity> mClz;

    private Handler h = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Injector.initSplash(this);
        super.onCreate(savedInstanceState);
        initImageLoader();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024))
                .memoryCacheSize(4 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(
                        new BaseImageDownloader(getApplicationContext(),
                                5 * 1000, 30 * 1000)).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }

    @Override
    protected void onStart() {
        super.onStart();
        goMainDelayed(mDelay);
    }

    public void setConfig(int delay, Class clz) {
        this.mDelay = delay;
        this.mClz = clz;
    }

    public void goMainDelayed(int delay) {
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(BaseSplashActivity.this, mClz));
                finish();
            }
        }, delay);

    }
}
