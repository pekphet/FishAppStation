package cc.fish.cld_ctrl.ad2.entity;

import java.util.Arrays;

import cc.fish.cld_ctrl.ad.entity.BaseResponse;

/**
 * Created by fish on 17-2-6.
 */

public class Ad2RespE extends BaseResponse{
    private Ad2RespEntity[] body;

    public Ad2RespEntity[] getBody() {
        return body;
    }

    public void setBody(Ad2RespEntity[] body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Ad2RespE{" +
                "body=" + Arrays.toString(body) +
                '}';
    }
}
