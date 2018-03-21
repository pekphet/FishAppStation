package com.xiaozi.appstore.activity.fragments

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import cc.fish.coreui.BaseFragment
import com.xiaozi.appstore.R
import com.xiaozi.appstore.activity.*
import com.xiaozi.appstore.manager.AccountManager
import com.xiaozi.appstore.manager.ConfManager
import com.xiaozi.appstore.manager.IDataPresenter
import com.xiaozi.appstore.manager.UserInfoPresenterImpl
import com.xiaozi.appstore.plugin.ForceObb
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import com.xiaozi.appstore.plugin.TypedOB
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.safetySelf

/**
 * Created by fish on 18-1-4.
 */
class MineFragment : BaseFragment() {

    companion object {
        val EventPoster = ForceObb<Any>()
    }

    lateinit var mUserImg: ImageView
    lateinit var mMemIcon: ImageView
    lateinit var mWifiImg: ImageView
    lateinit var mUserName: TextView
    lateinit var mUserAction: TextView
    lateinit var mMem: TextView
    lateinit var mLLLogin: LinearLayout
    lateinit var mLoader: IDataPresenter

    val mOb = object : TypedOB<Any> {
        override fun update(o: ForceObb<Any>, arg: Any?) {
            try {
               onSelected()
            } catch (ex: Exception) {
            }
        }
    }


    override fun initView(inflater: LayoutInflater) = inflater.inflate(R.layout.f_mine, null).safetySelf {
        mUserImg = findViewById(R.id.img_fmine_head)
        mWifiImg = findViewById(R.id.img_fmine_wifi)
        mUserName = findViewById(R.id.tv_fmine_name)
        mUserAction = findViewById(R.id.tv_fmine_action)
        mLLLogin = findViewById<LinearLayout>(R.id.ll_fmine_login)
        mMem = findViewById(R.id.tv_fmine_mem)
        mMemIcon = findViewById(R.id.img_fmine_mem)
        mLoader = UserInfoPresenterImpl(activity) {
            ImageLoaderHelper.loadImageWithCache(AccountManager.userHeadIcon, mUserImg)
            mUserName.text = AccountManager.userName
        }
        initEffects(this)
        EventPoster.addObserver(mOb)
    }

    private fun initEffects(view: View) {
        view.safety {
            findViewById<RelativeLayout>(R.id.rl_fmine_download).setOnClickListener { startActivity(Intent(activity, DownloadMgrActivity::class.java)) }
            findViewById<RelativeLayout>(R.id.rl_fmine_feedback).setOnClickListener { startActivity(Intent(activity, FeedbackActivity::class.java)) }
            findViewById<RelativeLayout>(R.id.rl_fmine_about).setOnClickListener { startActivity(Intent(activity, AboutActivity::class.java)) }
            findViewById<FrameLayout>(R.id.fl_fmine_search).setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java)) }
            findViewById<RelativeLayout>(R.id.fl_fmine_wifi).setOnClickListener {
                mWifiImg.setImageResource(if (ConfManager.isOnlyWifi()) R.drawable.switch_off else R.drawable.switch_on)
                ConfManager.setOnlyWifi(!ConfManager.isOnlyWifi())
            }
        }
        mMemIcon.setOnClickListener { flushMem() }
    }


    override fun onSelected() {
        flushMem()
        if (!AccountManager.isLoggedIn()) {
            mLLLogin.setOnClickListener { activity.startActivity(Intent(activity, LoginActivity::class.java)) }
            mUserAction.apply {
                text = "登录"
                setOnClickListener {
                    activity.startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
        } else {
            mLLLogin.setOnClickListener {}
            mUserAction.apply {
                text = "退出"
                setOnClickListener {
                    AccountManager.logout()
                    mUserImg.setImageResource(R.drawable.icon_unlogin_img)
                    mUserName.text = "未登录"
                    onSelected()
                }
            }
        }
        mLoader.load()
    }

    fun flushMem() {
        val mi = ActivityManager.MemoryInfo()
        (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(mi)
        mMem.text = String.format("%.1f%%", mi.availMem * 100.0 / mi.totalMem)
        mMemIcon.setImageResource(if (mi.availMem * 1.0 / mi.totalMem > 0.2) R.drawable.wode1 else R.drawable.wode2)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventPoster.deleteObserver(mOb)
    }

}