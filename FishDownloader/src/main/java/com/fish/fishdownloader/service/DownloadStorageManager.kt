package com.fish.fishdownloader.service

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log

/**
 * Created by Administrator on 2018/1/28.
 */
val DownloadSPLock = Any()

fun deleteInfo(ctx: Context, tag: String) = synchronized(DownloadSPLock) { ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).edit().remove(tag).apply() }

fun saveInfo(ctx: Context, info: DownloadRecInfo) = synchronized(DownloadSPLock) { ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).edit().putString(info.tag, FishDownloaderSVC.GSON.toJson(info)).apply() }

fun takeInfo(ctx: Context, tag: String): DownloadRecInfo? {
    synchronized(DownloadSPLock) {
        ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).all.run {
            if (containsKey(tag))
                return FishDownloaderSVC.GSON.fromJson(this[tag] as? String
                        ?: return null, DownloadRecInfo::class.java)
            return null
        }
    }
}

fun takeAllInfo(ctx: Context): List<DownloadRecInfo> {
    return synchronized(DownloadSPLock) {
        ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).all
                .map {
                    FishDownloaderSVC.GSON.fromJson(it.value.toString(), DownloadRecInfo::class.java)
                }.filter { it != null }
    }
}

fun hasInfo(ctx: Context, tag: String) = synchronized(DownloadSPLock) { ctx.applicationContext.getSharedPreferences("download_rec", Context.MODE_MULTI_PROCESS).all.containsKey(tag) }

data class DownloadRecInfo(var tag: String, var name: String, var downloadUrl: String, var filePath: String, var ptr: Int, var size: Int, var cancelSignal: Boolean, var pauseSignal: Boolean)

object DownloadStorageManager {
    fun save(ctx: Context, tag: String) {

    }

    fun delete(ctx: Context, tag: String) {

    }

    fun have(ctx: Context, tag: String) {

    }

    fun all(ctx: Context): List<DownloadRecInfo> {
        return listOf()
    }
}