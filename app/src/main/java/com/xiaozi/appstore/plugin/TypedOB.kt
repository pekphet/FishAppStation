package com.xiaozi.appstore.plugin

import java.util.*

/**
 * Created by fish on 18-1-5.
 */
interface TypedOB<T> {
    fun update(o: ForceObb<T>, arg: T?)
}