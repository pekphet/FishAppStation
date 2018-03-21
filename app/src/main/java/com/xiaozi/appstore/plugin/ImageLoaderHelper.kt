package com.xiaozi.appstore.plugin

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.xiaozi.appstore.ZToast
import com.xiaozi.appstore.component.Framework
import java.io.File
import java.io.FileOutputStream

/**
 * Created by fish on 17-10-23.
 */
object ImageLoaderHelper {
    val defaultDispImgOpt = DisplayImageOptions.Builder()
//                    .showImageOnLoading(loadingSrc)
//                    .showImageForEmptyUri(defaultSrc)
//                    .showImageOnFail(defaultSrc)
            .cacheInMemory(false)
            .cacheOnDisk(false)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build()
    val commonImgOpt = DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .cacheInMemory(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build()

    fun forceLoadImage(url: String, imgView: ImageView) {
        ImageLoader.getInstance().displayImage(url, imgView, defaultDispImgOpt)
    }

    fun loadImageWithCache(url: String, imgView: ImageView?) {
        ImageLoader.getInstance().displayImage(url, imgView, commonImgOpt)
    }

    fun syncLoadThumbImage(url: String) = ImageLoader.getInstance().loadImageSync(url, ImageSize(200, 200), commonImgOpt)

    fun storageBMP(img: ImageView) {
        img.isDrawingCacheEnabled = true
        val dir = Environment.getExternalStoragePublicDirectory("ad/image").apply { if (!this.exists()) mkdirs() }
        val f = File(dir, "st-${System.currentTimeMillis()}.png")
        val out = FileOutputStream(f)
        try {
            img.drawingCache.compress(Bitmap.CompressFormat.PNG, 50, out)
            out.flush()
            out.close()
            Framework._C.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)))
            MediaStore.Images.Media.insertImage(Framework._C.contentResolver, f.absolutePath, "Pic/YQZ-QR.png", null)
            img.context.ZToast("保存图片成功")
        } catch (ex: Exception) {
            img.context.ZToast("保存图片失败")
        }
    }
}