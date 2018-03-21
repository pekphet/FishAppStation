package com.fish.fishdownloader.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout

/**
 * Created by fish on 18-1-25.
 */
class BlockLinearLayout(ctx: Context, attr: AttributeSet) : LinearLayout(ctx, attr) {
    override fun performClick(): Boolean {
        return true
    }
}