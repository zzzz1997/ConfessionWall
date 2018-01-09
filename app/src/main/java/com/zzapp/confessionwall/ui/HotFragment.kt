package com.zzapp.confessionwall.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobRelation
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Post
import com.zzapp.confessionwall.utils.PostAdapter
import com.zzapp.confessionwall.utils.User
import kotlinx.android.synthetic.main.hot_frag.*

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class HotFragment : Fragment() {

    var isOperation = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.hot_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val user = BmobUser.getCurrentUser(User::class.java)

        hot_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        hot_refresh.setOnRefreshListener {
            refresh(user)
            Handler().postDelayed({
                hot_refresh.isRefreshing = false
            }, 2000)
        }

        hot_refresh.isRefreshing = true
        Handler().postDelayed({
            hot_refresh.isRefreshing = false
        }, 2000)
        refresh(user)

        hot_recycler.layoutManager = LinearLayoutManager(context)
    }

    private fun refresh(user: User?){
        val query = BmobQuery<Post>()
        query.order("-updatedAt")
        query.include("author")
        query.findObjects(object: FindListener<Post>() {
            override fun done(p0: MutableList<Post>?, p1: BmobException?) {
                if(p1 == null){
                    val adapter = PostAdapter(context!!, p0!!, user)
                    adapter.setOnPostClickListener(object: PostAdapter.MyOnPostClickListener{
                        override fun onUserClicked(view: View, position: Int) {
                            toast("点击用户" + p0[position].author!!.username)
                        }

                        override fun onContentClicked(view: View, position: Int) {
                            toast("点击内容")
                        }

                        override fun onCommentClicked(view: View, position: Int) {
                            toast("点击评论")
                        }

                        override fun onLikesClicked(view: View, position: Int) {
                            if(!isOperation){
                                isOperation = true

                                val button = view.findViewById<TextView>(R.id.post_like)
                                if(user != null){
                                    val relation = BmobRelation()
                                    val isLike = getString(R.string.like) == button.text.substring(0, 2)
                                    if (isLike){
                                        relation.add(user)
                                        p0[position].likesNum += 1
                                    } else {
                                        relation.remove(user)
                                        p0[position].likesNum -= 1
                                    }
                                    p0[position].likes = relation
                                    p0[position].update(object: UpdateListener(){
                                        override fun done(e: BmobException?) {
                                            if(e == null){
                                                if(isLike){
                                                    toast(getString(R.string.like_success))
                                                    button.text = getString(R.string.liked) + p0[position].likesNum
                                                } else {
                                                    button.text = getString(R.string.like) + p0[position].likesNum
                                                }
                                            } else {
                                                toast(e.message!!)
                                            }
                                            isOperation = false
                                        }
                                    })
                                } else {
                                    startActivity(Intent(context, LoginActivity::class.java))
                                }
                            } else {
                                toast(getString(R.string.over_operation_warning))
                            }
                        }
                    })
                    hot_recycler.adapter = adapter
                } else {
                    toast("失败")
                }
            }
        })
    }

    private fun toast(string: String){
        Toast.makeText(activity!!.applicationContext, string, Toast.LENGTH_SHORT).show()
    }
}