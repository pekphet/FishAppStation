package cc.fish.coreui.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fish on 16-4-28.
 */
public class PreferenceUtil {

    public static SharedPreferences getSharedPreference(Context context, String preferenceName) {
        return context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

}
