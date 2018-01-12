package com.zzapp.confessionwall.ui

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Post
import com.zzapp.confessionwall.utils.OnPostClickListener
import com.zzapp.confessionwall.utils.PostAdapter
import com.zzapp.confessionwall.utils.User
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.hot_frag.*

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class HotFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.hot_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.e("hot", "created")

        val user = BmobUser.getCurrentUser(User::class.java)

        hot_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        hot_refresh.setOnRefreshListener {
            refresh(user)
        }

        hot_refresh.isRefreshing = true
        refresh(user)
    }

    private fun refresh(user: User?){
        val query = BmobQuery<Post>()
        query.order("-updatedAt")
        query.include("author")
        query.findObjects(object: FindListener<Post>() {
            override fun done(p0: MutableList<Post>?, p1: BmobException?) {
                if(p1 == null){
                    val adapter = PostAdapter(context!!, p0!!, user)
                    adapter.setOnPostClickListener(OnPostClickListener(context!!, user, p0))
                    hot_recycler.adapter = adapter
                    hot_recycler.layoutManager = LinearLayoutManager(context)
                } else {
                    Toasty.error(context!!, p1.message!!).show()
                }
            }
        })
        hot_refresh.isRefreshing = false
    }
}