package com.zzapp.confessionwall.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.bumptech.glide.Glide
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.entity.Comment
import com.zzapp.confessionwall.entity.User

/**
 * Project ConfessionWall
 * Date 2018-01-12
 *
 * 评论消息的适配器
 *
 * @author zzzz
 */
class CommentAdapter(private val context: Context, private val comments: MutableList<Comment>, private val user: User?)
    : BaseAdapter<Comment, CommentAdapter.CommentViewHolder, CommentAdapter.OnCommentClickListener>(comments), View.OnClickListener {

    private lateinit var onCommentClickListener: OnCommentClickListener

    private var query = BmobQuery<User>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CommentViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.comment, parent, false)
        view.setOnClickListener(this)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder?, position: Int) {
        Glide.with(context)
                .load(comments[position].author!!.icon)
                .into(holder!!.icon)
        holder.name.text = comments[position].author!!.username
        if(user == null){
            holder.like.setImageDrawable(context.getDrawable(R.drawable.like))
        } else {
            query = BmobQuery()
            query.addWhereRelatedTo("likes", BmobPointer(comments[position]))
            query.findObjects(object: FindListener<User>(){
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if(p1 == null){
                        if (p0!!.any { it.objectId == user.objectId }){
                            holder.like.setImageDrawable(context.getDrawable(R.drawable.liked))
                        } else {
                            holder.like.setImageDrawable(context.getDrawable(R.drawable.like))
                        }
                    } else {
                        holder.like.setImageDrawable(context.getDrawable(R.drawable.like))
                    }
                }
            })
        }
        holder.likeNum.text = comments[position].likesNum.toString()
        holder.content.text = comments[position].content


        holder.user.tag = position
        holder.like.tag = position
        holder.likeNum.tag = position
        holder.content.tag = position
        holder.user.setOnClickListener(this)
        holder.like.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.comment_like -> onCommentClickListener.onLikeClicked(p0, p0.tag as Int)
            else -> onCommentClickListener.onUserClicked(p0, p0.tag as Int)
        }
    }

    override fun setOnBaseClickListener(onBaseClickListener: OnCommentClickListener) {
        this.onCommentClickListener = onBaseClickListener
    }

    override fun insert(bmobObject: Comment, position: Int){
        comments.add(position, bmobObject)
        notifyItemInserted(position)
        notifyItemRangeChanged(position, comments.size - position)
    }

    override fun delete(position: Int){
        comments.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, comments.size - position)
    }

    class CommentViewHolder(itemView: View) : BaseAdapter.BaseViewHolder(itemView){
        val user = itemView.findViewById<LinearLayout>(R.id.comment_user)!!
        val icon = itemView.findViewById<ImageView>(R.id.comment_icon)!!
        val name = itemView.findViewById<TextView>(R.id.comment_name)!!
        val like = itemView.findViewById<ImageView>(R.id.comment_like)!!
        val likeNum = itemView.findViewById<TextView>(R.id.comment_liked_num)!!
        val content = itemView.findViewById<TextView>(R.id.comment_content)!!
    }

    interface OnCommentClickListener : BaseAdapter.OnBaseClickListener{
        fun onUserClicked(view: View, position: Int)
        fun onLikeClicked(view: View, position: Int)
    }
}