package com.zzapp.confessionwall.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.datatype.BmobRelation
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.bumptech.glide.Glide
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Comment
import com.zzapp.confessionwall.data.Post
import com.zzapp.confessionwall.utils.CommentAdapter
import com.zzapp.confessionwall.utils.User
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dynamic.*

/**
 * Project ConfessionWall
 * Date 2018-01-09
 *
 * @author zzzz
 */
class DynamicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamic)

        initView()
    }

    private fun initView(){
        val user = BmobUser.getCurrentUser(User::class.java)
        val post = intent.getSerializableExtra("post") as Post

        dynamic_toolbar.setNavigationOnClickListener {
            finish()
        }

        dynamic_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        dynamic_refresh.setOnRefreshListener {
            refresh(post, user)
        }

        dynamic_refresh.isRefreshing = true
        refresh(post, user)
    }

    private fun refresh(post: Post, user: User?){
        Glide.with(this)
                .load(post.author!!.icon)
                .into(dynamic_icon)
        dynamic_name.text = post.author!!.username
        dynamic_content.text = post.content

        val query = BmobQuery<Comment>()
        query.addWhereEqualTo("post", BmobPointer(post))
        query.order("-updatedAt")
        query.include("author")
        query.findObjects(object: FindListener<Comment>(){
            override fun done(comments: MutableList<Comment>?, p1: BmobException?) {
                if(p1 == null){
                    if(comments!!.isEmpty()){
                        dynamic_warning.visibility = View.VISIBLE
                        dynamic_warning.text = getString(R.string.find_no_comment)
                        dynamic_comments.visibility = View.GONE
                    } else {
                        val adapter = CommentAdapter(this@DynamicActivity, comments, user)
                        adapter.setOnCommentClickListener(object : CommentAdapter.MyOnCommentClickListener {
                            override fun onUserClicked(view: View, position: Int) {
                                Toasty.info(this@DynamicActivity, "点击用户" + comments[position].author!!.username).show()
                            }

                            override fun onLikeClicked(view: View, position: Int) {
                                if (user == null) {
                                    this@DynamicActivity.startActivity(Intent(this@DynamicActivity, LoginActivity::class.java))
                                } else {
                                    val parent = view.parent as View
                                    val likeQuery = BmobQuery<User>()
                                    likeQuery.addWhereRelatedTo("likes", BmobPointer(comments[position]))
                                    likeQuery.findObjects(object : FindListener<User>() {
                                        override fun done(p0: MutableList<User>?, p1: BmobException?) {
                                            if (p1 == null) {
                                                val likes = BmobRelation()
                                                val isLiked = p0 != null && p0.any { it.objectId == user.objectId }
                                                if (isLiked) {
                                                    likes.remove(user)
                                                    comments[position].likesNum--
                                                } else {
                                                    likes.add(user)
                                                    comments[position].likesNum++
                                                }
                                                comments[position].likes = likes
                                                comments[position].update(object : UpdateListener() {
                                                    override fun done(p0: BmobException?) {
                                                        if (p0 == null) {
                                                            if (!isLiked) {
                                                                parent.findViewById<ImageView>(R.id.comment_like).setImageDrawable(getDrawable(R.drawable.liked))
                                                                Toasty.success(this@DynamicActivity, getString(R.string.success_liked)).show()
                                                            } else {
                                                                parent.findViewById<ImageView>(R.id.comment_like).setImageDrawable(getDrawable(R.drawable.like))
                                                            }
                                                            parent.findViewById<TextView>(R.id.comment_liked_num).text = "" + comments[position].likesNum
                                                        } else {
                                                            Toasty.error(this@DynamicActivity, p0.message!!).show()
                                                        }
                                                    }
                                                })
                                            } else {
                                                Toasty.error(this@DynamicActivity, p1.message!!).show()
                                            }
                                        }
                                    })
                                }
                            }
                        })
                        dynamic_warning.visibility = View.GONE
                        dynamic_comments.visibility = View.VISIBLE
                        dynamic_comments.adapter = adapter
                        dynamic_comments.layoutManager = LinearLayoutManager(this@DynamicActivity)
                    }
                } else {
                    Toasty.error(this@DynamicActivity, p1.message!!) .show()
                }
            }
        })
        dynamic_refresh.isRefreshing = false
    }
}