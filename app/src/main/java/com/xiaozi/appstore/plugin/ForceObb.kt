package com.xiaozi.appstore.plugin

import java.util.*

/**
 * Created by fish on 17-7-3.
 */
class ForceObb<T>  {
    private var mCache: T? = null
    private val mObs: Vector<TypedOB<T>> = Vector()

    fun addObserver(o: TypedOB<T>, useCache: Boolean = false) {
        mObs.addElement(o)
        if (useCache && mCache != null) {
            o.update(this@ForceObb, mCache)
        }
    }

    fun deleteObserver(o: TypedOB<T>) {
        mObs.remove(o)
    }

    fun notifyObs(data: T? = null) {
        mCache = data
        mObs.map { synchronized(this){it.update(this@ForceObb, data)} }
    }

    fun cleanCache() {
        mCache = null
    }

    fun getCache() = mCache
}