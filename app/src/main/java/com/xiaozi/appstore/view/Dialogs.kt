package com.xiaozi.appstore.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.xiaozi.appstore.R
import com.xiaozi.appstore.component.Framework.Math.limitL
import com.xiaozi.appstore.Call
import com.xiaozi.appstore.safety
import com.xiaozi.appstore.safetySelf

/**
 * Created by fish on 18-1-4.
 */
object Dialogs {
    fun create(ctx: Context, layoutID: Int): Dialog {
        return Dialog(ctx, R.style.app_dialog).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(layoutID)
        }
    }

    fun createWaiter(ctx: Context, cancelable: Boolean = false): Dialog {
        return Dialog(ctx, R.style.staticDialog).apply {
            setContentView(R.layout.d_waiter)
            setCanceledOnTouchOutside(cancelable)
            setCancelable(cancelable)
        }
    }

}

class AsyncWaiter(val activity: Activity) {
    var showTime = 0L
    var dialog: Dialog? = null
    var isWaiting = false
    fun show(cancelable: Boolean = false) {
        if (dialog?.isShowing == true) return
        dialog = Dialogs.createWaiter(activity, cancelable).safetySelf(Dialog::show)
        showTime = System.currentTimeMillis()
        isWaiting = true
    }

    fun showHidden(){
        isWaiting = true
    }

    fun hide(minDelay: Long) {
        isWaiting = false
        if (dialog?.isShowing == true && !isActivityDead(activity)) {
            Call(limitL(minDelay, System.currentTimeMillis() - showTime))
            {
                dialog.safety(Dialog::cancel)
            }
        }
    }

    private fun isActivityDead(activity: Activity?) = activity == null || activity.isFinishing || activity.isDestroyed

    fun hide() {
        if (dialog?.isShowing == true && !isActivityDead(activity)) {
            isWaiting = false
            dialog.safety(Dialog::cancel)
        }
    }

    fun showing() = isWaiting
}

class CommonDialog(val ctx: Context) {
    val mDialog = Dialogs.create(ctx, R.layout.d_common)
    fun title(title: String) = mDialog.findViewById<TextView>(R.id.tv_d_common_title).safety {
        if (title.isEmpty())
            visibility = View.INVISIBLE
        else {
            visibility = View.VISIBLE
            text = title
        }
    }

    fun content(content: String) = mDialog.findViewById<TextView>(R.id.tv_d_common_content).safety {
        text = content
    }

    fun applyBtn(tip: String, ck: () -> Unit) = mDialog.findViewById<TextView>(R.id.tv_d_common_apply).safety {
        text = tip
        setOnClickListener { ck() }
    }

    fun cancelBtn(tip: String?, ck: () -> Unit) = mDialog.findViewById<TextView>(R.id.tv_d_common_cancel).safety {
        if (tip.isNullOrEmpty()) {
            visibility = View.GONE
            mDialog.findViewById<View>(R.id.v_d_common_line).visibility = View.GONE
            mDialog.findViewById<ImageView>(R.id.img_d_common_alpha).setImageResource(R.drawable.d_btn_top_single)
        } else {
            visibility = View.VISIBLE
            mDialog.findViewById<View>(R.id.v_d_common_line).visibility = View.VISIBLE
            text = tip
            setOnClickListener { ck() }
        }
    }

    fun show() = safety { mDialog.show() }
    fun hide() = safety { mDialog.hide() }
}


fun Dialog.fullShow() {
    window.decorView.setPadding(0, 0, 0, 0)
    window.attributes = window.attributes.apply {
        gravity = Gravity.BOTTOM
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
    }
    safety(Dialog::show)
}
