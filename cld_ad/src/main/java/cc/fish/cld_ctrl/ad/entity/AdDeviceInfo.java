package cc.fish.cld_ctrl.ad.entity;

/**
 * Created by fish on 16-12-13.
 */

public class AdDeviceInfo {
    private String brand;
    private String model;
    private String osv;
    private float lat;
    private float lnt;
    private int bright;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOsv() {
        return osv;
    }

    public void setOsv(String osv) {
        this.osv = osv;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLnt() {
        return lnt;
    }

    public void setLnt(float lnt) {
        this.lnt = lnt;
    }

    public int getBright() {
        return bright;
    }

    public void setBright(int bright) {
        this.bright = bright;
    }
}
