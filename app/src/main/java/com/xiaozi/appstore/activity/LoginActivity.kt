package com.xiaozi.appstore.activity

import android.os.Bundle
import com.xiaozi.appstore.R
import com.xiaozi.appstore.wxapi.WXHelper
import kotlinx.android.synthetic.main.a_login.*

/**
 * Created by fish on 18-1-15.
 */
class LoginActivity : BaseBarActivity() {
    override fun title() = "微信登录"

    override fun layoutID() = R.layout.a_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv_wxlogin.setOnClickListener{
            WXHelper.login()
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}