package cc.fish.cld_ctrl.appstate.interfaces;

/**
 * Created by fish on 16-12-15.
 */

public interface UpdateCallback {
    void update(String url, boolean isForce, String content, String versionName, int versionCode);
}
