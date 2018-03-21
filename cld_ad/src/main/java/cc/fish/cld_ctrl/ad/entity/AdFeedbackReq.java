package cc.fish.cld_ctrl.ad.entity;

/**
 * Created by fish on 16-12-16.
 */

public class AdFeedbackReq {
    private String _action;
    private int app_ad_id;

    public AdFeedbackReq() {
    }

    public AdFeedbackReq(String _action, int app_ad_id) {
        this._action = _action;
        this.app_ad_id = app_ad_id;
    }

    public String get_action() {
        return _action;
    }

    public void set_action(String _action) {
        this._action = _action;
    }

    public int getApp_ad_id() {
        return app_ad_id;
    }

    public void setApp_ad_id(int app_ad_id) {
        this.app_ad_id = app_ad_id;
    }
}
