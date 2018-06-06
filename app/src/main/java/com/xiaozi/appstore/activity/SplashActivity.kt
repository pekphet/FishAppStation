package com.xiaozi.appstore.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import cc.fish.cld_ctrl.appstate.CldApp
import cc.fish.cld_ctrl.appstate.entity.RespUpdate
import com.fish.fishdownloader.view.FDownloadBar
import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.Call
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.component.Analisys
import com.xiaozi.appstore.plugin._GSON
import com.xiaozi.appstore.safetyNullable

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_splash)
        if (checkPermissions()) {
            checkUpdate()
        }
    }

    private fun checkPermissions(): Boolean {
        //如果当前api版本是8.0以及以上，请求安装权限
        if (Build.VERSION.SDK_INT >= 26) {
            //来判断应用是否有权限安装apk
            if (!this.packageManager.canRequestPackageInstalls()) {
                requestPermissions(arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES), 100)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 200)
                return false
            }
        } else {
            return true
        }
    }

    private fun checkUpdate() {
        CldApp.checkUpdateForString {
            _GSON.fromJson(it, RespUpdate::class.java).safetyNullable {
                if (it == null || this!!.is_force == 0)
                    NetManager.loadAppConfig(this@SplashActivity) { Call(2000) { HomeActivity.open(this@SplashActivity, this) } }
                else
                    Call(2000) { HomeActivity.open(this@SplashActivity, this@safetyNullable) }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if ((requestCode == 200 && grantResults!![0] == PackageManager.PERMISSION_GRANTED) || (requestCode == 100 && grantResults!![0] == PackageManager.PERMISSION_GRANTED)) {
                checkUpdate()
            } else {
                when (requestCode) {
                    100 -> ZToast("请到系统的应用权限设置管理中打开‘允许安装未知应用’权限")
                    200 -> ZToast("请到系统的应用权限设置管理中打开‘读取存储文件’权限")
                }


                finish()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        Analisys.resume(this)
    }

    override fun onPause() {
        super.onPause()
        Analisys.pause(this)
    }

}
