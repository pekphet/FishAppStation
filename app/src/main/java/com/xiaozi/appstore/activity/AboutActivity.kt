package com.xiaozi.appstore.activity

import android.os.Bundle
import android.text.Html
import com.xiaozi.appstore.R
import com.xiaozi.appstore.component.GlobalData
import kotlinx.android.synthetic.main.a_about.*

/**
 * Created by fish on 18-1-15.
 */
class AboutActivity : BaseBarActivity() {
    override fun title() = "关于我们"
    override fun layoutID() = R.layout.a_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tv_about_us.text = Html.fromHtml(GlobalData.getAppConfig()?.configs?.aboutUs ?: "")
    }
}