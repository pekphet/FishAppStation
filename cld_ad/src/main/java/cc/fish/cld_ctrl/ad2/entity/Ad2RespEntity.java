package cc.fish.cld_ctrl.ad2.entity;

import java.util.Arrays;

import static android.R.attr.type;

/**
 * Created by fish on 17-1-18.
 */

public class Ad2RespEntity {
    /*
    Rsponse:
        {
            html_snippet: "",           - string  HTML类型广告物料，内容为已渲染的HTML代码，可以直接进行展示
            html_url: "",               - string  网页
            title: "",                  - string  广告标题
            description:"",             - string  广告描述信息
            sub_description:"",         - string  广告描述信息2，原生广告可能包含该字段
            images:[],                  - string[] 大图的图片信息
            icons:[],                   - string[] 同上
            logos:[],                   - string[] 同上
            click_url:"",               - string  点击行为地址，在客户端进行响应，经过多次跳转最终会到达目标地址
                imp_trackers:[],            - string[]展现上报url，包含一个或多个地址，如果广告成功展现，客户端需要逐条进行上报
                clk_trackers:[],            - string[]    点击上报url，包含一个或多个地址，如果广告被用户点击，则客户端需要逐条进行上报
            datas:[
                {
                    type: 1,            - int 数据元素类型  见附录
                    value: ""           - string 数据格式化字符串的值
                }
            ],
            act_type:1,                 - int  交互类型 见附录
            type:[]                     - int  广告类型 见附录
        }
     */
    private String          html_snippet;
    private String          html_url;
    private String          title;
    private String          description;
    private String          sub_description;
    private String[]        images;
    private String[]        icons;
    private String[]        logos;
    private String          click_url;
    private String[]        imp_trackers;
    private String[]        clk_trackers;
    private Ad2RespDataE[]  datas;
    private int             act_type;
    private int             ad_type;

    public String getHtml_snippet() {
        return html_snippet;
    }

    public void setHtml_snippet(String html_snippet) {
        this.html_snippet = html_snippet;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
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

    public String getSub_description() {
        return sub_description;
    }

    public void setSub_description(String sub_description) {
        this.sub_description = sub_description;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getIcons() {
        return icons;
    }

    public void setIcons(String[] icons) {
        this.icons = icons;
    }

    public String[] getLogos() {
        return logos;
    }

    public void setLogos(String[] logos) {
        this.logos = logos;
    }

    public String getClick_url() {
        return click_url;
    }

    public void setClick_url(String click_url) {
        this.click_url = click_url;
    }

    public String[] getImp_trackers() {
        return imp_trackers;
    }

    public void setImp_trackers(String[] imp_trackers) {
        this.imp_trackers = imp_trackers;
    }

    public String[] getClk_trackers() {
        return clk_trackers;
    }

    public void setClk_trackers(String[] clk_trackers) {
        this.clk_trackers = clk_trackers;
    }

    public Ad2RespDataE[] getDatas() {
        return datas;
    }

    public void setDatas(Ad2RespDataE[] datas) {
        this.datas = datas;
    }

    public int getAct_type() {
        return act_type;
    }

    public void setAct_type(int act_type) {
        this.act_type = act_type;
    }

    public int getType() {
        return ad_type;
    }

    public void setType(int type) {
        this.ad_type = type;
    }

    @Override
    public String toString() {
        return "Ad2RespEntity{" +
                "html_snippet='" + html_snippet + '\'' +
                ", html_url='" + html_url + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", sub_description='" + sub_description + '\'' +
                ", images=" + Arrays.toString(images) +
                ", icons=" + Arrays.toString(icons) +
                ", logos=" + Arrays.toString(logos) +
                ", click_url='" + click_url + '\'' +
                ", imp_trackers=" + Arrays.toString(imp_trackers) +
                ", clk_trackers=" + Arrays.toString(clk_trackers) +
                ", datas=" + Arrays.toString(datas) +
                ", act_type=" + act_type +
                ", type=" + type +
                '}';
    }
}
