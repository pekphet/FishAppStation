package com.xiaozi.appstore.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.fish.fishdownloader.service.CrossProcessDownloadDataManager
import com.fish.fishdownloader.service.DownloadRecInfo
import com.xiaozi.appstore.R
import com.xiaozi.appstore.forkList
import com.xiaozi.appstore.plugin.ForceObb
import com.xiaozi.appstore.plugin.TypedOB
import com.xiaozi.appstore.view.DownloadingVH
import kotlinx.android.synthetic.main.a_downloadmgr.*

/**
 * Created by fish on 18-1-16.
 */
class DownloadMgrActivity : BaseBarActivity() {
    override fun title() = "下载管理"
    override fun layoutID() = R.layout.a_downloadmgr

    val mDrawableTab by lazy { resources.getDrawable(R.drawable.icon_linebar).apply { setBounds(0, 0, minimumWidth, minimumHeight) } }
    val mDrawableTabWhite by lazy { resources.getDrawable(R.drawable.icon_linebar_white).apply { setBounds(0, 0, minimumWidth, minimumHeight) } }

    val mDownloadingList = mutableListOf<DownloadRecInfo>()
    val mDownloadedList = mutableListOf<DownloadRecInfo>()
    lateinit var mDownloadingAdapter: RecyclerView.Adapter<DownloadingVH>
    lateinit var mDownloadedAdapter: RecyclerView.Adapter<DownloadingVH>
    var mOB: TypedOB<Any> = object : TypedOB<Any> {
        override fun update(o: ForceObb<Any>, arg: Any?) {
            flushList()
        }
    }

    companion object {
        val obb = ForceObb<Any>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRV()
        flushList()
        initEffect()
        checkTab(0)
        obb.addObserver(mOB)
    }

    private fun initEffect() {
        tv_dlmgr_tab_downloading.setOnClickListener { checkTab(0) }
        tv_dlmgr_tab_downloaded.setOnClickListener { checkTab(1) }
    }

    private fun initRV() {
        mDownloadedAdapter = object : RecyclerView.Adapter<DownloadingVH>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = DownloadingVH(parent)

            override fun onBindViewHolder(holder: DownloadingVH, position: Int) {
                holder.load(mDownloadedList[position])
            }

            override fun getItemCount() = mDownloadedList.size

            override fun onViewRecycled(holder: DownloadingVH?) {
                super.onViewRecycled(holder)
                holder?.release()
            }
        }
        mDownloadingAdapter = object : RecyclerView.Adapter<DownloadingVH>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = DownloadingVH(parent)

            override fun onBindViewHolder(holder: DownloadingVH, position: Int) {
                holder.load(mDownloadingList[position])
            }

            override fun getItemCount() = mDownloadingList.size
            override fun onViewRecycled(holder: DownloadingVH?) {
                super.onViewRecycled(holder)
                holder?.release()
            }
        }
        rv_download.layoutManager = LinearLayoutManager(this)

    }

    private fun flushList() {
         CrossProcessDownloadDataManager.getAllInfo(this@DownloadMgrActivity).forkList({ size == ptr }) { tList, fList ->
            run {
                mDownloadedList.clear()
                mDownloadingList.clear()
                mDownloadedList.addAll(tList)
                mDownloadingList.addAll(fList)
                mDownloadedAdapter.notifyDataSetChanged()
                mDownloadingAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun checkTab(index: Int) {
        tv_dlmgr_tab_downloading.setCompoundDrawables(null, null, null, if (index == 0) mDrawableTab else mDrawableTabWhite)
        tv_dlmgr_tab_downloaded.setCompoundDrawables(null, null, null, if (index == 1) mDrawableTab else mDrawableTabWhite)
        rv_download.adapter = if (index == 0) mDownloadingAdapter else mDownloadedAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        obb.deleteObserver(mOB)
    }

}