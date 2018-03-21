package cc.fish.cld_ctrl.ad2.entity;

/**
 * Created by fish on 17-1-18.
 */

public class Ad2RespDataE {
    private int     type;
    private String  value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Ad2RespDataE{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
