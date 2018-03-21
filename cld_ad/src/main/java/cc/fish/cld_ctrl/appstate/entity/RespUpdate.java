package cc.fish.cld_ctrl.appstate.entity;

import java.io.Serializable;

/**
 * Created by fish on 16-12-15.
 */

public class RespUpdate implements Serializable {
    private String  download_url;
    private int     version_code;
    private String  version_name;
    private String  content;
    private int     is_force;

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIs_force() {
        return is_force;
    }

    public void setIs_force(int is_force) {
        this.is_force = is_force;
    }
}
