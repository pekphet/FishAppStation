package cc.fish.cld_ctrl.appstate.entity;

import cc.fish.cld_ctrl.ad.entity.BaseResponse;

/**
 * Created by fish on 16-12-16.
 */

public class AllRespUpdate extends BaseResponse {
    private RespUpdate body;

    public RespUpdate getBody() {
        return body;
    }

    public void setBody(RespUpdate body) {
        this.body = body;
    }
}
