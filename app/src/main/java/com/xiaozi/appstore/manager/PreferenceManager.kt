package com.xiaozi.appstore.manager

import android.content.Context
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.plugin._GSON
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.toNotNullMutableList
import java.io.Serializable

/**
 * Created by fish on 17-7-4.
 */
sealed class PreferenceManager(module: Module) {
    val module = module
    fun getSP() = Framework._C.getSharedPreferences(module.name, Context.MODE_PRIVATE)
    fun putValue(key: String, value: Any?) {
        if (value == null) {
            getSP().edit().remove(key).apply()
            return
        }
        getSP().edit().apply {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                else -> putString(key, _GSON.toJson(value))
            }
        }.apply()
    }

    fun addItem(key: String, data: String) {
        getSP().getStringSet(key, setOf()).let {
            with(mutableSetOf<String>()) {
                addAll(it)
                add(data)
                getSP().edit().putStringSet(key, it).apply()
            }
        }
    }

    fun getBooleanValue(key: String, default: Boolean = false) = getSP().getBoolean(key, default)
    fun getIntValue(key: String, defaultValue: Int) = getSP().getInt(key, defaultValue)
    fun getIntValue(key: String) = getIntValue(key, -1)
    fun getStringValue(key: String) = getSP().getString(key, "")

    fun haveKey(key: String) = getSP().getString(key, null) != null

    enum class Module {
        Account,
        App,
        Config,
        Download,
    }
}

object AccountManager {
    private val KEY_TOKEN = "TOKEN"
    private val KEY_UID = "UID"

    var userName = ""
    var userHeadIcon = ""

    fun storeToken(token: String, id: Int) {
        AccountSPMgr.putValue(KEY_TOKEN, token)
        AccountSPMgr.putValue(KEY_UID, id)
    }

    fun isLoggedIn() = AccountSPMgr.haveKey(KEY_TOKEN)
    fun token() = AccountSPMgr.getStringValue(KEY_TOKEN)
    fun uid() = AccountSPMgr.getIntValue(KEY_UID)
    fun logout() {
        AccountSPMgr.putValue(KEY_TOKEN, null)
        AccountSPMgr.putValue(KEY_UID, null)
        userName = ""
        userHeadIcon = ""
    }
}

object DownloadInfoManager {
    val KEY_DOWNLOADS = "downloads"
    val downloadInfos: MutableList<DownloadInfo>

    init {
        downloadInfos = DownloadSPMgr.getSP().all.map { _GSON.safety { fromJson(it.value as String, DownloadInfo::class.java) } }.toNotNullMutableList()
    }

    fun getInfoByTag(tag: String): DownloadInfo? = try {
        downloadInfos.filter { it.tag == tag }[0]
    } catch (ex: Exception) {
        null
    }

    fun storeInfo(info: DownloadInfo) {
        DownloadSPMgr.putValue(info.tag, info)
        downloadInfos.add(info)
    }

    fun removeInfo(tag: String) {
        DownloadSPMgr.putValue(tag, null)
        downloadInfos.remove(getInfoByTag(tag))
    }

    data class DownloadInfo(val url: String, val path: String, val name: String, val tag: String, val size: Int, var ptr: Int)
}

object ConfManager {
    val KEY_ONLYWIFI = "only_wifi"
    fun isOnlyWifi() = ConfSPMgr.getBooleanValue(KEY_ONLYWIFI, true)
    fun setOnlyWifi(value: Boolean) = ConfSPMgr.putValue(KEY_ONLYWIFI, value)
}

object AccountSPMgr : PreferenceManager(Module.Account)
object AppSPMgr : PreferenceManager(Module.App)
object ConfSPMgr : PreferenceManager(Module.Config)
object DownloadSPMgr : PreferenceManager(Module.Download)