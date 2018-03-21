package cc.fish.cld_ctrl.ad.entity;

import cc.fish.cld_ctrl.ad.entity.enums.AdAction;
import cc.fish.cld_ctrl.ad.entity.enums.AdType;

/**
 * Created by fish on 16-12-13.
 */

public class ResponseAd {
    private AdType ad_type;
    private int ad_slot;
    private AdAction ad_action;
    private String link_url;
    private AdDisp ad_disp;
    private int app_ad_id;

    public int getApp_ad_id() {
        return app_ad_id;
    }

    public void setApp_ad_id(int app_ad_id) {
        this.app_ad_id = app_ad_id;
    }

    public AdType getAd_type() {
        return ad_type;
    }

    public void setAd_type(AdType ad_type) {
        this.ad_type = ad_type;
    }

    public int getAd_slot() {
        return ad_slot;
    }

    public void setAd_slot(int ad_slot) {
        this.ad_slot = ad_slot;
    }

    public AdAction getAd_action() {
        return ad_action;
    }

    public void setAd_action(AdAction ad_action) {
        this.ad_action = ad_action;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public AdDisp getAd_disp() {
        return ad_disp;
    }

    public void setAd_disp(AdDisp ad_disp) {
        this.ad_disp = ad_disp;
    }
}
