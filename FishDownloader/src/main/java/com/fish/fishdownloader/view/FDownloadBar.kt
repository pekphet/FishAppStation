package com.fish.fishdownloader.view

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.content.FileProvider
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.fish.downloader.extensions.bid
import com.fish.fishdownloader.IFDownloadAction
import com.fish.fishdownloader.IFDownloadCallbacks
import com.fish.fishdownloader.R
import com.fish.fishdownloader.service.*
import java.io.File

/**
 * Created by Administrator on 2018/1/27.
 */
class FDownloadBar(val ctx: Context, val attrs: AttributeSet?) : FrameLayout(ctx, attrs) {
    companion object {
        val H = Handler(Looper.getMainLooper())
    }

    init {
        View.inflate(ctx, R.layout.v_download_bar, this)
    }

    lateinit var mConnection: ServiceConnection
    lateinit var mServiceStub: IFDownloadAction
    val mActionTv by bid<TextView>(R.id.tv_dlbar_pg)
    val mProgress by bid<FrameLayout>(R.id.fl_dlbar_progress)
    private var mStatus: DownloadStatus = DownloadStatus.IDLE
        set(value) {
            if (field != value) {
                field = value
                flushUI()
            }
        }
    var mOnProgress: (Double) -> Unit = {}
    var mOnComplete: (String) -> Unit = {}
    var mOnFailed: (String) -> Unit = {}
    var mOnCanceled: (String) -> Unit = {}
    var mOnPause: (String) -> Unit = {}
    var mTag = ""
    var mInfoDelay: Triple<String, String, Int>? = null
    lateinit var mCK: IFDownloadCallbacks.Stub


    inner class DefaultDownloadSVCCallback : IFDownloadCallbacks.Stub() {
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
        }

        override fun onProgress(pg: Double) {
            post {
                mActionTv.text = "下载中"
                progressUI(pg)
                mOnProgress(pg)
            }
        }

        override fun onComplete(filePath: String) {
            post {
                progressUI(0.0)
                mOnComplete(filePath)
//                installApp(ctx, filePath)
                mStatus = DownloadStatus.COMPLETE
            }
        }

        override fun onFailed(msg: String) {
            post {
                mOnFailed(msg)
                mStatus = DownloadStatus.FAILED
                ZToast(msg)
            }
        }

        override fun onCanceled(msg: String) {
            post {
                mOnCanceled(msg)
                mStatus = DownloadStatus.IDLE
            }
        }

        override fun onPause(msg: String) {
            post {
                mOnPause(msg)
                mStatus = DownloadStatus.PAUSE
            }
        }
    }

    /****PUBLIC FUNCS****/
    fun bindTag(tag: String) {
        mCK = DefaultDownloadSVCCallback()
        mInfoDelay = null
        mStatus = DownloadStatus.IDLE
        progressUI(0.0)
        mOnProgress = {}
        mOnComplete = {}
        mOnFailed = {}
        mOnCanceled = {}
        mOnPause = {}
        mTag = tag
        initStatusBySP(mTag)
        initConnect()
        flushUI()
    }

    fun putInfo(name: String, url: String, size: Int) {
        if (this::mServiceStub.isInitialized)
            mServiceStub.initInfo(mTag, name, url, size)
        else {
            mInfoDelay = Triple(name, url, size)
        }
    }

    fun release() {
        try {
            mServiceStub.unregisterCK(mTag)
            mTag = ""
            mInfoDelay = null
            mStatus = DownloadStatus.IDLE
            mOnProgress = {}
            mOnComplete = {}
            mOnFailed = {}
            mOnCanceled = {}
            mOnPause = {}
            flushUI()
            progressUI(0.0)
            if (this::mConnection.isInitialized)
                ctx.applicationContext.unbindService(mConnection)
        } catch (ex: Exception) {
        }
    }

    /****INITIAL****/
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.e("FDB", "ONATTACHED")
        cleanView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.e("FDB", "ONDETACHED")
        release()
    }


    private fun cleanView() {
        flushUI()
    }

    private fun initStatusBySP(tag: String) {
        if (CrossProcessDownloadDataManager.isInstalled(tag)) {
            mStatus = DownloadStatus.INSTALL_CHK
            return
        }
        if (CrossProcessDownloadDataManager.hasTag(tag)) {
            CrossProcessDownloadDataManager.getInfo(ctx, tag)?.run {
                if (ptr != size)
                    mStatus = DownloadStatus.PAUSE
                else
                    mStatus = DownloadStatus.INSTALL_CHK
            }
        } else {
            mStatus = DownloadStatus.IDLE
        }
    }

    private fun initConnect() {
        mConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                Log.e("remote service", "disconnected")
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e("remote service", "connected")
                mServiceStub = IFDownloadAction.Stub.asInterface(service)
                mServiceStub.registerCK(mTag, mCK)
                mInfoDelay?.run { mServiceStub.initInfo(mTag, first, second, third) }
            }
        }
        ctx.applicationContext.bindService(Intent(ctx.applicationContext, FishDownloaderSVC::class.java), mConnection, Service.BIND_AUTO_CREATE)
    }


    /****Inner funcs****/
    private fun progressUI(pg: Double) {
        post { mProgress.layoutParams = mProgress.layoutParams.apply { this@apply.width = (this@FDownloadBar.width * pg).toInt() } }
    }

    private fun download() {
        if (!CrossProcessDownloadDataManager.mSupportNet()) {
            ZToast("正在使用数据流量，目前仅在WIFI环境下下载")
            flushUI()
            return
        }
        mStatus = DownloadStatus.DOWNLOADING
        mServiceStub.startDownload(mTag)
    }

    private fun pause() {
        mServiceStub.pauseByTag(mTag)
    }

    private fun open() {
        try {
            ctx.startActivity(ctx.packageManager.getLaunchIntentForPackage(mTag))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun flushUI() {
        H.post {
            progressUI(0.0)
            when (mStatus) {
                DownloadStatus.IDLE -> {
                    mActionTv.text = "下载"
                    onceClick(this@FDownloadBar::download)
                }
                DownloadStatus.DOWNLOADING -> {
                    mActionTv.text = "下载中"
                    onceClick(this@FDownloadBar::pause)
                }
                DownloadStatus.COMPLETE -> {
                    mActionTv.text = "安装中"
                    postDelayed({ mStatus = DownloadStatus.INSTALL_CHK }, 10000)
                }
                DownloadStatus.PAUSE -> {
                    mActionTv.text = "继续"
                    onceClick(this@FDownloadBar::download)
                    progressUI(CrossProcessDownloadDataManager.getInfo(ctx, mTag)?.run { 1.0 * ptr / size }
                            ?: return@post)
                }
                DownloadStatus.FAILED -> {
                    mActionTv.text = "失败"
                }
                DownloadStatus.INSTALL_CHK -> {
                    if (CrossProcessDownloadDataManager.isInstalled(mTag)) {
                        mActionTv.text = "打开"
                        setOnClickListener { open() }
                    } else {
                        if (CrossProcessDownloadDataManager.getInfo(ctx, mTag)?.filePath?.run { File(this).exists() } == true) {
                            mActionTv.text = "安装"
                            onceClick {
                                mStatus = DownloadStatus.COMPLETE
                                installApp(ctx, CrossProcessDownloadDataManager.getInfo(ctx, mTag)?.filePath
                                        ?: return@onceClick)
                            }
                        } else {
                            mStatus = DownloadStatus.IDLE
                        }
                    }
                }
            }
        }
    }


    /****others****/
    private enum class DownloadStatus {
        IDLE,
        DOWNLOADING,
        COMPLETE,
        PAUSE,
        FAILED,
        INSTALL_CHK,
    }

    var onceClkFlag = false
    private fun onceClick(ck: () -> Unit) {
        onceClkFlag = true
        setOnClickListener {
            onceClkFlag = false
            ck()
            if (!onceClkFlag)
                setOnClickListener {}
        }
    }

    fun ZToast(msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
    }
}

fun installApp(ctx: Context, filePath: String) = try {
    ctx.startActivity(Intent(Intent.ACTION_VIEW).run {
        setDataAndType(FromFileMultiApis(ctx, File(filePath)), "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    })
} catch (ex: Exception) {
    ex.printStackTrace()
}

fun FromFileMultiApis(context: Context, f: File): Uri{
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        return Uri.fromFile(f)
    } else {
        return FileProvider.getUriForFile(context, "com.fengye.appstore.provider", f).apply { Log.e("URI PARSER", this.toString()) }
    }
}
