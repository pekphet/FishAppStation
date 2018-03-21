package cc.fish.cld_ctrl.ad2.entity;

/**
 * Created by fish on 17-1-18.
 */

public class AdReqEntity {
    private AdAppE      app;
    private AdDeviceE   device;
    private AdGeo       geo;
    private int[]       slots;

    public AdAppE getApp() {
        return app;
    }

    public void setApp(AdAppE app) {
        this.app = app;
    }

    public AdDeviceE getDevice() {
        return device;
    }

    public void setDevice(AdDeviceE device) {
        this.device = device;
    }

    public AdGeo getGeo() {
        return geo;
    }

    public void setGeo(AdGeo geo) {
        this.geo = geo;
    }

    public int[] getSlots() {
        return slots;
    }

    public void setSlots(int[] slots) {
        this.slots = slots;
    }
}
