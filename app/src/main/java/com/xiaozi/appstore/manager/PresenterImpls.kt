package com.xiaozi.appstore.manager

import android.app.Activity
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.view.AsyncWaiter

/**
 * Created by fish on 18-1-7.
 */
open class AppListDataPresenterImpl(private val waiter: AsyncWaiter, private val type: String, private val condition: String, private val keyword: String = "", private val onLoaded: List<DataManager.AppInfo>.(Boolean) -> Unit) : INetAppsPresenter {
    var isFirstLoad = true
    override fun load(showWaiter: Boolean, index: Int) {
        if (waiter.showing()) return
        if (showWaiter || isFirstLoad)
            waiter.show(false)
        else
            waiter.showHidden()
        isFirstLoad = false
        NetManager.loadAppList(type, condition, keyword, index, {
            appNodes.node.map { DataManager.AppInfoDM.trans(it) }.onLoaded(index == 0)
            waiter.hide(200)
        }) {
            waiter.activity::ZToast
            listOf<DataManager.AppInfo>().onLoaded(true)
            waiter.hide(200)
        }
    }
}

open class CommentListPresenterImpl(private val waiter: AsyncWaiter, private val appId: Int, private val onLoaded: Array<DataManager.Comment>.(isAppend: Boolean) -> Unit) : INetAppsPresenter {
    override fun load(showWaiter: Boolean, index: Int) {
        if (waiter.isWaiting) return
        if (showWaiter)
            waiter.show(false)
        else
            waiter.showHidden()
        NetManager.loadCommentList(appId, index, {
            DataManager.Transor.CommentTransor(this).onLoaded(index != 0)
            waiter.hide(200)
        }) {
            waiter.activity::ZToast
            arrayOf<DataManager.Comment>().onLoaded(index != 0)
            waiter.hide(200)
        }

    }

}

open class AppDetailPresenterImpl(private val mWaiter: AsyncWaiter, private val appId: Int, private val onLoaded: (data: DataManager.AppDetail) -> Unit) : IDataPresenter {
    override fun load() {
        mWaiter.show()
        NetManager.loadAppDetail(appId, {
            mWaiter.hide(200)
            onLoaded(DataManager.Transor.AppDetailTransor(this))
        }) {
            mWaiter.hide(200)
            mWaiter.activity::ZToast
        }
    }
}

class AppAdListPresenterImpl(private val mWaiter: AsyncWaiter, private val appId: Int, private val onLoaded: (data: List<DataManager.AppInfo>) -> Unit) : IDataPresenter {
    override fun load() {
        NetManager.loadAssociateApps("$appId", { onLoaded(appNodes.node.map { DataManager.AppInfoDM.trans(it) }) }, mWaiter.activity::ZToast)
    }

}

class CategoryPresenterImpl(private val type: AppListType, private val onLoaded: List<DataManager.Category>.() -> Unit) : IDataPresenter {
    override fun load() = when (type) {
        AppListType.APP -> DataManager.CategoryDM.mAppCategory.onLoaded()
        AppListType.GAME -> DataManager.CategoryDM.mGameCategory.onLoaded()
        else -> {
        }
    }
}

class SearchPresenterImpl(private val onLoaded: Array<String>.() -> Unit) : IDataPresenter {
    var isLoading = false
    override fun load() {
        if (isLoading) return
        else isLoading = true
        NetManager.loadHotWords {
            hotSearchWd.onLoaded()
            isLoading = false
        }
    }
}

class BannerPresenterImpl(private val onLoaded: List<DataManager.Banner>.() -> Unit) : IDataPresenter {
    var isLoading = false
    override fun load() {
        if (isLoading) return
        isLoading = true
        NetManager.loadBanners({
            DataManager.Transor.BannerTransor(this).onLoaded()
            isLoading = false
        }) { isLoading = false }
    }
}

class UserInfoPresenterImpl(private val activity: Activity, private val onLoaded: () -> Unit) : IDataPresenter {
    override fun load() {
        if (!AccountManager.isLoggedIn()) {
            return
        }
        if (AccountManager.userHeadIcon.isEmpty() || AccountManager.userName.isEmpty()) {
            NetManager.loadUserInfo(activity, AccountManager.uid()) { onLoaded() }
        } else {
            onLoaded()
        }
    }
}

object PresenterImpls {
    val AppInfoCachedPresenterImpl = object : ICachedDataPresenter<DataManager.AppInfo> {
        override fun get(pkg: String) = DataManager.AppInfoDM.getAppInfo(pkg)
    }
}


