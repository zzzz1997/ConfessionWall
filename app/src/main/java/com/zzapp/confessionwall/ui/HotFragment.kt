package com.zzapp.confessionwall.ui

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Post
import com.zzapp.confessionwall.utils.OnPostClickListener
import com.zzapp.confessionwall.utils.PostAdapter
import com.zzapp.confessionwall.utils.User
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class HotFragment : BaseFragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView

    private var user: User? = null

    override fun setContentView(): Int {
        return R.layout.hot_frag
    }

    override fun initView() {
        toolbar = findViewById(R.id.hot_toolbar) as Toolbar
        refresh = findViewById(R.id.hot_refresh) as SwipeRefreshLayout
        recycler = findViewById(R.id.hot_recycler) as RecyclerView

        setHasOptionsMenu(true)
        (activity!! as AppCompatActivity).setSupportActionBar(toolbar)
        (activity!! as AppCompatActivity).supportActionBar!!.title = null
        Log.e("hot", "init")

        user = BmobUser.getCurrentUser(User::class.java)

        refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        refresh.setOnRefreshListener {
            refresh(user)
        }
    }

    override fun loadView() {
        refresh.isRefreshing = true
        refresh(user)
    }

    override fun stopLoad() {

    }

    override fun refresh(user: User?){
        val query = BmobQuery<Post>()
        query.order("-updatedAt")
        query.include("author")
        query.findObjects(object: FindListener<Post>() {
            override fun done(p0: MutableList<Post>?, p1: BmobException?) {
                if(p1 == null){
                    val adapter = PostAdapter(context!!, p0!!, user)
                    adapter.setOnPostClickListener(OnPostClickListener(context!!, user, p0, adapter))
                    recycler.adapter = adapter
                    recycler.layoutManager = LinearLayoutManager(context)
                } else {
                    Toasty.error(context!!, p1.message!!).show()
                }
            }
        })
        refresh.isRefreshing = false
    }
}