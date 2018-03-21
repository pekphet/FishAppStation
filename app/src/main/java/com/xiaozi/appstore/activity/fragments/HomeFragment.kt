package com.xiaozi.appstore.activity.fragments

import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import cc.fish.coreui.BaseFragment
import com.jude.rollviewpager.RollPagerView
import com.jude.rollviewpager.adapter.StaticPagerAdapter
import com.xiaozi.appstore.R
import com.xiaozi.appstore.activity.SearchActivity
import com.xiaozi.appstore.activity.WebActivity
import com.xiaozi.appstore.manager.*
import com.xiaozi.appstore.plugin.ImageLoaderHelper
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.safetySelf
import com.xiaozi.appstore.view.AsyncWaiter
import com.xiaozi.appstore.view.HomeGridVH
import com.xiaozi.appstore.view.HomeVH

/**
 * Created by fish on 18-1-4.
 */
class HomeFragment : BaseFragment() {
    val mDrawableTab by lazy { resources.getDrawable(R.drawable.icon_linebar).apply { setBounds(0, 0, minimumWidth, minimumHeight) } }
    val mDrawableTabWhite by lazy { resources.getDrawable(R.drawable.icon_linebar_white).apply { setBounds(0, 0, minimumWidth, minimumHeight) } }
    val mDefaultItemLP = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    lateinit var mTvTabApp: TextView
    lateinit var mTvTabGame: TextView
    lateinit var mRPV: RollPagerView
    lateinit var mRV: RecyclerView
    lateinit var mFLSearch: FrameLayout
    lateinit var mAppLoader: INetAppsPresenter
    lateinit var mGameLoader: INetAppsPresenter
    lateinit var mBannerLoader: IDataPresenter
    lateinit var mWaiter: AsyncWaiter
    val mData: MutableList<DataManager.AppInfo> = mutableListOf()

    override fun initView(inflater: LayoutInflater) = inflater.inflate(R.layout.f_home, null).safetySelf {
        mTvTabApp = findViewById(R.id.tv_fhome_tab_app)
        mTvTabGame = findViewById(R.id.tv_fhome_tab_game)
        mRPV = findViewById(R.id.rp_fhome_top)
        mFLSearch = findViewById(R.id.fl_fmain_search)
        mRV = findViewById(R.id.rv_fhome)
        mRV.layoutManager = GridLayoutManager(activity, 3)
//        mRV.addItemDecoration(DividerItemDecoration(activity, VERTICAL))
        mRV.adapter = mAdapter
        mRV.isNestedScrollingEnabled = false
        initDataLoader()
        checkTab(0)
        initEffects()
    }

    private fun initDataLoader() {
        mWaiter = AsyncWaiter(activity)
        mAppLoader = AppListDataPresenterImpl(mWaiter, AppListType.APP.str, AppCondition.HOT.str) {
            mData.safety {
                if (it)
                    clear()
                addAll(this@AppListDataPresenterImpl)
            }
            mAdapter.notifyDataSetChanged()
        }
        mGameLoader = AppListDataPresenterImpl(mWaiter, AppListType.GAME.str, AppCondition.HOT.str) {
            mData.safety {
                if (it)
                    clear()
                addAll(this@AppListDataPresenterImpl)
            }
            mAdapter.notifyDataSetChanged()
        }
        mBannerLoader = BannerPresenterImpl(this::freshRPV)
        mBannerLoader.load()
    }

    private fun freshRPV(banner: List<DataManager.Banner>) {
        mRPV.apply {
            setPlayDelay(3000)
            setAnimationDurtion(300)
            setAdapter(object : StaticPagerAdapter() {
                override fun getView(container: ViewGroup?, position: Int) = ImageView(container?.context).apply {
                    ImageLoaderHelper.loadImageWithCache(banner[position].img, this)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = mDefaultItemLP
                    setOnClickListener { banner[position].link.run { if (isNotBlank()) WebActivity.start(activity, this) } }
                }

                override fun getCount() = banner.size
            })
        }
    }

    private fun initEffects() {
        mTvTabApp.setOnClickListener { checkTab(0) }
        mTvTabGame.setOnClickListener { checkTab(1) }
        mFLSearch.setOnClickListener { activity.startActivity(Intent(activity, SearchActivity::class.java)) }
    }

    private fun checkTab(index: Int) {
        if (mWaiter.isWaiting)
            return
        mTvTabApp.setCompoundDrawables(null, null, null, if (index == 0) mDrawableTab else mDrawableTabWhite)
        mTvTabGame.setCompoundDrawables(null, null, null, if (index == 1) mDrawableTab else mDrawableTabWhite)
        if (index == 0) {
            mAppLoader.load(true)
        } else if (index == 1) {
            mGameLoader.load(true)
        }
    }

    val mAdapter = object : RecyclerView.Adapter<HomeGridVH>() {
        override fun getItemCount() = mData.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = HomeGridVH(parent)

        override fun onBindViewHolder(holder: HomeGridVH?, position: Int) {
            holder?.load(mData[position])
        }

        override fun onViewRecycled(holder: HomeGridVH?) {
            super.onViewRecycled(holder)
            if (holder == null) return
            try {
                holder.release()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onActivityResume() {
        super.onActivityResume()
        mAdapter.notifyDataSetChanged()
    }

    override fun onSelected() {
        super.onSelected()
        mAdapter.notifyDataSetChanged()
    }
}