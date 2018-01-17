package com.zzapp.confessionwall.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.datatype.BmobRelation
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Comment
import com.zzapp.confessionwall.data.Post
import com.zzapp.confessionwall.ui.DynamicActivity
import com.zzapp.confessionwall.ui.LoginActivity
import es.dmoral.toasty.Toasty

/**
 * Project ConfessionWall
 * Date 2018-01-10
 *
 * @author zzzz
 */
class OnPostClickListener(private val context: Context, private val user: User?, private val posts: MutableList<Post>,
                          private val adapter: PostAdapter) : PostAdapter.MyOnPostClickListener{

    var isOperation = false

    override fun onUserClicked(view: View, position: Int) {
        Toasty.info(context, "点击用户" + posts[position].author!!.username).show()
    }

    override fun onFollowClicked(view: View, position: Int) {
        if(!isOperation) {
            isOperation = true

            val follow = view.findViewById<TextView>(R.id.post_follow)
            if (follow.text == context.getString(R.string.oneself)) {
                Toasty.info(context, context.getString(R.string.is_you)).show()
            } else {
                if (user == null) {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                } else {
                    val query = BmobQuery<User>()
                    query.addWhereRelatedTo("follow", BmobPointer(user))
                    query.findObjects(object : FindListener<User>() {
                        override fun done(p0: MutableList<User>?, p1: BmobException?) {
                            if (p1 == null) {
                                val isFollow = p0!!.any { it.objectId == posts[position].author!!.objectId }
                                val relation = BmobRelation()
                                if (isFollow) {
                                    relation.remove(posts[position].author)
                                    user.followNum--
                                } else {
                                    relation.add(posts[position].author)
                                    user.followNum++
                                }
                                user.follow = relation
                                user.update(object : UpdateListener() {
                                    override fun done(p0: BmobException?) {
                                        if (p0 == null) {
                                            if (isFollow) {
                                                follow.text = context.getString(R.string.add_follow)
                                                Toasty.info(context, context.getString(R.string.cancel_follow)).show()
                                            } else {
                                                follow.text = context.getString(R.string.followed)
                                                Toasty.success(context, context.getString(R.string.success_followed)).show()
                                            }
                                        } else {
                                            Toasty.error(context, p0.message!!).show()
                                        }
                                    }
                                })
                            } else {
                                Toasty.error(context, p1.message!!).show()
                            }
                            isOperation = false
                        }
                    })
                }
            }
        } else {
            Toasty.warning(context, context.getString(R.string.over_operation_warning)).show()
        }
    }

    override fun onMenuClicked(view: View, position: Int) {
        val follow = (view.parent as View).findViewById<TextView>(R.id.post_follow)
        val popup = PopupMenu(context, view.findViewById(R.id.post_menu))
        val menu = popup.menu
        menu.add(context.getString(R.string.collection))
        if(follow.text == context.getString(R.string.oneself)){
            menu.add(context.getString(R.string.delete))
        }
        popup.setOnMenuItemClickListener {
            when(it.title){
                context.getString(R.string.collection) -> {
                    if(user == null){
                        Toasty.warning(context, context.getString(R.string.please_login)).show()
                    } else {
                        val collections = BmobRelation()
                        collections.add(posts[position])
                        user.collections = collections
                        user.update(object: UpdateListener(){
                            override fun done(p0: BmobException?) {
                                if(p0 == null){
                                    Toasty.success(context, context.getString(R.string.success_collection)).show()
                                } else {
                                    Toasty.error(context, p0.message!!).show()
                                }
                            }
                        })
                    }
                    true
                }
                else -> {
                    AlertDialog.Builder(context)
                            .setTitle(R.string.delete_dynamic)
                            .setMessage(R.string.confirm_delete)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                posts[position].delete(object: UpdateListener(){
                                    override fun done(p0: BmobException?) {
                                        if(p0 == null){
                                            adapter.delete(position)
                                            Toasty.success(context, context.getString(R.string.success_delete)).show()
                                        } else {
                                            Toasty.error(context, p0.message!!).show()
                                        }
                                    }
                                })
                            }
                            .setNegativeButton(R.string.cancel) {_, _ -> }
                            .show()
                    true
                }
            }
        }
        popup.show()
    }

    override fun onContentClicked(view: View, position: Int) {
        val intent = Intent(context, DynamicActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("post", posts[position])
        intent.putExtras(bundle)
        context.startActivity(intent)
    }

    override fun onCommentClicked(view: View, position: Int) {
        if(user != null){
            val text = view.findViewById<TextView>(R.id.post_comment)
            if(text.text == context.getString(R.string.comment) + "0"){
                val editView = LayoutInflater.from(context).inflate(R.layout.add_comment, null)
                val dialog = AlertDialog.Builder(context)
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
                        Toasty.warning(context, context.getString(R.string.comment_cannot_be_null)).show()
                    } else {
                        val comment = Comment()
                        comment.author = user
                        comment.content = content
                        comment.post = posts[position]
                        comment.save(object: SaveListener<String>(){
                            override fun done(p0: String?, p1: BmobException?) {
                                if(p1 == null){
                                    posts[position].commentsNum++
                                    posts[position].update(object: UpdateListener(){
                                        override fun done(p0: BmobException?) {
                                            if(p0 == null){
                                                text.text = context.getString(R.string.comment) + posts[position].commentsNum
                                                Toasty.success(context, context.getString(R.string.success_commented)).show()
                                            } else {
                                                Toasty.error(context, p0.message!!).show()
                                            }
                                        }
                                    })
                                } else {
                                    Toasty.error(context, p1.message!!).show()
                                }
                            }
                        })
                    }
                    dialog.dismiss()
                }
            } else {
                val intent = Intent(context, DynamicActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("post", posts[position])
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
        } else {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    override fun onLikesClicked(view: View, position: Int) {
        if(!isOperation){
            isOperation = true

            val like = view.findViewById<TextView>(R.id.post_like)
            if(user != null){
                val query = BmobQuery<User>()
                query.addWhereRelatedTo("likes", BmobPointer(posts[position]))
                query.findObjects(object: FindListener<User>(){
                    override fun done(p0: MutableList<User>?, p1: BmobException?) {
                        if(p1 == null){
                            val likes = BmobRelation()
                            val isLike = p0!!.any { it.objectId == user.objectId }
                            if(isLike){
                                likes.remove(user)
                                posts[position].likesNum--
                            } else {
                                likes.add(user)
                                posts[position].likesNum++
                            }
                            posts[position].likes = likes
                            posts[position].update(object: UpdateListener(){
                                override fun done(p0: BmobException?) {
                                    if (p0 == null){
                                        if(isLike){
                                            like.text = context.getString(R.string.like) + posts[position].likesNum
                                        } else {
                                            like.text = context.getString(R.string.liked) + posts[position].likesNum
                                            Toasty.success(context, context.getString(R.string.success_liked)).show()
                                        }
                                    } else {
                                        Toasty.error(context, p0.message!!).show()
                                    }
                                }
                            })
                        } else {
                            Toasty.error(context, p1.message!!).show()
                        }
                        isOperation = false
                    }
                })
            } else {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        } else {
            Toasty.warning(context, context.getString(R.string.over_operation_warning)).show()
        }
    }
}