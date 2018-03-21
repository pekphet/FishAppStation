package com.xiaozi.appstore.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.umeng.analytics.MobclickAgent
import com.xiaozi.appstore.R
import com.xiaozi.appstore.component.Analisys
import kotlinx.android.synthetic.main.a_base_bar.*

/**
 * Created by fish on 18-1-9.
 */
abstract class BaseBarActivity : Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_base_bar)
        tv_base_bar_title.text = title()
        fl_base_body.addView(LayoutInflater.from(this@BaseBarActivity).inflate(layoutID(), null))
        img_base_bar_back.setOnClickListener { onBackPressed() }
    }

    abstract fun title(): String

    abstract fun layoutID(): Int

    fun changeTitle(title: String) {
        tv_base_bar_title.text = title
    }

    fun hideBack() {
        img_base_bar_back.visibility = View.GONE
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