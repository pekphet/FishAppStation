package com.xiaozi.appstore.manager

/**
 * Created by fish on 18-1-7.
 */
interface INetAppsPresenter {
    fun load(showWaiter: Boolean = false, index: Int = 0)
}

interface ICachedDataPresenter<out T> {
    fun get(tag: String): T?
}

interface IDataPresenter {
    fun load()
}