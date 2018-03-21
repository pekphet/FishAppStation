package cc.fish.cld_ctrl.ad.entity.enums;

import cc.fish.cld_ctrl.ad.entity.BaseResponse;
import cc.fish.cld_ctrl.ad.entity.ResponseAd;

/**
 * Created by fish on 16-12-16.
 */

public class AllResponseAd extends BaseResponse {
    private ResponseAd body;

    public ResponseAd getBody() {
        return body;
    }

    public void setBody(ResponseAd body) {
        this.body = body;
    }
}
