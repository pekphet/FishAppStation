package com.fish.downloader.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.fish.downloader.framework.ThreadPool
import com.fish.fishdownloader.IDownloadCK
import com.fish.fishdownloader.IDownloader
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Created by fish on 17-9-6.
 */
class DownloadService : Service() {
    companion object {
        val DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory("ad/download/")
        val TAG = "FISH DOWNLOAD SERVICE"
        val GSON = Gson()
    }

    val mDownloaderBinder: IBinder = object : IDownloader.Stub() {

        override fun hasTag(tag: String) = mDownloadMapper.containsKey(tag)

        @Synchronized override fun pauseByTag(tag: String?) {
            mDownloadMapper[tag]?.pauseSignal = true
        }

        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?, aCK: IBinder?) {
        }

        override fun getAbsFilePath(tag: String): String? {
            return mDownloadMapper.get(tag)?.filePath
        }

        override fun startDownload(url: String, tag: String, fileName: String, fileSize: Long) {
            Log.e(TAG, "start download")
            var pausePtr = 0
            takePauseInfo(tag).run {
                if (ptr != 0) {
                    if (File(path).run { exists() && length() >= ptr }) {
                        pausePtr = ptr
                    }
                }
                mDownloadMapper.put(tag, Downloader.createDownloadInfo(url, tag, fileName, fileSize, pausePtr))
                ThreadPool.addTask(Downloader().get(mDownloadMapper.get(tag) ?: return, mDownloadCKSender))
            }
        }

        override fun cancelDownloadByTag(tag: String?) {
            mDownloadMapper.get(tag)?.cancelSignal = true
        }

        override fun cancelAll() {
            mDownloadMapper.map { it.value?.cancelSignal = true }
        }

        override fun registerCB(ck: IBinder?) {
            mCKs.add(IDownloadCK.Stub.asInterface(ck))
//            Log.e(TAG, "CKS LEN:${mCKs.size}")
        }

        override fun unregisterCB(ck: IBinder?) {
            mCKs.remove(IDownloadCK.Stub.asInterface(ck))
        }
    }

    val mDownloadCKSender = object : IDownloadCK.Stub() {
        override fun onPause(tag: String, filePath: String, ptr: Int, size: Int) {
            if (mDownloadMapper.containsKey(tag)) {
                savePauseInfo(DownloadPausedInfo(tag, mDownloadMapper[tag]?.filePath ?: "", filePath, ptr, size))
                mCKs.map { it.onPause(tag, filePath, ptr, size) }
            }
        }

        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
        }

        override fun onProgress(tag: String?, pg: Double) {
//            Log.e(TAG, "pg:$pg")
            mCKs.map { it.onProgress(tag, pg) }
        }

        override fun onComplete(tag: String?, filePath: String?) {
            Log.e(TAG, "complete:$filePath")
            mCKs.map { it.onComplete(tag, filePath) }
            mDownloadMapper.remove(tag)
        }

        override fun onFailed(tag: String?, msg: String?) {
            Log.e(TAG, "tag: $tag, failed: $msg")
            mCKs.map { it.onFailed(tag, msg) }
            mDownloadMapper.remove(tag)
        }

        override fun onCanceled(tag: String?) {
            Log.e(TAG, "Cancel:$tag")
            mCKs.map { it.onCanceled(tag) }
            mDownloadMapper.remove(tag)

        }
    }

    val mDownloadMapper = HashMap<String, DownloadInfo?>()
    val mCKMap = mutableMapOf<String, IDownloadCK>()
    val mCKs = ArrayList<IDownloadCK>()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.e(TAG, "ON BIND")
        return mDownloaderBinder
    }

    private fun savePauseInfo(info: DownloadPausedInfo) = getSharedPreferences("pause-list", Context.MODE_PRIVATE).edit().putString(info.tag, GSON.toJson(info)).apply()
    private fun takePauseInfo(tag: String) = GSON.fromJson(getSharedPreferences("pause-list", Context.MODE_PRIVATE).getString(tag, "{}"), DownloadPausedInfo::class.java)

}


data class DownloadInfo(val tag: String, val url: String, val fileName: String, var filePath: String, var fileSize: Long, var offset: Long, var cancelSignal: Boolean, var pauseSignal: Boolean) : Serializable
data class DownloadPausedInfo(val tag: String, val url: String, val path: String, val ptr: Int, val size: Int) : Serializable

class Downloader {
    companion object {
        private val BUF_SIZE = 2 * 1024
        val TAG = "fish downloader"
        fun createDownloadInfo(url: String, tag: String, fileName: String, fileSize: Long, ptr: Int)
                = DownloadInfo(tag, url, fileName, "", fileSize, ptr.toLong(), false, false)
    }

    private fun createFile(info: DownloadInfo) = File(DownloadService.DOWNLOAD_DIR, "${info.fileName}-${System.currentTimeMillis()}.apk").apply {
        Log.e(TAG, "CREATE FILE")
        if (!parentFile.exists()) parentFile.mkdirs()
        if (exists()) delete()
        createNewFile()
    }

    fun get(info: DownloadInfo, ck: IDownloadCK) = Runnable {
        try {
            Log.e(TAG, "START ${info.url}")
            val connection = URL(info.url).openConnection() as HttpURLConnection
            if (info.offset > 0) {
                connection.addRequestProperty("Range", "bytes=${limit(info.offset.toInt(), 1024)}-")
            }
            Log.e(TAG, "url connected!")
            if (connection.responseCode == 200 || connection.responseCode == 206 || connection.responseCode == 302) {
                Log.e(TAG, "code:${connection.responseCode}")
                if (connection.contentLength != 0) info.fileSize = connection.contentLength.toLong()
                Log.e(TAG, "lenth:${info.fileSize}")
                val f = createFile(info)
                info.filePath = f.absolutePath
                val fos = FileOutputStream(f, true)
                val netIS = connection.inputStream
                var downloadPtr = limit(info.offset.toInt(), 1024)
                var readCnt = 0
                val buf = ByteArray(BUF_SIZE)
                do {
                    readCnt = netIS.read(buf, 0, BUF_SIZE)
                    if (readCnt == -1)
                        break
                    fos.write(buf, 0, readCnt)
                    fos.flush()
                    Log.e(TAG, "dptr:$downloadPtr, readCnt:$readCnt, BUF SIZE: $BUF_SIZE")
                    downloadPtr += readCnt
                    ck.onProgress(info.tag, downloadPtr * 1.0 / info.fileSize)
                    Log.e(TAG, "cancelSig: ${info.cancelSignal}, pauseSignal: ${info.pauseSignal}")
                } while (readCnt > 0 && !info.cancelSignal && !info.pauseSignal)
                Log.e(TAG, "exit looper")
                try {
                    fos.close()
                    netIS.close()
                    connection.disconnect()
                } catch (ioex: IOException) {
                    ioex.printStackTrace()
                }
                if (info.cancelSignal) {
                    ck.onCanceled(info.tag)
                    f.delete()
                } else if (info.pauseSignal) {
                    ck.onPause(info.tag, info.filePath, downloadPtr, info.fileSize.toInt())
                } else {
                    ck.onComplete(info.tag, info.filePath)
                }
            } else {
                ck.onFailed(info.tag, "REQUEST ERROR:${connection.responseCode}")
            }
        } catch (ioEX: IOException) {
            ioEX.printStackTrace()
            ck.onFailed(info.tag, "CONNECTION FAILED")
        }
    }

    fun limit(origin: Int, limits: Int) = if (origin <= limits) 0 else origin - limits
}
