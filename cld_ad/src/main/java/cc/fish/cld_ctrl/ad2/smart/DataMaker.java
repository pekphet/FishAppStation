package cc.fish.cld_ctrl.ad2.smart;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import cc.fish.cld_ctrl.ad2.entity.AdAppE;
import cc.fish.cld_ctrl.ad2.entity.AdDeviceE;
import cc.fish.cld_ctrl.ad2.entity.AdGeo;
import cc.fish.cld_ctrl.ad2.entity.AdReqEntity;
import cc.fish.cld_ctrl.ad2.entity.AdScreenE;
import cc.fish.cld_ctrl.common.net.NetManager;
import cc.fish.cld_ctrl.common.util.AppUtils;

/**
 * Created by fish on 17-1-18.
 */

public class DataMaker {
    private static AdDeviceE sDevice = null;
    private static AdAppE sApp = null;

    public static AdReqEntity getAdReq(int ad_slot) {
        return getAdReq(NetManager.mContext, ad_slot);
    }

    private static AdReqEntity getAdReq(Context c, int ad_slot) {
        AdReqEntity a = getBaseAdReq(c);
        a.setSlots(new int[]{ad_slot});
        return a;
    }

    private static AdReqEntity getBaseAdReq(Context context) {
        AdReqEntity result = new AdReqEntity();
        result.setGeo(new AdGeo());
        result.setApp(getApp(context));
        result.setDevice(getDevice(context));
        return result;
    }

    private static AdDeviceE getDevice(Context c) {
        if (sDevice == null) {
            sDevice = initDevice(c);
        }
        return sDevice;
    }

    private static AdAppE getApp(Context c) {
        if (sApp == null) {
            sApp = new AdAppE();
            sApp.setVersion(AppUtils.getVersionCode(c));
            sApp.setId(AppUtils.getMetaAppId(c));
        }
        return sApp;
    }

    private static AdDeviceE initDevice(Context applicationContext) {
        AdDeviceE device = new AdDeviceE();
        device.setOsv(Build.VERSION.RELEASE);
        device.setVendor(Build.PRODUCT);
        device.setModel(Build.MODEL);
        String androidId = Settings.Secure.getString(applicationContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        device.setAndroid_id(androidId);

        try {
            TelephonyManager tm = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getSubscriberId() == null) {
                device.setCarrier("46000");
            } else {
                device.setCarrier(tm.getSubscriberId().substring(0, 5));
            }
        } catch (SecurityException se) {
            se.printStackTrace();
            device.setCarrier("46000");
        }
        WifiManager wm = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
        device.setMac(wm.getConnectionInfo().getMacAddress());
        device.setIpv4(wm.getConnectionInfo().getIpAddress() + "");

        DisplayMetrics dm = applicationContext.getResources().getDisplayMetrics();
        device.setPpi(dm.densityDpi);
        device.setScreen(new AdScreenE(dm.widthPixels, dm.heightPixels));
        device.setConn_type(getCurrentConntype(applicationContext));
        return device;
    }

    private static int getCurrentConntype(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            return 0;
        }
        String type = ni.getTypeName();
        if (type.equalsIgnoreCase("WIFI")) {
            return 2;
        } else if (type.equalsIgnoreCase("MOBILE")) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            switch (tm.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_LTE:
                case 18: // TelephonyManager.NETWORK_TYPE_IWLAN:
                    return 6;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case 17: // TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    return 5;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return 4;
                default:
                    return 3;
            }
        } else {
            return 0;
        }
    }

}
