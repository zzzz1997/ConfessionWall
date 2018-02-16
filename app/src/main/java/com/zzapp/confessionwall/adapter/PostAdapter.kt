package com.zzapp.confessionwall.adapter

import android.annotation.SuppressLint
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
import com.zzapp.confessionwall.adapter.base.BaseAdapter
import com.zzapp.confessionwall.entity.Post
import com.zzapp.confessionwall.entity.User
import com.zzapp.confessionwall.view.CircleImageView

/**
 * Project ConfessionWall
 * Date 2018-01-09
 *
 * 动态列表适配器
 *
 * @author zzzz
 */
class PostAdapter(private val context: Context, private val posts: MutableList<Post>, private val user: User?)
    : BaseAdapter<Post, PostAdapter.PostViewHolder, PostAdapter.OnPostClickListener>(posts), View.OnClickListener {

    private lateinit var onPostClickListener: OnPostClickListener

    private var query = BmobQuery<User>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PostViewHolder {
        return PostViewHolder(LayoutInflater.from(context).inflate(R.layout.post, parent, false))
    }

    override fun onBindViewHolder(holder: PostViewHolder?, @SuppressLint("RecyclerView") position: Int) {
        Glide.with(context)
                .load(posts[position].author!!.icon)
                .into(holder!!.icon)
        holder.name.text = posts[position].author!!.username
        when {
            user == null -> holder.follow.text = context.getString(R.string.add_follow)
            posts[position].author!!.objectId == user.objectId -> holder.follow.text = context.getString(R.string.oneself)
            else -> {
                query = BmobQuery()
                query.addWhereRelatedTo("follow", BmobPointer(user))
                query.findObjects(object: FindListener<User>(){
                    override fun done(p0: MutableList<User>?, p1: BmobException?) {
                        if(p1 == null){
                            if(p0!!.any { it.objectId == posts[position].author!!.objectId }){
                                holder.follow.text = context.getString(R.string.followed)
                            } else {
                                holder.follow.text = context.getString(R.string.add_follow)
                            }
                        } else {
                            holder.follow.text = context.getString(R.string.add_follow)
                        }
                    }
                })
            }
        }

        holder.content.text = posts[position].content
        holder.comment.text = context.getString(R.string.comment) + posts[position].commentsNum
        query = BmobQuery()
        query.addWhereRelatedTo("likes", BmobPointer(posts[position]))
        query.include("likes")
        query.findObjects(object: FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1 == null){
                    if(user != null && p0!!.any { it.objectId == user.objectId }){
                        holder.like.text = context.getString(R.string.liked) + posts[position].likesNum
                    } else {
                        holder.like.text = context.getString(R.string.like) + posts[position].likesNum
                    }
                } else {
                    holder.like.text = context.getString(R.string.like) + posts[position].likesNum
                }
            }
        })

        holder.itemView.tag = position
        holder.user.tag = position
        holder.follow.tag = position
        holder.menu.tag = position
        holder.content.tag = position
        holder.comment.tag = position
        holder.like.tag = position
        holder.itemView.setOnClickListener(this)
        holder.user.setOnClickListener(this)
        holder.follow.setOnClickListener(this)
        holder.menu.setOnClickListener(this)
        holder.content.setOnClickListener(this)
        holder.comment.setOnClickListener(this)
        holder.like.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.post_user -> onPostClickListener.onUserClicked(p0, p0.tag as Int)
            R.id.post_follow -> onPostClickListener.onFollowClicked(p0, p0.tag as Int)
            R.id.post_menu -> onPostClickListener.onMenuClicked(p0, p0.tag as Int)
            R.id.post_comment -> onPostClickListener.onCommentClicked(p0, p0.tag as Int)
            R.id.post_like -> onPostClickListener.onLikesClicked(p0, p0.tag as Int)
            else -> onPostClickListener.onContentClicked(p0, p0.tag as Int)
        }
    }


    override fun setOnBaseClickListener(onBaseClickListener: OnPostClickListener) {
        this.onPostClickListener = onBaseClickListener
    }

    class PostViewHolder(itemView: View) : BaseAdapter.BaseViewHolder(itemView){
        val user = itemView.findViewById<LinearLayout>(R.id.post_user)!!
        val icon = itemView.findViewById<CircleImageView>(R.id.post_icon)!!
        val name = itemView.findViewById<TextView>(R.id.post_name)!!
        val follow = itemView.findViewById<TextView>(R.id.post_follow)!!
        val menu = itemView.findViewById<ImageView>(R.id.post_menu)!!
        val content = itemView.findViewById<TextView>(R.id.post_content)!!
        val comment = itemView.findViewById<TextView>(R.id.post_comment)!!
        val like = itemView.findViewById<TextView>(R.id.post_like)!!
    }

    interface OnPostClickListener : BaseAdapter.OnBaseClickListener{
        fun onUserClicked(view: View, position: Int)
        fun onFollowClicked(view: View, position: Int)
        fun onMenuClicked(view: View, position: Int)
        fun onContentClicked(view: View, position: Int)
        fun onCommentClicked(view: View, position: Int)
        fun onLikesClicked(view: View, position: Int)
    }
}