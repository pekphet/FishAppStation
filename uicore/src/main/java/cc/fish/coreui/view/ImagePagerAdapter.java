package cc.fish.coreui.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by fish on 16-5-4.
 */
public class ImagePagerAdapter extends PagerAdapter {
    private ArrayList<ImageView> viewlist;

    public ImagePagerAdapter(ArrayList<ImageView> viewlist) {
        this.viewlist = viewlist;
    }

    public ImagePagerAdapter(Context context, int[] resIds) {

    }

    @Override
    public int getCount() {
        return viewlist.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (viewlist.size() != 0) {
            int index = position % viewlist.size();
            container.removeView(viewlist.get(index));
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (viewlist.size() != 0) {
            int index = position % viewlist.size();
            try {
                if ((viewlist.get(index).getParent() == null)) {
                    container.addView(viewlist.get(index), ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                } else {
                    ((ViewGroup) viewlist.get(index).getParent()).removeView(viewlist.get(index));
                    container.addView(viewlist.get(index), ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            return viewlist.get(index);
        }
        return null;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
