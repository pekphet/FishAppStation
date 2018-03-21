package com.xiaozi.appstore.activity

import android.content.Context
import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.xiaozi.appstore.R
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.manager.AccountManager
import com.xiaozi.appstore.manager.CommentListPresenterImpl
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.view.AsyncWaiter
import com.xiaozi.appstore.view.CommentVH
import kotlinx.android.synthetic.main.a_comment_list.*

/**
 * Created by fish on 18-1-11.
 */
class CommentListActivity : BaseBarActivity() {
    override fun title() = "评论详情"
    override fun layoutID() = R.layout.a_comment_list

    companion object {
        val KEY_APPID = "APPID"
        val KEY_APP_PKG = "APPPKG"
        fun open(ctx: Context, appID: Int, pkg: String) {
            ctx.startActivity(Intent(ctx, CommentListActivity::class.java).apply {
                putExtra(KEY_APPID, appID)
                putExtra(KEY_APP_PKG, pkg)
            })
        }
    }

    var mAppId: Int = -1
    var mAppPkg = ""
    lateinit var mWaiter: AsyncWaiter
    lateinit var mLoader: CommentListPresenterImpl
    lateinit var mAdapter: RecyclerView.Adapter<CommentVH>

    val mData = mutableListOf<DataManager.Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initRV()
        initLoader()
        initEffects()
    }

    private fun initRV() {
        mAdapter = object : RecyclerView.Adapter<CommentVH>() {
            override fun onBindViewHolder(holder: CommentVH, position: Int) {
                holder.load(mData[position]) {
                    if (!AccountManager.isLoggedIn()) {
                        startActivity(Intent(this@CommentListActivity, LoginActivity::class.java))
                    } else {
                        NetManager.applyThumbsup(mAppId, it.id, AccountManager.uid(), it.isAgreed == 0, {
                            (this?.thumbsup?.beThumbsup == 1).apply {
                                mData[position].count = mData[position].count + if (this) 1 else -1
                                mData[position].isAgreed = if (this) 1 else 0
                            }
                            mAdapter.notifyItemChanged(position)
                        }, this@CommentListActivity::ZToast)
                    }
                }
            }

            override fun getItemCount() = mData.size

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = CommentVH(parent)

        }
        rv_comment_list.layoutManager = LinearLayoutManager(this)
        rv_comment_list.adapter = mAdapter
        swipe_comment_list.onSwipe({ mLoader.load() }) { mLoader.load(false, mData.size) }
    }

    private fun initData() {
        mAppId = intent.getIntExtra(KEY_APPID, -1)
        mAppPkg = intent.getStringExtra(KEY_APP_PKG)
        if (mAppId == -1 || mAppPkg.isEmpty())
            exit()
    }

    private fun exit() {
        ZToast("评论获取失败")
        finish()
    }

    private fun initLoader() {
        mWaiter = AsyncWaiter(this)
        mLoader = CommentListPresenterImpl(mWaiter, mAppId) {
            mData.run {
                if (!it)
                    clear()
                addAll(this@CommentListPresenterImpl)
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initEffects() {
        tv_comment_write.setOnClickListener {
            if (!Framework.Package.isInstalled(mAppPkg)) {
                ZToast("请先安装此应用")
                return@setOnClickListener
            }
            if (!AccountManager.isLoggedIn()) {
                startActivity(Intent(this@CommentListActivity, LoginActivity::class.java))
                return@setOnClickListener
            }
            fl_comment_page.visibility = View.VISIBLE
            et_comment.requestFocus()

        }
        tv_comment_apply.setOnClickListener {
            if (et_comment.text.isEmpty()) {
                ZToast("请输入评论内容")
            } else {
                fl_comment_page.visibility = View.GONE
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                NetManager.applyComment(mAppId, et_comment.text.toString(), 0, AccountManager.uid(), AccountManager.userName, {
                    ZToast("评论提交成功")
                    et_comment.text.clear()
                    mLoader.load()
                }) { this@CommentListActivity.ZToast(this) }
            }
        }
        tv_comment_cancel.setOnClickListener {
            fl_comment_page.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        mLoader.load(true)
    }
}