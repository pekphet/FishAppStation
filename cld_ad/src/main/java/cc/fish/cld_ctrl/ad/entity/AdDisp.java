package cc.fish.cld_ctrl.ad.entity;

/**
 * Created by fish on 16-12-13.
 */
public class AdDisp {
    private String icon;
    private String title;
    private String description;
    private String[] images;
    private String html_src;
    private int is_url;
    private String content_url;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getHtml_src() {
        return html_src;
    }

    public void setHtml_src(String html_src) {
        this.html_src = html_src;
    }

    public int getIs_url() {
        return is_url;
    }

    public void setIs_url(int is_url) {
        this.is_url = is_url;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }
}
