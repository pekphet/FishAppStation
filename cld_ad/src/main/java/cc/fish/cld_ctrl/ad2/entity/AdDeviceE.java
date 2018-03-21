package cc.fish.cld_ctrl.ad2.entity;

/**
 * Created by fish on 17-1-18.
 */

public class AdDeviceE {
    private String      ipv4;
    private int         os = 1;
    private String      osv;
    private String      vendor;
    private String      model;
    private String      android_id;
    private String      mac;
    private AdScreenE   screen;
    private int         ppi;
    private String      carrier;
    private int         conn_type;

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }

    public String getOsv() {
        return osv;
    }

    public void setOsv(String osv) {
        this.osv = osv;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public AdScreenE getScreen() {
        return screen;
    }

    public void setScreen(AdScreenE screen) {
        this.screen = screen;
    }

    public int getPpi() {
        return ppi;
    }

    public void setPpi(int ppi) {
        this.ppi = ppi;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public int getConn_type() {
        return conn_type;
    }

    public void setConn_type(int conn_type) {
        this.conn_type = conn_type;
    }
}
