package cc.fish.cld_ctrl.ad2.entity;

/**
 * Created by fish on 17-1-18.
 */

public class AdScreenE {
    private int width;
    private int height;

    public AdScreenE() {
    }

    public AdScreenE(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
