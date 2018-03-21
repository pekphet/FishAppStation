package com.fish.downloader.view

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.fish.downloader.extensions.bid
import com.fish.downloader.service.DownloadService
import com.fish.fishdownloader.IDownloadCK
import com.fish.fishdownloader.IDownloader
import com.fish.fishdownloader.R
import com.fish.fishdownloader.view.ColorChangedTextView

/**
 * Created by fish on 17-9-6.
 */
class DownloadBar(ctx: Context, attrs: AttributeSet?) : FrameLayout(ctx, attrs) {

    companion object {
        val DOWNLOADING_COLOR: Int = 0xfffde179.toInt()
        val COMPLETE_COLOR: Int = 0xfffff1ba.toInt()
        val INIT_BG_COLOR: Int = 0xfffff1ba.toInt()
        val TEXT_COLOR: Int = 0xffff8a02.toInt()
    }

    init {
        View.inflate(context, R.layout.v_download_bar, this)
    }

    val mTvPG by bid<TextView>(R.id.tv_dlbar_pg)
    val mFlPG by bid<FrameLayout>(R.id.fl_dlbar_progress)
    val mBG by bid<FrameLayout>(R.id.fl_dlbar_bg)
    val mMask by bid<ImageView>(R.id.img_dlbar_mask)

    lateinit var mDlck: (type: CK_TYPE, data: String?) -> Unit
    var mTag: String = ""
    var mUrl: String = ""
    var mFileName: String = ""
    var mSize: Long = 0

    var mConf = DownloadBarConfigure { initView() }
        set(conf) {
            Log.e("SET CONF", "DO")
            field = conf
            initView()
        }
    val mCK = object : IDownloadCK.Stub() {
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {}

        override fun onPause(tag2: String?, filePath: String?, ptr: Int, size: Int) {
            if (tag2.equals(mTag)) {
                setOnClickListener {
                    onceReDownload()
                }
            }
        }

        override fun onProgress(tag2: String?, pg: Double) {
            if (tag2.equals(mTag))
                progress(pg)
        }

        override fun onComplete(tag2: String?, filePath: String?) {
            if (tag2.equals(mTag)) {
                complete(filePath)
                if (this@DownloadBar::mDlck.isInitialized)
                    mDlck(CK_TYPE.COMPLETE, filePath)
            }
        }

        override fun onFailed(tag2: String?, msg: String?) {
            if (tag2.equals(mTag)) {
                mHandler.post {
                    mTvPG.text = mConf.failedText
                }
                if (this@DownloadBar::mDlck.isInitialized)
                    mDlck(CK_TYPE.FAILED, msg)
            }
        }

        override fun onCanceled(tag2: String?) {
            if (tag2.equals(mTag)) {
                if (this@DownloadBar::mDlck.isInitialized)
                    mDlck(CK_TYPE.CANCELED, "")
                setOnClickListener {
                    onceReDownload()
                }
            }
        }
    }

    var mServiceBinder: IDownloader? = null

    val mHandler = Handler(Looper.getMainLooper())

    var mConnection: ServiceConnection? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initView()
    }

    private fun initConnect() {
        mConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                Log.e("remote service", "disconnected")
                mServiceBinder?.unregisterCB(mCK)
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e("remote service", "connected")
                mServiceBinder = IDownloader.Stub.asInterface(service)
                mServiceBinder?.registerCB(mCK)
            }
        }
        context.bindService(Intent(context, DownloadService::class.java), mConnection, Service.BIND_AUTO_CREATE)
    }

    fun restore() {
        disconnectService()
        Log.e("dl bar", "restore status of tag:$mTag, url: $mUrl")
        mTag = ""
        mUrl = ""
        mFileName = ""
        mSize = 0
        initView()
    }

    fun init(ck: (type: CK_TYPE, data: String?) -> Unit) {
        if (!this::mDlck.isInitialized)
            mDlck = ck
        initConnect()
    }

    fun initInfo(tag: String, fileName: String, fileSize: Long, url: String) {
        mTag = tag
        mFileName = fileName
        mSize = fileSize
        mUrl = url
        Log.e("dl bar", "init, tag:$mTag, url:$mUrl")
    }

    fun text() = mTvPG.text.toString()

    private fun initView() {
        mTvPG.text = mConf.initText
        mTvPG.setTextColor(mConf.textColor)
        mMask.setBackgroundResource(mConf.maskRes)
        if (mConf.baseBGRes == null) mBG.setBackgroundColor(mConf.baseBGColor) else mBG.setBackgroundResource(mConf.baseBGRes ?: return)
        if (mConf.initBGRes == null) mFlPG.setBackgroundColor(mConf.initBGColor) else mFlPG.setBackgroundResource(mConf.initBGRes ?: return)
    }

    fun restoreUI() {
        initView()
    }

    fun text(str: String) {
        mHandler.post {
            mTvPG.text = str
        }
    }

    fun onceReDownload() = setOnClickListener {
        download()
        setOnClickListener {}
    }

    fun download() = try {
        mServiceBinder?.startDownload(mUrl, mTag, mFileName, mSize)
        chgDownloadUI()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    private fun chgDownloadUI() {
        mHandler.post {
            mFlPG.layoutParams = mFlPG.layoutParams.apply { width = 0 }
            mTvPG.setTextColor(mConf.downloadingTextColor)
            if (mConf.downloadingBGRes == null) mFlPG.setBackgroundColor(mConf.downloadingBGColor) else mFlPG.setBackgroundResource(mConf.downloadingBGRes ?: return@post)
        }
    }

    private fun complete(filePath: String?) {
        mHandler.postDelayed({
            if (mConf.completeBGRes == null) mFlPG.setBackgroundColor(mConf.completeBGColor) else mFlPG.setBackgroundResource(mConf.completeBGRes ?: return@postDelayed)
            mTvPG.text = mConf.completeText
        }, 100)
    }

    private fun progress(pg: Double) {
        mHandler.post {
            mConf.pogressCK(this@DownloadBar, mFlPG, pg, mTvPG)
        }
    }

    fun disconnectService() {
        try {
            context.unbindService(mConnection ?: return)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun cancelByTag(tag: String) {
        mServiceBinder?.cancelDownloadByTag(tag)
    }

    fun cancelAll() {
        mServiceBinder?.cancelAll()
    }

    enum class CK_TYPE { COMPLETE, CANCELED, FAILED }

    data class DownloadBarConfigure(var initText: String = "下载",
                                    var downloadingText: String = "下载中",
                                    var completeText: String = "完成",
                                    var failedText: String = "下载失败",
                                    var textColor: Int = TEXT_COLOR,
                                    var downloadingTextColor: Int = TEXT_COLOR,
                                    var compileTextColor: Int = TEXT_COLOR,
                                    var initBGColor: Int = INIT_BG_COLOR,
                                    var initBGRes: Int? = null,
                                    var downloadingBGColor: Int = DOWNLOADING_COLOR,
                                    var completeBGColor: Int = COMPLETE_COLOR,
                                    var downloadingBGRes: Int? = null,
                                    var completeBGRes: Int? = null,
                                    var baseBGColor: Int = INIT_BG_COLOR,
                                    var baseBGRes: Int? = null,
                                    var maskRes: Int = R.drawable.i_download_top,
                                    var pogressCK: (parentView: View, progressBar: FrameLayout, pg: Double, colorChangableTV: TextView) -> Unit = { view, img, pg, ctv ->
                                        {
                                            if (downloadingBGRes == null) img.setBackgroundColor(downloadingBGColor) else img.setBackgroundResource(downloadingBGRes)
                                            img.layoutParams = img.layoutParams.apply { this@apply.width = (view.width * pg).toInt() }
                                            ctv.text = downloadingText

                                        }()
                                    },
                                    val notifyConfigureChanged: () -> Unit)
}