package com.fish.fishdownloader.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import com.fish.fishdownloader.IFDownloadAction
import com.fish.fishdownloader.IFDownloadCallbacks
import com.google.gson.reflect.TypeToken

/**
 * Created by fish on 18-1-25.
 */
object CrossProcessDownloadDataManager {

    lateinit var mConnection: ServiceConnection
    lateinit var mServiceStub: IFDownloadAction
    fun initControlConnection(context: Context) {
        mConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                Log.e("remote service", "disconnected")
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.e("remote service", "connected")
                mServiceStub = IFDownloadAction.Stub.asInterface(service)
                mServiceStub.registerCK("control", object : IFDownloadCallbacks.Stub(){
                    override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
                    }

                    override fun onProgress(pg: Double) {
                    }

                    override fun onComplete(filePath: String?) {
                    }

                    override fun onFailed(msg: String?) {
                    }

                    override fun onCanceled(msg: String?) {
                    }

                    override fun onPause(msg: String?) {
                    }
                })
            }
        }
        context.applicationContext.bindService(Intent(context.applicationContext, FishDownloaderSVC::class.java), mConnection, Service.BIND_AUTO_CREATE)
    }
    fun getAllInfo(ctx: Context): List<DownloadRecInfo> {
        if (!this::mServiceStub.isInitialized) return listOf()
        return FishDownloaderSVC.GSON.fromJson(mServiceStub.getAbsFilePath("all"), object : TypeToken<List<DownloadRecInfo>>(){}.type)
    }
    fun getInfo(ctx: Context, pkg: String): DownloadRecInfo? {
        if (!this::mServiceStub.isInitialized) return null
        return FishDownloaderSVC.GSON.fromJson(mServiceStub.getAbsFilePath(pkg), DownloadRecInfo::class.java)
    }
    fun hasTag(pkg: String) = mServiceStub.hasTag(pkg)

    fun clearCKS() = if (this::mServiceStub.isInitialized) {
        mServiceStub.unregisterAllCKs()
    } else {

    }

    lateinit var mInstallCK : () -> List<String>
    lateinit var mSupportNet : () -> Boolean
    fun installInfoCK(ck: () -> List<String>) {
        mInstallCK = ck
    }
    fun netSupportCK(ck: () -> Boolean) {
        mSupportNet = ck
    }
    fun isInstalled(pkg: String) = pkg in mInstallCK()
}