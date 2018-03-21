package cc.fish.cld_ctrl.appstate.entity;

import cc.fish.cld_ctrl.ad.entity.AdDeviceInfo;

/**
 * Created by fish on 16-12-15.
 */

public class ReqFeedback {
    private String     app_id;
    private int     version_code;
    private String  channel;
    private String  content;
    private AdDeviceInfo device;

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AdDeviceInfo getDevice() {
        return device;
    }

    public void setDevice(AdDeviceInfo device) {
        this.device = device;
    }
}

