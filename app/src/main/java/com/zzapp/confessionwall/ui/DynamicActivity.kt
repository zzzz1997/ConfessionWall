package com.zzapp.confessionwall.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.datatype.BmobRelation
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.bumptech.glide.Glide
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Comment
import com.zzapp.confessionwall.data.Post
import com.zzapp.confessionwall.utils.CommentAdapter
import com.zzapp.confessionwall.data.User
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dynamic.*

/**
 * Project ConfessionWall
 * Date 2018-01-09
 *
 * 单个动态的显示界面
 *
 * @author zzzz
 */
class DynamicActivity : AppCompatActivity() {

    private lateinit var adapter: CommentAdapter

    private var isOperation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamic)

        initView()
    }

    @SuppressLint("InflateParams")
    private fun initView(){
        val user = BmobUser.getCurrentUser(User::class.java)
        val post = intent.getSerializableExtra("post") as Post

        val query = BmobQuery<User>()
        query.addWhereRelatedTo("likes", BmobPointer(post))
        query.findObjects(object: FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1 == null){
                    if(p0!!.any { it.objectId == user.objectId }){
                        dynamic_like.text = getString(R.string.liked)
                    } else {
                        dynamic_like.text = getString(R.string.like)
                    }
                } else {
                    Toasty.error(this@DynamicActivity, p1.message!!).show()
                }
            }
        })

        dynamic_toolbar.setNavigationOnClickListener {
            finish()
        }

        dynamic_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        dynamic_refresh.setOnRefreshListener {
            refresh(post, user)
        }

        dynamic_comment.setOnClickListener {
            val editView = LayoutInflater.from(this).inflate(R.layout.add_comment, null)
            val dialog = AlertDialog.Builder(this)
                    .setView(editView)
                    .show()
            dialog.window.setGravity(Gravity.BOTTOM)
            dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            editView.findViewById<TextView>(R.id.add_comment_cancel).setOnClickListener {
                dialog.dismiss()
            }

            editView.findViewById<TextView>(R.id.add_comment_publish).setOnClickListener {
                val content = editView.findViewById<EditText>(R.id.add_comment_edit).text.toString()
                if(content.isEmpty()){
                    Toasty.warning(this, getString(R.string.comment_cannot_be_null)).show()
                } else {
                    val comment = Comment()
                    comment.author = user
                    comment.content = content
                    comment.post = post
                    comment.save(object: SaveListener<String>(){
                        override fun done(p0: String?, p1: BmobException?) {
                            if(p1 == null){
                                post.commentsNum++
                                post.update(object: UpdateListener(){
                                    override fun done(p0: BmobException?) {
                                        if(p0 == null){
                                            try {
                                                adapter.insert(comment, 0)
                                                dynamic_recycler.scrollToPosition(0)
                                                Toasty.success(this@DynamicActivity, getString(R.string.success_commented)).show()
                                            } catch (e: Exception){
                                                Toasty.error(this@DynamicActivity, e.message!!).show()
                                            }
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
                dialog.dismiss()
            }
        }

        dynamic_like.setOnClickListener {
            if(!isOperation){
                isOperation = true

                if(user != null){
                    val likeQuery = BmobQuery<User>()
                    likeQuery.addWhereRelatedTo("likes", BmobPointer(post))
                    likeQuery.findObjects(object: FindListener<User>(){
                        override fun done(p0: MutableList<User>?, p1: BmobException?) {
                            if(p1 == null){
                                val likes = BmobRelation()
                                val isLike = p0!!.any { it.objectId == user.objectId }
                                if(isLike){
                                    likes.remove(user)
                                    post.likesNum--
                                } else {
                                    likes.add(user)
                                    post.likesNum++
                                }
                                post.likes = likes
                                post.update(object: UpdateListener(){
                                    override fun done(p0: BmobException?) {
                                        if (p0 == null){
                                            if(isLike){
                                                dynamic_like.text = getString(R.string.like) + post.likesNum
                                            } else {
                                                dynamic_like.text = getString(R.string.liked) + post.likesNum
                                                Toasty.success(this@DynamicActivity, getString(R.string.success_liked)).show()
                                            }
                                        } else {
                                            Toasty.error(this@DynamicActivity, p0.message!!).show()
                                        }
                                    }
                                })
                            } else {
                                Toasty.error(this@DynamicActivity, p1.message!!).show()
                            }
                            isOperation = false
                        }
                    })
                } else {
                    startActivity(Intent(this@DynamicActivity, LoginActivity::class.java))
                }
            } else {
                Toasty.warning(this@DynamicActivity, getString(R.string.over_operation_warning)).show()
            }
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
                        dynamic_recycler.visibility = View.GONE
                    }
                    adapter = CommentAdapter(this@DynamicActivity, comments, user)
                    adapter.setOnBaseClickListener(object : CommentAdapter.OnCommentClickListener {
                        override fun onUserClicked(view: View, position: Int) {
                            val intent = Intent(this@DynamicActivity, DetailsActivity::class.java)
                            intent.putExtra("user", comments[position].author)
                            startActivity(intent)
                        }

                        override fun onLikeClicked(view: View, position: Int) {
                            if(!isOperation) {
                                isOperation = true

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
                                                val isLiked = p0!!.any { it.objectId == user.objectId }
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
                                isOperation = false
                            } else {
                                Toasty.warning(this@DynamicActivity, getString(R.string.over_operation_warning)).show()
                            }
                        }
                    })
                    dynamic_warning.visibility = View.GONE
                    dynamic_recycler.visibility = View.VISIBLE
                    dynamic_recycler.adapter = adapter
                    dynamic_recycler.layoutManager = LinearLayoutManager(this@DynamicActivity)
                } else {
                    Toasty.error(this@DynamicActivity, p1.message!!) .show()
                }
            }
        })
        dynamic_refresh.isRefreshing = false
    }
}