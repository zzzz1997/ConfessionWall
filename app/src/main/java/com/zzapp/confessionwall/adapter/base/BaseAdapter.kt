package com.zzapp.confessionwall.adapter.base

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Project ConfessionWall
 * Date 2018-01-29
 *
 * RecyclerView的基础适配器
 *
 * @author zzzz
 */
abstract class BaseAdapter<in T, R: BaseAdapter.BaseViewHolder, in C: BaseAdapter.OnBaseClickListener>
    (private val lists: MutableList<T>) : RecyclerView.Adapter<R>(), View.OnClickListener {

    override fun getItemCount(): Int {
        return lists.size
    }

    /**
     * RecyclerView插入操作
     *
     * @param bmobObject 插入对象
     * @param position 插入位置
     */
    fun insert(bmobObject: T, position: Int){
        lists.add(position, bmobObject)
        notifyItemInserted(position)
        notifyItemRangeChanged(position, lists.size - position)
    }
    /**
     * RecyclerView删除操作
     *
     * @param position 删除位置
     */
    fun delete(position: Int){
        lists.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, lists.size - position)
    }
    /**
     * RecyclerView局部刷新
     *
     * @param position 刷新位置
     */
    fun fresh(position: Int){
        notifyItemChanged(position)
    }

    /**
     * 设置RecyclerView的点击监听器
     *
     * @param onBaseClickListener 监听器对象
     */
    abstract fun setOnBaseClickListener(onBaseClickListener: C)

    //继承于ViewHolder的基础ViewHolder
    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    //自定义点击事件接口
    interface OnBaseClickListener
}