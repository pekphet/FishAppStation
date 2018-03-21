package com.xiaozi.appstore

import android.app.Application
import cc.fish.cld_ctrl.ad.CldAd
import com.fish.fishdownloader.service.CrossProcessDownloadDataManager
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.plugin.netSupportByWifi

/**
 * Created by fish on 18-1-2.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Framework.mContext = applicationContext
        CldAd.init(applicationContext)
        initEnv()

    }

    private fun initEnv() {
        Framework.Package.installed()
        CrossProcessDownloadDataManager.initControlConnection(this)
        CrossProcessDownloadDataManager.installInfoCK { Framework.Package.installed() }
        CrossProcessDownloadDataManager.netSupportCK { netSupportByWifi() }
    }
}