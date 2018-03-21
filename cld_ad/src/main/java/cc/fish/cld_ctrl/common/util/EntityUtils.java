package cc.fish.cld_ctrl.common.util;

import com.google.gson.Gson;

/**
 * Created by fish on 16-12-14.
 */

public class EntityUtils {

    public static Object copy(Object origin, Class clz) {
        Gson gson = new Gson();
        String tmpJson = gson.toJson(origin);
        return gson.fromJson(tmpJson, clz);
    }
}
