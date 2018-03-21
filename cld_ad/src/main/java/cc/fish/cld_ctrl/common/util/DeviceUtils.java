package cc.fish.cld_ctrl.common.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import cc.fish.cld_ctrl.ad.entity.AdDeviceInfo;

/**
 * Created by fish on 16-12-14.
 */

public class DeviceUtils {
    public static AdDeviceInfo getAdDeviceInfo(Context context) {
        float[] gps = getGps(context);
        AdDeviceInfo info = new AdDeviceInfo();
        info.setOsv(Build.VERSION.SDK_INT + "");
        info.setBrand(Build.MANUFACTURER);
        info.setModel(Build.MODEL.replace(" ", "_"));
        info.setLat(gps[0]);
        info.setLnt(gps[1]);
        info.setBright(getSystemBrightness(context));
        return info;
    }

    private static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            systemBrightness = -1;
        }
        return systemBrightness;
    }

    private static float[] getGps (Context context) {
        float[] defaultResult = {0.1f, 0.1f};
        return defaultResult;
    }

    public static String getImei(Context context) {
        try {
            TelephonyManager tm  = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId() == null ? "" : tm.getDeviceId();
        } catch (SecurityException se) {
            se.printStackTrace();
            return "";
        }
    }

}
