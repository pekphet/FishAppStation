package cc.fish.coreui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import cc.fish.coreui.annotation.Injector;

/**
 * Created by fish on 16-4-25.
 */
abstract public class BaseFragmentActivity extends Activity {
    final public static String DECLARED_FIELD_DEFAULT_PAGE = "mDefaultPage";

    private Class<? extends BaseFragment>[] fCls = null;

    //fragment repository res id
    private int flMainId;
    //bottom buttons
    private LinearLayout llBottom = null;

    private BaseFragment[] fragments = null;

    private int mDefaultPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Injector.initFragmentActivity(this);
        super.onCreate(savedInstanceState);
        fCls = putFragments();
        fragments = new BaseFragment[fCls.length];
        flMainId = getFLid();
        llBottom = getBottomLayout();
        initBaseView();
        initView();
        setTabSel(llBottom.getChildAt(mDefaultPage), mDefaultPage);
    }

    private void initBaseView() {
        for (int i = 0; i < fCls.length; i++) {
            final int index = i;
            View v = getBottomItemView(index);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTabSel(v, index);
                }
            });
            llBottom.addView(v);
        }
    }


    protected void setTabSel(View item, int index) {
        onItemClick(item, index);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        for (int i = 0; i < fCls.length; i++) {
            checkAllBottomItem(llBottom.getChildAt(i), i, false);
            if (i == index) {
                checkAllBottomItem(llBottom.getChildAt(index), index, true);
                if (fragments[index] == null) {
                    try {
                        BaseFragment bf = fCls[index].newInstance();
                        fragments[index] = bf;
                        ft.add(flMainId, bf);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ft.show(fragments[index]);
                    fragments[index].onSelected();
                }
            } else if (fragments[i] != null) {
                ft.hide(fragments[i]);
            }
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * On action button click callback
     *
     * @param item  The clicked item
     * @param index The position
     */
    protected abstract void onItemClick(View item, int index);

    protected LayoutInflater getBottomLayoutInflater() {
        return LayoutInflater.from(this);
    }

    /**
     * Do operations after abstract methods called.
     * U can do onCreate after abstract methods called.
     */
    protected abstract void initView();

    /**
     * @return Array of Fragments'class
     */
    protected abstract Class<? extends BaseFragment>[] putFragments();

    /**
     * @param index item's position
     * @return //Return Action Click bar's item at index
     */
    protected abstract View getBottomItemView(int index);

    /**
     * @return The repository of Fragments --> Resource id
     */
    protected abstract int getFLid();

    /**
     * @return The repository of Action buttons at bottom normally.
     */
    protected abstract LinearLayout getBottomLayout();

    /**
     * The method is used for fresh ui state.
     * The method will be called on every item when checked the item.
     * Must Only do UI operation!
     *
     * @param item      The checked item
     * @param position  Item's position
     * @param isChecked Whether the item is checked
     */
    protected abstract void checkAllBottomItem(View item, int position, boolean isChecked);

    @Override
    protected void onResume() {
        super.onResume();
        for (BaseFragment f: fragments) {
            try {
                f.onActivityResume();
            } catch(Exception ex) {}
        }
    }
}
