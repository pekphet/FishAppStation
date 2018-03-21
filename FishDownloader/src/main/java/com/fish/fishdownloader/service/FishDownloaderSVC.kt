package com.fish.fishdownloader.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.fish.downloader.framework.ThreadPool
import com.fish.fishdownloader.IFDownloadAction
import com.fish.fishdownloader.IFDownloadCallbacks
import com.fish.fishdownloader.service.FishDownloaderData.mCKS
import com.fish.fishdownloader.service.FishDownloaderData.mInfos
import com.fish.fishdownloader.service.FishDownloaderSVC.Companion.DOWNLOAD_DIR
import com.fish.fishdownloader.view.installApp
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Administrator on 2018/1/27.
 */
class FishDownloaderSVC : Service() {
    companion object {
        val DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory("ad/fishdownload/")
        const val TAG = "FISH DOWNLOAD SVC"
        val GSON = Gson()
    }

    private val mActionBinder = object : IFDownloadAction.Stub() {
        override fun initInfo(tag: String, name: String, downloadUrl: String, size: Int) {
            if (hasInfo(this@FishDownloaderSVC, tag)) {
                FishDownloaderData.mInfos[tag] = takeInfo(this@FishDownloaderSVC, tag)!!.apply { cancelSignal = false;pauseSignal = false }
            } else {
                FishDownloaderData.mInfos[tag] = DownloadRecInfo(tag, name, downloadUrl, "", 0, size, false, false)
            }
        }

        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?, aCK: IBinder?) {
        }

        override fun getAbsFilePath(tag: String): String {
            if (tag == "all")
                return GSON.toJson(takeAllInfo(this@FishDownloaderSVC))
            else
                return GSON.toJson(takeInfo(this@FishDownloaderSVC, tag))
        }

        override fun startDownload(tag: String) {
            takeInfo(this@FishDownloaderSVC, tag)?.run {
                if (ptr == size) {
                    if (File(filePath).exists()) {
                        FishDownloaderData.mCKS[tag]?.onComplete(filePath)
                        return
                    } else
                        deleteInfo(this@FishDownloaderSVC, tag)
                }
            }
            if (tag !in FishDownloaderData.mDownloadings) {
                ThreadPool.addTask(FishDownloader().get(this@FishDownloaderSVC, tag))
                FishDownloaderData.mDownloadings.add(tag)
            }
        }

        override fun cancelDownloadByTag(tag: String) {
            mInfos[tag]?.cancelSignal = true
        }

        override fun cancelAll() {
            mInfos.map { it.value.cancelSignal = true }
        }

        override fun registerCK(tag: String, ck: IBinder) {
            Log.e(TAG, "reg: $tag")
            mCKS[tag] = IFDownloadCallbacks.Stub.asInterface(ck)
        }

        override fun unregisterCK(tag: String) {
            mCKS.remove(tag)
        }

        override fun unregisterAllCKs() {
            mCKS.clear()
        }

        override fun hasTag(tag: String) = mInfos[tag]?.ptr ?: -1 > 0 || hasInfo(this@FishDownloaderSVC, tag)

        override fun pauseByTag(tag: String) {
            Log.e(TAG, "PAUSE  $tag")
            mInfos[tag]?.pauseSignal = true
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return mActionBinder
    }

    override fun onCreate() {
        super.onCreate()
    }
}

object FishDownloaderData {
    val mCKS = mutableMapOf<String, IFDownloadCallbacks>()
    val mInfos = mutableMapOf<String, DownloadRecInfo>()
    val mDownloadings = mutableSetOf<String>()
}

class FishDownloader {
    companion object {
        private const val BUF_SIZE = 2 * 1024
        const val TAG = "FISH Downloader"
    }

    private fun createFile(info: DownloadRecInfo): File {
        if (info.filePath.isNotBlank())
            File(info.filePath).run {
                if (exists())
                    return this
                else
                    FishDownloaderData.mInfos[info.tag]?.ptr = 0
            }
        return File(DOWNLOAD_DIR, "${info.name}-${System.currentTimeMillis()}.apk").apply {
            Log.e(TAG, "CREATE FILE")
            if (!parentFile.exists()) parentFile.mkdirs()
            if (exists()) delete()
            createNewFile()
            mInfos[info.tag]?.filePath = absolutePath
        }
    }

    fun get(ctx: Context, tag: String) = Runnable {
        try {
            if (mInfos[tag] == null) return@Runnable
            mInfos[tag]!!.pauseSignal = false
            val connection = URL(mInfos[tag]!!.downloadUrl).openConnection() as HttpURLConnection
            mInfos[tag]!!.ptr = File(mInfos[tag]!!.filePath).length().toInt()
            if (mInfos[tag]!!.ptr > 0) {
                connection.addRequestProperty("Range", "bytes=${limit(mInfos[tag]!!.ptr, 0)}-")
            }
            Log.e(TAG, "url connected!")
            if (connection.responseCode == 200 || connection.responseCode == 206 || connection.responseCode == 302) {
                if (connection.contentLength != 0) mInfos[tag]!!.size = connection.contentLength + limit(mInfos[tag]!!.ptr, 0)
                Log.e(TAG, "lenth:${mInfos[tag]!!.size}")
                val f = createFile(mInfos[tag]!!)
                val fos = FileOutputStream(f, true)
                val netIS = connection.inputStream
                var downloadPtr = limit(mInfos[tag]!!.ptr, 0)
                var readCnt = 0
                val buf = ByteArray(BUF_SIZE)
                saveInfo(ctx, mInfos[tag]!!)
                do {
                    readCnt = netIS.read(buf, 0, BUF_SIZE)
                    if (readCnt == -1)
                        break
                    fos.write(buf, 0, readCnt)
                    fos.flush()
//                    Log.e(TAG, "total: ${mInfos[tag]!!.size} ,tag: $tag, dptr:$downloadPtr, readCnt:$readCnt, BUF SIZE: $BUF_SIZE")
                    downloadPtr += readCnt
                    mCKS[tag]?.onProgress(downloadPtr * 1.0 / mInfos[tag]!!.size)
                    mInfos[tag]?.ptr = downloadPtr
//                    Log.e(TAG, "cancelSig: ${mInfos[tag]?.cancelSignal}, pauseSignal: ${mInfos[tag]?.pauseSignal}")
                } while (readCnt > 0 && !(mInfos[tag]?.cancelSignal
                                ?: return@Runnable) && !(mInfos[tag]?.pauseSignal
                                ?: return@Runnable))
                Log.e(TAG, "exit looper")
                if (mInfos[tag]?.cancelSignal!!) {
                    mCKS[tag]?.onCanceled(tag)
                    deleteInfo(ctx, tag)
                    f.delete()
                    Log.e(TAG, "canceled: ${tag}")
                } else if (mInfos[tag]!!.pauseSignal) {
                    saveInfo(ctx, mInfos[tag]!!)
                    mCKS[tag]?.onPause(mInfos[tag]!!.filePath)
                    Log.e(TAG, "pause: ${tag}")
                } else {
                    saveInfo(ctx, mInfos[tag]!!)
                    installApp(ctx, mInfos[tag]!!.filePath)
                    mCKS[tag]?.onComplete(mInfos[tag]!!.filePath)
                    Log.e(TAG, "completed: ${tag}")
                }
                try {
                    fos.close()
                    netIS.close()
                    connection.disconnect()
                } catch (ioex: IOException) {
                    ioex.printStackTrace()
                }
            } else {
                mCKS[tag]?.onFailed("REQUEST ERROR:${connection.responseCode}")
                Log.e(TAG, "failed: ${tag}")
                deleteInfo(ctx, tag)
            }
        } catch (ioEX: IOException) {
            ioEX.printStackTrace()
            mCKS[tag]?.onFailed("CONNECTION FAILED")
            Log.e(TAG, "failed: ${tag}")
            deleteInfo(ctx, tag)
        } finally {
            FishDownloaderData.mDownloadings.remove(tag)
        }
    }

    private fun limit(origin: Int, limits: Int) = if (origin <= limits) 0 else origin - limits
}