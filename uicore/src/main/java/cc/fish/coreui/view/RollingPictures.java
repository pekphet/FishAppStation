package cc.fish.coreui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cc.fish.coreui.R;

/**
 * Created by fish on 16-5-4.
 */
public class RollingPictures {
    private static final int RES_POINT_CHECKED = R.drawable.white_point_50;
    private static final int RES_POINT_UNCHECKED = R.drawable.gray_point_50;
    private static final int ROLL_DELAY = 5000;
    private Context mContext;
    private View mView;
    ArrayList<ImageView> imgViews;
    private ImagePagerAdapter mAdapter;
    private LinearLayout llRepo;
    private ViewPager mVp;

    public RollingPictures(Context context) {
        mContext = context;
    }

    public RollingPictures setImgViews(ArrayList<ImageView> imgViews) {
        this.imgViews = imgViews;
        mAdapter = new ImagePagerAdapter(imgViews);
        initView();
        changePoint(0);
        return this;
    }

    public View getView() {
        roll();
        return mView;
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.rolling_pictures, null);
        mVp = (ViewPager) mView.findViewById(R.id.vp_roller);
        llRepo = (LinearLayout) mView.findViewById(R.id.ll_point_repo);
        mAdapter = new ImagePagerAdapter(imgViews);
        mVp.setAdapter(mAdapter);
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                
            }

            @Override
            public void onPageSelected(int position) {
                changePoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initPoints();
    }

    private void initPoints() {
        for (ImageView img : imgViews) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.point_image, null);
            ImageView point = (ImageView) v.findViewById(R.id.img_point);
            llRepo.addView(v);
        }
    }

    private void changePoint(int position) {
        for(int i = 0; i < llRepo.getChildCount(); i++) {
            View v = llRepo.getChildAt(i);
            ImageView img = (ImageView) v.findViewById(R.id.img_point);
            img.setImageResource(i == position ? RES_POINT_CHECKED : RES_POINT_UNCHECKED);
        }
    }

    private Handler h = new Handler(Looper.getMainLooper());
    private void roll() {
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (h != null) {
                    mVp.setCurrentItem((mVp.getCurrentItem() + 1)%imgViews.size());
                    roll();
                }
            }
        }, ROLL_DELAY);
    }

}
