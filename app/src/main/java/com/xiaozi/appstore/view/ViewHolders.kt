package com.xiaozi.appstore.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.fish.fishdownloader.service.DownloadRecInfo
import com.fish.fishdownloader.view.FDownloadBar
import com.nostra13.universalimageloader.core.ImageLoader
import com.xiaozi.appstore.*
import com.xiaozi.appstore.activity.AppActivity
import com.xiaozi.appstore.activity.AppListActivity
import com.xiaozi.appstore.activity.DownloadMgrActivity
import com.xiaozi.appstore.component.Framework
import com.xiaozi.appstore.component.GlobalData
import com.xiaozi.appstore.manager.AppCondition
import com.xiaozi.appstore.manager.AppListType
import com.xiaozi.appstore.manager.DataManager
import com.xiaozi.appstore.manager.NetManager
import com.xiaozi.appstore.plugin.ImageLoaderHelper

/**
 * Created by fish on 18-1-5.
 */
class HomeVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_fhome, parent, false))

    val mTvName = v.findViewById<TextView>(R.id.tv_ifhome_name)
    val mTvInfo = v.findViewById<TextView>(R.id.tv_ifhome_info)
    val mTvTip = v.findViewById<TextView>(R.id.tv_ifhome_tip)
    val mImgIcon = v.findViewById<ImageView>(R.id.img_ifhome_icon)
    val mDL = v.findViewById<FDownloadBar>(R.id.dlbar_ifhome)

    fun load(app: DataManager.AppInfo) {
        mTvName.text = app.name
        mTvTip.text = app.tip
        mTvInfo.text = "${Framework.Trans.toWan(app.installCnt)}次安装/${app.size}"
        NetManager.fastCall<String>(app.calls?.showUrl)
        ImageLoaderHelper.loadImageWithCache(app.icon, mImgIcon)
        v.setOnClickListener { AppActivity.open(v.context, app.appId, app.calls) }
        mDL.run {
            bindTag(app.pkg)
            putInfo(app.name, app.dlUrl, app.sizeInt)
            mOnStart = {NetManager.fastCall<String>(app.calls?.startUrl)}
            mOnComplete = {NetManager.fastCall<String>(app.calls?.completeUrl)}
        }
    }

    fun release() {
        mDL.release()
    }
}

class TypedAppListVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_applist, parent, false))

    val mTvName = v.findViewById<TextView>(R.id.tv_iapp_name)
    val mTvPos = v.findViewById<TextView>(R.id.tv_iapp_pos)
    val mTvCon = v.findViewById<TextView>(R.id.tv_iapp_content)
    val mImgIcon = v.findViewById<ImageView>(R.id.img_iapp_icon)
    val mDL = v.findViewById<FDownloadBar>(R.id.dlbar_iapp)

    fun load(app: DataManager.AppInfo, poi: Int) {
        mTvName.text = app.name
        if (poi > 0) {
            mTvPos.visibility = View.VISIBLE
            mTvPos.text = "$poi"
        } else
            mTvPos.visibility = View.GONE
        mTvCon.text = app.tip
        ImageLoaderHelper.loadImageWithCache(app.icon, mImgIcon)
        NetManager.fastCall<String>(app.calls?.showUrl)
        v.setOnClickListener { AppActivity.open(v.context, app.appId, app.calls) }
        mDL.run {
            mOnStart = {NetManager.fastCall<String>(app.calls?.startUrl)}
            mOnComplete = {NetManager.fastCall<String>(app.calls?.completeUrl)}
            bindTag(app.pkg)
            putInfo(app.name, app.dlUrl, app.sizeInt)
        }
    }

    fun release() {
        mDL.release()
    }
}

class CategoryVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_category, parent, false))

    val mTvName = v.findViewById<TextView>(R.id.tv_icate_name)
    val mImgIcon = v.findViewById<ImageView>(R.id.img_icate_icon)
    val mRlCate = v.findViewById<RelativeLayout>(R.id.rl_icate_cate)
    val mCateSecIdArray = arrayOf(R.id.tv_icate1, R.id.tv_icate2, R.id.tv_icate3, R.id.tv_icate4, R.id.tv_icate5, R.id.tv_icate6)

    fun load(category: DataManager.Category, type: AppListType) {
        mTvName.text = category.name
        ImageLoaderHelper.loadImageWithCache(category.icon, mImgIcon)
        mRlCate.setOnClickListener { AppListActivity.open(v.context, category.name, type.str, "class", "${category.classId}") }
        for (i in category.tabs.indices)
            category.tabs[i].safety {
                v.findViewById<TextView>(mCateSecIdArray[i]).run {
                    visibility = View.VISIBLE
                    text = name
                    onClick {
                        AppListActivity.open(v.context, name, type.str, "class", "$classId")
                    }
                }
            }
    }

    fun release() {
        for (id in mCateSecIdArray)
            v.findViewById<View>(id).visibility = View.INVISIBLE
    }
}

class CommentVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_comment, parent, false))

    val imgIcon = v.findViewById<ImageView>(R.id.img_icomment_userhead)
    val tvName = v.findViewById<TextView>(R.id.tv_icomment_username)
    val tvAgree = v.findViewById<TextView>(R.id.tv_icomment_agree)
    val tvDate = v.findViewById<TextView>(R.id.tv_icomment_date)
    val tvContent = v.findViewById<TextView>(R.id.tv_icomment_content)
    val mDrawableAgree = v.context.resources.getDrawable(R.drawable.icon_agreed).apply { setBounds(0, 0, minimumWidth, minimumHeight) }
    val mDrawableUnAgree = v.context.resources.getDrawable(R.drawable.icon_unagreed).apply { setBounds(0, 0, minimumWidth, minimumHeight) }
    fun load(data: DataManager.Comment, tvAction: TextView.(DataManager.Comment) -> Unit) {
        ImageLoaderHelper.loadImageWithCache(data.headIcon, imgIcon)
        tvName.text = data.name
        tvDate.text = data.time
        tvContent.text = data.content
        tvAgree.safety {
            text = "${data.count}"
            setCompoundDrawables(null, null, if (data.isAgreed == 1) mDrawableAgree else mDrawableUnAgree, null)
            setOnClickListener { this.tvAction(data) }
        }
    }
}

class ImageVH(v: ImageView) : RecyclerView.ViewHolder(v) {
    val img = v
    fun load(imgUrl: String) {
        ImageLoader.getInstance().displayImage(imgUrl, img)
    }
}

class DownloadingVH(private val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_downloading, parent, false))

    private val mTvName = v.findViewById<TextView>(R.id.tv_idl_name)
    private val mTvContent = v.findViewById<TextView>(R.id.tv_idl_content)
    val mDownloader = v.findViewById<FDownloadBar>(R.id.download_idl)
    @SuppressLint("SetTextI18n")

    fun load(data: DownloadRecInfo) {
        var mLastPG = 0.0
        var mLastTS = System.currentTimeMillis()
        var mCnt = 0
        mDownloader.bindTag(data.tag)
        mTvName.text = data.name
        if (data.ptr == data.size) {
            //downloaded
            mTvContent.text = "下载完成"
        } else {
            //downloading
            mTvContent.text = "${Framework.Trans.Size(data.ptr)}/${Framework.Trans.Size(data.size)}"
            mDownloader.mOnProgress = { pg ->
                if (System.currentTimeMillis() - mLastTS > 1000)
                    mTvContent.text = "${Framework.Trans.Size((data.size * pg).toInt())}/${Framework.Trans.Size(data.size)} ${
                    if (mLastPG == 0.0) {
                        0.0
                    } else {
                        pg - mLastPG
                    }.run {
                        Framework.Trans.Size((this * data.size / ((System.currentTimeMillis() - mLastTS) / 1000.0)).toInt())
                    }.also {
                        mLastPG = pg
                        mLastTS = System.currentTimeMillis()
                    }
                    }/s"
            }
            mDownloader.mOnComplete = {
                Call(1000) {
                    DownloadMgrActivity.obb.notifyObs()
                }
            }
        }
    }

    fun release() {
        mDownloader.release()
    }

    fun content(str: String) {
        mTvContent.text = str
    }
}

class SearchVH(private val v: TextView) : RecyclerView.ViewHolder(v) {
    fun load(searchWord: String) {
        v.text = searchWord
        v.setOnClickListener {
            AppListActivity.open(v.context, "搜索结果", AppListType.ALL.str, AppCondition.SEARCH.str, searchWord)
        }
    }
}


class RecyclerDividerDecor(private val ctx: Context, private val dividerSize: Int) : RecyclerView.ItemDecoration() {
    val mDividerSize = ctx.dp2px(dividerSize)
    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        if (parent?.layoutManager is LinearLayoutManager) {

            if ((parent.layoutManager as LinearLayoutManager).orientation == LinearLayoutManager.HORIZONTAL) {
                outRect?.set(0, 0, mDividerSize, 0)
            } else {
                outRect?.set(0, 0, 0, mDividerSize)
            }
        }
    }
}



class HomeGridVH(val v: View) : RecyclerView.ViewHolder(v) {
    constructor(parent: ViewGroup?) : this(LayoutInflater.from(parent?.context).inflate(R.layout.i_fhome_grid, parent, false))

    val mTvName = v.findViewById<TextView>(R.id.tv_ifhome_grid_name)
    val mImgIcon = v.findViewById<ImageView>(R.id.img_ifhome_grid_icon)
    val mDL = v.findViewById<FDownloadBar>(R.id.fdownloader_ifhome_grid)

    fun load(app: DataManager.AppInfo) {
        mTvName.text = app.name
        ImageLoaderHelper.loadImageWithCache(app.icon, mImgIcon)
        v.setOnClickListener { AppActivity.open(v.context, app.appId, app.calls) }
        mDL.run {
            mOnStart = {NetManager.fastCall<String>(app.calls?.startUrl)}
            mOnComplete = {NetManager.fastCall<String>(app.calls?.completeUrl)}
            bindTag(app.pkg)
            putInfo(app.name, app.dlUrl, app.sizeInt)

        }
    }

    fun release() {
        mDL.release()
    }
}