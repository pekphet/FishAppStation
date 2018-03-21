package com.fish.downloader.extensions

import android.app.Activity
import android.app.Dialog
import android.view.View

/**
 * Created by fish on 17-9-6.
 */
fun <T : View> Activity.bid(id: Int) = lazy { findViewById<T>(id)}
fun <T : View> View.bid(id: Int) = lazy { findViewById<T>(id)}
fun <T : View> Dialog.bid(id: Int) = lazy { findViewById<T>(id)}
