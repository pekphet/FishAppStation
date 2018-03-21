package com.xiaozi.appstore.component

import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.RespAppConf


/**
 * Created by fish on 18-1-2.
 */
object GlobalData {
    private var mAppConf: RespAppConf? = null
    fun storeAppConfig(config: RespAppConf) {
        mAppConf = config
        DataManager.CategoryDM.init(config)
    }

    fun getAppConfig() = mAppConf
}