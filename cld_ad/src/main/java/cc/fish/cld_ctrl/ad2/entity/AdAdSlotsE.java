package cc.fish.cld_ctrl.ad2.entity;

/**
 * Created by fish on 17-1-18.
 */

public class AdAdSlotsE {

    private int         app_slot_id;
    private AdScreenE   size;

    public AdAdSlotsE() {
    }

    public AdAdSlotsE(int app_slot_id, AdScreenE size) {
        this.app_slot_id = app_slot_id;
        this.size = size;
    }

    public int getApp_slot_id() {
        return app_slot_id;
    }

    public void setApp_slot_id(int app_slot_id) {
        this.app_slot_id = app_slot_id;
    }

    public AdScreenE getSize() {
        return size;
    }

    public void setSize(AdScreenE size) {
        this.size = size;
    }
}
