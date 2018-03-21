package com.xiaozi.appstore.view

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE
import android.util.AttributeSet
import android.util.Log
import com.xiaozi.appstore.Call
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.plugin.ForceObb
import com.xiaozi.appstore.plugin.TypedOB
import java.util.*

/**
 * Created by fish on 17-11-14.
 */
class LoadableSwipeLayout(ctx: Context, attrs: AttributeSet) : SwipeRefreshLayout(ctx, attrs) {

    var mRecyclerView: RecyclerView? = null
    var mLoadData: () -> Unit = {}
    var isLoading = false
    val mDataOB = object : TypedOB<Any> {
        override fun update(o: ForceObb<Any>, arg: Any?) {
            getChildAdapter()?.notifyDataSetChanged()
        }
    }
    val mAdapterOBB: ForceObb<Any> = ForceObb<Any>().apply { addObserver(mDataOB) }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mRecyclerView != null) return
        if (childCount >= 1) {
            for (i in 0..childCount - 1) {
                if (getChildAt(i) is RecyclerView) {
                    mRecyclerView = getChildAt(i) as RecyclerView
                    setRecyclerLoadListener()
                    break
                }
            }

        }
    }

    private fun setRecyclerLoadListener() {
        mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE && canLoadMore() && !isRefreshing) {
                    isLoading = true
                    mLoadData()
                }
            }
        })
    }

    private fun canLoadMore(): Boolean {
        if (mRecyclerView == null || mRecyclerView?.layoutManager !is LinearLayoutManager || !isEnabled) return false
        return (mRecyclerView?.layoutManager as LinearLayoutManager).run { findLastVisibleItemPosition() == itemCount - 1 } && !isLoading
    }

    fun getChildAdapter(): RecyclerView.Adapter<*>? = mRecyclerView?.adapter

    private var mLimPoi = 0
    fun onSwipe(refresh: () -> Unit, load: Int.() -> Unit) {
        Call(200) {
            if (mRecyclerView == null)
                return@Call
            setOnRefreshListener {
                refresh()
                Log.e("refresh", "ok")
//                mAdapterOBB.notifyObs()
                mLimPoi = 0
                isRefreshing = false
            }
            mLoadData = {
                (++mLimPoi).load()
                Log.e("load", "ok")
//                mAdapterOBB.notifyObs()
                isLoading = false
            }
        }
    }

    fun <T> onSwipeComplex(dataList: MutableList<T>, refresh: (Array<out T>.() -> Unit) -> Unit,
                           load: (offset: Int, (Array<out T>) -> Unit) -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (mRecyclerView == null) {
                Log.e("swipe", "null!!!!!")
                return@postDelayed
            }
            setOnRefreshListener {
                refresh {
                    Log.e("refresh", "ok")
                    dataList.apply {
                        clear()
                        addAll(this@refresh)
                        mAdapterOBB.notifyObs()
                        mLimPoi = 0
                        isRefreshing = false
                    }
                }
            }
            mLoadData = {
                load(++mLimPoi) {
                    Log.e("list size", "${dataList.size}")
                    dataList.addAll(it.toList())
                    Log.e("added size", "${it.size}")
                    Log.e("added list size", "${dataList.size}")
                    Log.e("load", "ok")
                    mAdapterOBB.notifyObs()
                    Handler(Looper.getMainLooper()).postDelayed({ Log.e("notified size", "${dataList.size}") }, 300)
                    isLoading = false
                }
            }
        }, 200)
    }

    override fun setOnRefreshListener(listener: OnRefreshListener?) {
        super.setOnRefreshListener(listener)
        Log.e("set refresh lsner", "OK")
    }

    fun resetOffset() {
        mLimPoi = 0
    }

    fun setSwipeAble(allow: Boolean) {
        isEnabled = allow
    }
}
