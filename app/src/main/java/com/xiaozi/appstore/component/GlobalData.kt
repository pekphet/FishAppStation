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

    private val mCallsMap = mutableMapOf<String, DataManager.UrlCalls?>()
    fun putCalls(pkg: String?, calls: DataManager.UrlCalls?) = if (pkg == null) null else mCallsMap.put(pkg, calls)
    fun getCalls(pkg: String) = mCallsMap[pkg]
}