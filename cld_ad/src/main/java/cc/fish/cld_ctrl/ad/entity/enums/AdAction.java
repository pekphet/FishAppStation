package cc.fish.cld_ctrl.ad.entity.enums;

/**
 * Created by fish on 16-12-13.
 */

public enum AdAction {
    Web,            //跳转网页,使用内部WebView
    Download,       //下载文件
    OuterWeb,       //跳转网页，使用外部浏览器
    AutoLoad,       //自动跳转
}
