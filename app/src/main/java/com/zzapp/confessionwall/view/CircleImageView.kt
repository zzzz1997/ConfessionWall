package com.zzapp.confessionwall.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.support.v7.widget.AppCompatImageView

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * 一个圆形的图片控件
 * 参考@caizhiming的java代码
 *
 * @author zzzz
 */

class CircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        AppCompatImageView(context, attrs, defStyle) {

    private val paint: Paint = Paint()

    override fun onDraw(canvas: Canvas) {

        val drawable = drawable
        if (null != drawable) {
            val bitmap = (drawable as BitmapDrawable).bitmap
            val b = getCircleBitmap(bitmap)
            val rectSrc = Rect(0, 0, b.width, b.height)
            val rectDest = Rect(0, 0, width, height)
            paint.reset()
            canvas.drawBitmap(b, rectSrc, rectDest, paint)

        } else {
            super.onDraw(canvas)
        }
    }

    /**
     * 获取圆形图片方法
     *
     * @param bitmap 传入原始图像
     * @return 返回圆形bitmap
     */
    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe

        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        val x = bitmap.width

        canvas.drawCircle((x / 2).toFloat(), (x / 2).toFloat(), (x / 2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}
