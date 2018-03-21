package com.xiaozi.appstore.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.fish.fishdownloader.service.CrossProcessDownloadDataManager
import com.xiaozi.appstore.R
import com.xiaozi.appstore.manager.AppListDataPresenterImpl
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.INetAppsPresenter
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.view.AsyncWaiter
import com.xiaozi.appstore.view.TypedAppListVH
import kotlinx.android.synthetic.main.a_app_list.*

/**
 * Created by fish on 18-1-10.
 */
class AppListActivity : BaseBarActivity() {
    companion object {
        val KEY_TITLE = "title"
        val KEY_TYPE = "type"
        val KEY_CONDITION = "condition"
        val KEY_KEYWORD = "keyword"
        fun open(ctx: Context, title: String, type: String, condition: String, keyword: String) {
            ctx.startActivity(Intent(ctx, AppListActivity::class.java).apply {
                putExtra(KEY_TITLE, title)
                putExtra(KEY_TYPE, type)
                putExtra(KEY_CONDITION, condition)
                putExtra(KEY_KEYWORD, keyword)
            })
        }
    }

    override fun title() = "搜索结果"
    override fun layoutID() = R.layout.a_app_list

    val mData: MutableList<DataManager.AppInfo> = mutableListOf()
    lateinit var mLoader: INetAppsPresenter
    lateinit var mWaiter: AsyncWaiter
    lateinit var mAdapter: RecyclerView.Adapter<TypedAppListVH>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mWaiter = AsyncWaiter(this)
        changeTitle(intent.getStringExtra(KEY_TITLE))
        initRV()
        initLoader()
        mLoader.load()
    }

    private fun initRV() {
        swipe_applist.onSwipe({}) { mLoader.load(false, mData.size) }
        mAdapter = object : RecyclerView.Adapter<TypedAppListVH>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = TypedAppListVH(parent)

            override fun onBindViewHolder(holder: TypedAppListVH, position: Int) {
                mData[position].run {
                    holder.load(this, -1)
                }
            }

            override fun getItemCount() = mData.size

            override fun onViewRecycled(holder: TypedAppListVH?) {
                super.onViewRecycled(holder)
                holder?.release()
            }
        }
        rv_applist.run {
            layoutManager = LinearLayoutManager(this@AppListActivity)
            addItemDecoration(DividerItemDecoration(this@AppListActivity, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }
    }

    private fun initLoader() {
        intent.apply {
            mLoader = AppListDataPresenterImpl(mWaiter, intent.getStringExtra(KEY_TYPE), intent.getStringExtra(KEY_CONDITION), intent.getStringExtra(KEY_KEYWORD)) {
                mData.safety {
                    if (it) clear()
                    if (this@AppListDataPresenterImpl.isEmpty()) return@safety
                    addAll(this@AppListDataPresenterImpl)
                    if (it) mAdapter.notifyDataSetChanged()
                    else mAdapter.notifyItemRangeInserted(this.size - this@AppListDataPresenterImpl.size, this@AppListDataPresenterImpl.size)
                    img_applist_none.visibility = if (mData.isEmpty()) View.VISIBLE else View.GONE
                }
                mAdapter.notifyDataSetChanged()
            }
        }
    }

}