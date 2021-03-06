package com.xiaozi.appstore.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fish.fishdownloader.service.deleteInfo
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.component.GlobalData
import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.manager.OBManager

/**
 * Created by fish on 18-1-5.
 */
class AppInstallRcv : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_PACKAGE_ADDED == intent?.action) {
            val appPackageName = intent.dataString?.split(":")?.get(1)?.trim()
            if (appPackageName != null) {
                Framework.Package.addInstalled(appPackageName)
                OBManager.INSTALL_CALLBACK_OBB.notifyObs(appPackageName)
                deleteInfo(context!!, appPackageName)
                NetManager.callInstalledUpload(appPackageName)
                NetManager.fastCall<String>(GlobalData.getCalls(appPackageName)?.install)
            }
        } else if (Intent.ACTION_PACKAGE_REMOVED == intent?.action) {
            val appPackageName = intent.dataString?.split(":")?.get(1)?.trim()
            if (appPackageName != null) {
                Framework.Package.removeInstalled(appPackageName)
            }
        }
    }

}