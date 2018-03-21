package com.xiaozi.appstore.component

import android.content.Context
import android.content.pm.PackageManager
import android.os.Process
import com.xiaozi.appstore.plugin.LogFilePlugin
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by fish on 17-10-12.
 */
class KExceptionHandler private constructor() : Thread.UncaughtExceptionHandler{

    companion object {
        fun get(): KExceptionHandler = Instance.instance
    }

    object Instance {
        val instance = KExceptionHandler()
    }

    var mContext: Context? = null

    fun init(ctx: Context){
        mContext = ctx
        Thread.setDefaultUncaughtExceptionHandler(this@KExceptionHandler)
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        e?.printStackTrace()
        if (!handleException(e)) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(t, e)
        }
        Process.killProcess(Process.myPid())
        System.exit(1)
    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        LogFilePlugin.AppendLogBlock("device", "{${getDeviceInfo()}}")
        LogFilePlugin.AppendCrashLogBlock(ex)
        return true
    }
    val formatter = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    private fun getDeviceInfo(): String {
        val pi = mContext?.packageManager?.getPackageInfo(mContext?.packageName, PackageManager.GET_ACTIVITIES)
        return "VersionName:${pi?.versionName?:""}\nVersionCode:${pi?.versionCode}\nTime:${formatter.format(Date())}"
    }
}