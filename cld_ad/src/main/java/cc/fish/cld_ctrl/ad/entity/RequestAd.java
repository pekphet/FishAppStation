package cc.fish.cld_ctrl.ad.entity;

import java.io.Serializable;

import cc.fish.cld_ctrl.ad.entity.enums.AdType;

/**
 * Created by fish on 16-12-13.
 */

public class RequestAd implements Serializable{
    private String app_id;
    private AdType ad_type;
    private int ad_slot;
    private String vid;
    private String channel;
    private AdDeviceInfo device_info;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public AdType getAd_type() {
        return ad_type;
    }

    public void setAd_type(AdType ad_type) {
        this.ad_type = ad_type;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public AdDeviceInfo getDevice_info() {
        return device_info;
    }

    public void setDevice_info(AdDeviceInfo device_info) {
        this.device_info = device_info;
    }

    public int getAd_slot() {
        return ad_slot;
    }

    public void setAd_slot(int ad_slot) {
        this.ad_slot = ad_slot;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
