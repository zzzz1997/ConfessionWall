package com.zzapp.confessionwall.model.listener

import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.view.View

/**
 * Project ConfessionWall
 * Date 2018-02-16
 *
 * 键盘弹出及退出的监听器
 *
 * @author zzzz
 */
class SoftKeyBoardListener(activity: AppCompatActivity) {

    private var rootView: View = activity.window.decorView
    private var rootViewVisibleHeight = 0
    private lateinit var onSoftKeyBoardListener: OnSoftKeyBoardChangeListener

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            val visibleHeight = r.height()
            if(rootViewVisibleHeight == 0){
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }

            if(rootViewVisibleHeight == visibleHeight){
                return@addOnGlobalLayoutListener
            }

            if(rootViewVisibleHeight - visibleHeight > 200){
                onSoftKeyBoardListener.keyBoardShow(rootViewVisibleHeight - visibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }

            if(visibleHeight - rootViewVisibleHeight > 200){
                onSoftKeyBoardListener.keyBoardHide(visibleHeight - rootViewVisibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }
        }
    }

    /**
     * 设置监听器
     *
     * @param onSoftKeyBoardListener 监听器对象
     */
    fun setOnSoftKeyBoardChangeListener(onSoftKeyBoardListener: OnSoftKeyBoardChangeListener){
        this.onSoftKeyBoardListener = onSoftKeyBoardListener
    }

    interface OnSoftKeyBoardChangeListener{
        fun keyBoardShow(height: Int)
        fun keyBoardHide(height: Int)
    }
}