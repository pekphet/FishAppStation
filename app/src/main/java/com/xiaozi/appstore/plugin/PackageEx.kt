package com.xiaozi.appstore.plugin

import android.util.Log
import com.google.gson.Gson
import com.xiaozi.appstore.component.Device
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.manager.ConfManager

/**
 * Created by fish on 18-1-2.
 */
val _DEBUG = true

val _CLIENT_ID = "BK_APP_STORE"
val _KEY = "DA23JH1238"

val _GSON = Gson()


private val _LOG_D = true
private val _LOG_W = true
private val _LOG_E = true

fun ZLogD(tag: String = "FrameZLog", msg: String) = msg.apply { if (_LOG_D) Log.d(tag, msg) }
fun ZLogW(tag: String = "FrameZLog", msg: String) = msg.apply { if (_LOG_W) Log.w(tag, msg) }
fun ZLogE(tag: String = "FrameZLog", msg: String) = msg.apply { if (_LOG_E) Log.e(tag, msg) }

inline fun <T, R> within(t: T, r: T.() -> R) = t.run(r)

fun netSupportByWifi() = !ConfManager.isOnlyWifi() || Device.isUsingWifi(Framework._C)