package com.zzapp.confessionwall.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.bumptech.glide.Glide
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Post
import com.zzapp.confessionwall.view.CircleImageView

/**
 * Project ConfessionWall
 * Date 2018-01-09
 *
 * @author zzzz
 */
class PostAdapter(private val context: Context, private val posts: MutableList<Post>, private val user: User?) : RecyclerView.Adapter<PostAdapter.PostViewHolder>(), View.OnClickListener {

    private lateinit var onPostClickListener: MyOnPostClickListener

    override fun onBindViewHolder(holder: PostViewHolder?, position: Int) {
        Glide.with(context)
                .load(posts[position].author!!.icon)
                .into(holder!!.icon)
        holder.name.text = posts[position].author!!.username
        holder.content.text = posts[position].content
        holder.comment.text = context.getString(R.string.comment) + posts[position].commentsNum
        val query = BmobQuery<User>()
        query.addWhereRelatedTo("likes", BmobPointer(posts[position]))
        query.include("likes")
        query.findObjects(object: FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1 == null){
                    if(p0 == null){
                        holder.like.text = context.getString(R.string.like) + posts[position].likesNum
                    } else {
                        if(user != null && p0.any { it.objectId == user.objectId }){
                            holder.like.text = context.getString(R.string.liked) + posts[position].likesNum
                        } else {
                            holder.like.text = context.getString(R.string.like) + posts[position].likesNum
                        }
                    }
                } else {
                    holder.like.text = context.getString(R.string.like) + posts[position].likesNum
                }
            }
        })

        holder.user.tag = position
        holder.user.setOnClickListener(this)
        holder.content.tag = position
        holder.content.setOnClickListener(this)
        holder.comment.tag = position
        holder.comment.setOnClickListener(this)
        holder.like.tag = position
        holder.like.setOnClickListener(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PostViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.post, parent, false)
        view.setOnClickListener(this)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.post_user -> onPostClickListener.onUserClicked(p0, p0.tag as Int)
            R.id.post_comment -> onPostClickListener.onCommentClicked(p0, p0.tag as Int)
            R.id.post_like -> onPostClickListener.onLikesClicked(p0, p0.tag as Int)
            else -> onPostClickListener.onContentClicked(p0, p0.tag as Int)
        }
    }

    fun setOnPostClickListener(onPostClickListener: MyOnPostClickListener){
        this.onPostClickListener = onPostClickListener
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val user = itemView.findViewById<LinearLayout>(R.id.post_user)!!
        val icon = itemView.findViewById<CircleImageView>(R.id.post_icon)!!
        val name = itemView.findViewById<TextView>(R.id.post_name)!!
        val content = itemView.findViewById<TextView>(R.id.post_content)!!
        val comment = itemView.findViewById<TextView>(R.id.post_comment)!!
        val like = itemView.findViewById<TextView>(R.id.post_like)!!
    }

    interface MyOnPostClickListener{
        fun onUserClicked(view: View, position: Int)
        fun onContentClicked(view: View, position: Int)
        fun onCommentClicked(view: View, position: Int)
        fun onLikesClicked(view: View, position: Int)
    }
}