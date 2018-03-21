package cc.fish.coreui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fish on 16-4-25.
 */
abstract public class BaseFragment extends Fragment implements View.OnClickListener{
    protected Context mContext;
    private Handler mHandler = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = initView(inflater);
        onSelected();
        return v;
    }

    @Override
    public void onClick(View v) {
        click(v);
    }

    protected Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public void onActivityResume(){}

    abstract protected View initView(LayoutInflater inflater);

    /*** will be called when the tab selected ***/
    protected void onSelected(){}

    protected void click(View v){}

}
