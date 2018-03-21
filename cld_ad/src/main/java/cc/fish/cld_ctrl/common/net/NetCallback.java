package cc.fish.cld_ctrl.common.net;

/**
 * Created by fish on 16-12-14.
 */

public interface NetCallback<T> {
    void success(T result);
    void failed(String msg);
    void noDisp();


}
