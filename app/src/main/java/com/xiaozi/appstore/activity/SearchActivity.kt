package com.xiaozi.appstore.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.component.Analisys
import com.xiaozi.appstore.dp2px
import com.xiaozi.appstore.manager.SearchPresenterImpl
import com.xiaozi.appstore.view.SearchVH
import kotlinx.android.synthetic.main.a_search.*

/**
 * Created by fish on 18-1-16.
 */
class SearchActivity : Activity() {

    lateinit var mAdapter: RecyclerView.Adapter<SearchVH>
    val mData = mutableListOf<String>()
    val mLoader = SearchPresenterImpl {
        mData.clear()
        mData.addAll(this)
        mAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_search)
        initRV()
        mLoader.load()
        tv_search.setOnClickListener {
            if (et_search.text.isEmpty()) {
                ZToast("请输入搜索内容")
                return@setOnClickListener
            }
            AppListActivity.open(this@SearchActivity, "搜索结果", "", "search", et_search.text.toString())
        }
        img_search_back.setOnClickListener { finish() }
        img_search_clear.setOnClickListener { et_search.text.clear() }
    }

    private fun initRV() {
        rv_search.layoutManager = GridLayoutManager(this@SearchActivity, 2)
        mAdapter = object : RecyclerView.Adapter<SearchVH>() {
            val params = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, dp2px(32)).apply {
                dp2px(4).let { setMargins(it, it, it, it) }
            }

            override fun onBindViewHolder(holder: SearchVH, position: Int) {
                holder.load(mData[position])
            }

            override fun getItemCount() = mData.size

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = SearchVH(TextView(this@SearchActivity).apply {
                layoutParams = params
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.rect_gray)
            })

        }
        rv_search.adapter = mAdapter
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