package com.xiaozi.appstore.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.dp2px
import com.xiaozi.appstore.manager.*
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import com.xiaozi.appstore.safetyNullable
import com.xiaozi.appstore.view.AsyncWaiter
import com.xiaozi.appstore.view.HomeVH
import com.xiaozi.appstore.view.ImageVH
import com.xiaozi.appstore.view.RecyclerDividerDecor
import kotlinx.android.synthetic.main.a_app.*
import kotlinx.android.synthetic.main.i_applist.*

/**
 * Created by fish on 18-1-9.
 */
class AppActivity : BaseBarActivity() {
    override fun title() = "应用市场"
    override fun layoutID() = R.layout.a_app
    lateinit var mData: DataManager.AppDetail
    var mAdData: MutableList<DataManager.AppInfo> = mutableListOf()
    lateinit var mLoader: IDataPresenter
    lateinit var mAdsLoader: IDataPresenter
    lateinit var mWaiter: AsyncWaiter
    lateinit var mAdapter: RecyclerView.Adapter<ImageVH>
    lateinit var mAdAdapter: RecyclerView.Adapter<HomeVH>

    companion object {
        val KEY_APPID = "appID"
        fun open(ctx: Context, appId: Int) {
            ctx.startActivity(Intent(ctx, AppActivity::class.java).apply {
                putExtra(KEY_APPID, appId)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLoader()
        mLoader.load()
    }

    private fun initLoader() {
        mWaiter = AsyncWaiter(this)
        mLoader = AppDetailPresenterImpl(mWaiter, getAppId()) { mData = it; initView() }
        mAdsLoader = AppAdListPresenterImpl(mWaiter, getAppId()) { mAdData.addAll(it); mAdAdapter.notifyDataSetChanged() }
    }

    private fun getAppId() = intent.getIntExtra(KEY_APPID, -1).apply {
        if (this == -1)
            exit()
    }

    private fun initView() {
        if (!this::mData.isInitialized) exit()
        tv_iapp_name.text = mData.name
        tv_iapp_content.text = mData.tip
        tv_iapp_pos.visibility = View.GONE
        tv_app_info.text = mData.content
        tv_app_update_info.text = mData.updateLog
        ImageLoaderHelper.loadImageWithCache(mData.icon, img_iapp_icon)
        tv_app_chat.text = "${mData.commentCnt}"
        rl_app_comment.setOnClickListener { CommentListActivity.open(this, mData.appId, mData.pkg) }
        dlbar_iapp.run {
            bindTag(mData.pkg)
            putInfo(mData.name, mData.dlUrl, 100)
        }
        changeTitle(mData.name)
        initRV()
    }

    private fun initRV() {
        mAdapter = object : RecyclerView.Adapter<ImageVH>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = ImageVH(ImageView(this@AppActivity).apply {
                layoutParams = ViewGroup.LayoutParams(dp2px(140), dp2px(240))
                scaleType = ImageView.ScaleType.FIT_XY
            })

            override fun getItemCount() = mData.imgs.size

            override fun onBindViewHolder(holder: ImageVH, position: Int) {
                holder.load(mData.imgs[position])
            }
        }
        mAdAdapter = object : RecyclerView.Adapter<HomeVH>() {
            override fun getItemCount() = mAdData.size

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = HomeVH(parent)

            override fun onBindViewHolder(holder: HomeVH?, position: Int) {
                holder?.load(mAdData[position])
            }

            override fun onViewRecycled(holder: HomeVH?) {
                super.onViewRecycled(holder)
                if (holder == null) return
                try {
                    holder.release()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        rv_app.run {
            layoutManager = LinearLayoutManager(this@AppActivity, LinearLayoutManager.HORIZONTAL,false)
            adapter = mAdapter
            addItemDecoration(RecyclerDividerDecor(this@AppActivity, 4))
        }
        rv_app_adv.run {
            layoutManager = LinearLayoutManager(this@AppActivity)
            adapter = mAdAdapter
            mAdAdapter.notifyDataSetChanged()
        }
        mAdsLoader.load()
    }

    override fun onDestroy() {
        super.onDestroy()
//        dlbar_iapp.release()
    }

    private fun exit() {
        ZToast("应用信息错误")
        finish()
    }
}