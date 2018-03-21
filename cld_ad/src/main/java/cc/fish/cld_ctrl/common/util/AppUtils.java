package cc.fish.cld_ctrl.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by fish on 16-12-15.
 */

public class AppUtils {
    private static int sVerCode = -1;
    private static String sVerName = "";


    private static Bundle getMetaData (Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getMetaAppId(Context context) {
        return getMetaData(context).getString(Const.META_DATA_APP_ID);
    }

    public static String getMetaChannel(Context context) {
        return  getMetaData(context).getString(Const.META_DATA_CHANNEL);
    }

    public static int getVersionCode(Context context){
        if (sVerCode != -1) {
            return sVerCode;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            sVerCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return sVerCode;
    }

    public static String getVersionName(Context context){
        if (!"".equals(sVerName)) {
            return sVerName;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            sVerName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return sVerName;
    }


}
