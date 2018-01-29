package com.zzapp.confessionwall.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import cn.bmob.v3.BmobObject

/**
 * Project ConfessionWall
 * Date 2018-01-29
 *
 * RecyclerView的基础适配器
 *
 * @author zzzz
 */
abstract class BaseAdapter<in T: BmobObject, R: BaseAdapter.BaseViewHolder, in C: BaseAdapter.OnBaseClickListener>
    (private val lists: MutableList<T>) : RecyclerView.Adapter<R>(), View.OnClickListener {

    override fun getItemCount(): Int {
        return lists.size
    }

    /**
     * 设置RecyclerView的点击监听器
     *
     * @param onBaseClickListener 监听器对象
     */
    abstract fun setOnBaseClickListener(onBaseClickListener: C)
    /**
     * RecyclerView插入操作
     *
     * @param bmobObject 插入对象
     * @param position 插入位置
     */
    abstract fun insert(bmobObject: T, position: Int)
    /**
     * RecyclerView删除操作
     *
     * @param position 删除位置
     */
    abstract fun delete(position: Int)

    //继承于ViewHolder的基础ViewHolder
    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    //自定义点击事件接口
    interface OnBaseClickListener
}