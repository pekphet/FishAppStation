package com.xiaozi.appstore.component

import android.content.Context
import com.umeng.analytics.MobclickAgent

/**
 * Created by fish on 18-2-8.
 */
object Analisys {
    fun point(msg: String, value: String) {
        MobclickAgent.onEvent(Framework._C, msg, value)
    }

    fun resume(ctx: Context?) {
        MobclickAgent.onResume(ctx)
    }

    fun pause(ctx: Context) {
        MobclickAgent.onPause(ctx)
    }
}