package com.zzapp.confessionwall.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.datatype.BmobRelation
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.zzapp.confessionwall.R
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
class OnPostClickListener(private val context: Context, private val user: User?, private val posts: MutableList<Post>) : PostAdapter.MyOnPostClickListener{

    var isOperation = false

    override fun onUserClicked(view: View, position: Int) {
        Toasty.info(context, "点击用户" + posts[position].author!!.username).show()
    }

    override fun onFollowClicked(view: View, position: Int) {
        val follow = view.findViewById<TextView>(R.id.post_follow)
        if(follow.text == context.getString(R.string.oneself)){
            Toasty.info(context, context.getString(R.string.is_you)).show()
        } else {
            if(user == null){
                context.startActivity(Intent(context, LoginActivity::class.java))
            } else {
                val query = BmobQuery<User>()
                query.addWhereRelatedTo("follow", BmobPointer(user))
                query.findObjects(object: FindListener<User>() {
                    override fun done(p0: MutableList<User>?, p1: BmobException?) {
                        if (p1 == null) {
                            val isFollow = p0!!.any { it.objectId == posts[position].author!!.objectId }
                            val relation = BmobRelation()
                            if(isFollow){
                                relation.remove(posts[position].author)
                                user.followNum--
                            } else {
                                relation.add(posts[position].author)
                                user.followNum++
                            }
                            user.follow = relation
                            user.update(object: UpdateListener(){
                                override fun done(p0: BmobException?) {
                                    if(p0 == null){
                                        if(isFollow){
                                            follow.text = context.getString(R.string.add_follow)
                                            Toasty.info(context, context.getString(R.string.cancel_follow)).show()
                                        } else {
                                            follow.text = context.getString(R.string.followed)
                                            Toasty.success(context, context.getString(R.string.success_followed)).show()
                                        }
                                    } else {
                                        Toasty.error(context, p0.message as CharSequence).show()
                                    }
                                }
                            })
                        } else {
                            Toasty.error(context, p1.message as CharSequence).show()
                        }
                    }
                })
            }
        }
    }

    override fun onMenuClicked(view: View, position: Int) {
        Toasty.info(context, "点击菜单").show()
    }

    override fun onContentClicked(view: View, position: Int) {
        val intent = Intent(context, DynamicActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("post", posts[position])
        intent.putExtras(bundle)
        context.startActivity(intent)
    }

    override fun onCommentClicked(view: View, position: Int) {
        Toasty.info(context, "点击评论").show()
    }

    override fun onLikesClicked(view: View, position: Int) {
        if(!isOperation){
            isOperation = true

            val button = view.findViewById<TextView>(R.id.post_like)
            if(user != null){
                val relation = BmobRelation()
                val isLike = context.getString(R.string.like) == button.text.substring(0, 2)
                if (isLike){
                    relation.add(user)
                    posts[position].likesNum++
                } else {
                    relation.remove(user)
                    posts[position].likesNum--
                }
                posts[position].likes = relation
                posts[position].update(object: UpdateListener(){
                    override fun done(e: BmobException?) {
                        if(e == null){
                            if(isLike){
                                Toasty.success(context, context.getString(R.string.success_liked)).show()
                                button.text = context.getString(R.string.liked) + posts[position].likesNum
                            } else {
                                Toasty.info(context, context.getString(R.string.cancel_like)).show()
                                button.text = context.getString(R.string.like) + posts[position].likesNum
                            }
                        } else {
                            Toasty.error(context, e.message!!).show()
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