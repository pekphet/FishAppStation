package com.xiaozi.appstore.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.view.WindowManager
import com.xiaozi.appstore.ZToast
import java.net.NetworkInterface

/**
 * Created by fish on 17-7-4.
 */
class Device() {
    companion object {
        val DEVICE_NAME = Build.MODEL
        val OS_VERSION = "${Build.VERSION.SDK_INT}"

        private val NET_STATUS_NO = 0
        private val NET_STATUS_UNKNOWN = -1
        private val NET_STATUS_NOT_WIFI = 30
        private val NET_STATUS_WIFI = ConnectivityManager.TYPE_WIFI

        @SuppressLint("MissingPermission")
        fun getIMEI(): String {
            try {
                return (Framework.mContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
            } catch (ex: Exception) {
                ex.printStackTrace()
                return "000000000000000"
            }
        }

        fun isUsingWifi(ctx: Context): Boolean {
            val netType = try {
                val netInfo = (ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo
                if (netInfo == null || !netInfo.isAvailable) {
                    NET_STATUS_NO
                } else {
                    if (netInfo.type == NET_STATUS_WIFI) NET_STATUS_WIFI else NET_STATUS_NOT_WIFI
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                NET_STATUS_UNKNOWN
            }
            return netType == NET_STATUS_WIFI
        }
        fun CheckNetStatForToast(activity: Activity) {
            val netType = try {
                val netInfo = (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo
                if (netInfo == null || !netInfo.isAvailable) {
                    NET_STATUS_NO
                } else {
                    if (netInfo.type == NET_STATUS_WIFI) NET_STATUS_WIFI else NET_STATUS_NOT_WIFI
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                NET_STATUS_UNKNOWN
            }
            activity.ZToast(when (netType) {
                NET_STATUS_NO -> "网络连接不可用，请稍后再试"
                NET_STATUS_WIFI -> return
                NET_STATUS_NOT_WIFI -> "注意：正在使用非WIFI网络，下载将产生流量费用"
                else -> "网络环境未知，下载可能产生流量费用"
            })
        }

        @SuppressLint("WifiManagerLeak")
        fun getIP(): String {
            val wm = Framework.mContext!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wm.isWifiEnabled) {
                return ip2Str(wm.connectionInfo.ipAddress)
            }
            val enumNI = NetworkInterface.getNetworkInterfaces()
            while (enumNI.hasMoreElements()) {
                enumNI.nextElement().apply {
                    if (!isLoopback) {
                        val nads = inetAddresses
                        while (nads.hasMoreElements()) {
                            nads.nextElement().apply {
                                if (isLoopbackAddress) {
                                    return hostAddress.toString()
                                }
                            }
                        }
                    }
                }
            }
            return "127.0.0.2"
        }

        fun getMacAddr(): String {
            var macAddress: String
            val buf = StringBuffer()
            var networkInterface: NetworkInterface?
            try {
                networkInterface = NetworkInterface.getByName("eth1")
                if (networkInterface == null) {
                    networkInterface = NetworkInterface.getByName("wlan0")
                }
                if (networkInterface == null) {
                    return "02:00:00:00:00:02"
                }
                val addr = networkInterface.getHardwareAddress()
                for (b in addr) {
                    buf.append(String.format("%02X:", b))
                }
                if (buf.length > 0) {
                    buf.deleteCharAt(buf.length - 1)
                }
                macAddress = buf.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                return "02:00:00:00:00:02"
            }
            return macAddress;
        }

        fun ip2Str(ip: Int) = "${ip and 0xff}.${(ip shr 8) and 0xff}.${(ip shr 16) and 0xff}.${(ip shr 24) and 0xff}"

        fun getDispWidth() = (Framework.mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width
        fun getDispHeight() = (Framework.mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.height
        fun getDispPPI() = Framework.mContext!!.resources.displayMetrics.densityDpi

        @Suppress("DEPRECATION")
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getAndroidID(): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try{
                    Build.getSerial()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Build.SERIAL
                }
            } else {
                Build.SERIAL
            }
        }

        fun getCarrierType(): Int {
            try {
                (Framework.mContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simOperator.run {
                    if (equals("46000") || equals("46002"))
                        return 1
                    else if (equals("46001"))
                        return 2
                    else if (equals("46003"))
                        return 3
                    else return 0
                }
            } catch (ex: Exception) {
                return 0
            }
        }
    }
}