package com.zzapp.confessionwall.ui.fragment

import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.entity.Post
import com.zzapp.confessionwall.adapter.OnPostClickListener
import com.zzapp.confessionwall.adapter.PostAdapter
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * 热门界面的fragment
 *
 * @author zzzz
 */
class HotFragment : BaseFragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView

    override fun setContentView(): Int {
        return R.layout.hot_frag
    }

    override fun initView() {
        toolbar = findViewById(R.id.hot_toolbar) as Toolbar
        refresh = findViewById(R.id.hot_refresh) as SwipeRefreshLayout
        recycler = findViewById(R.id.hot_recycler) as RecyclerView

        refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        refresh.setOnRefreshListener {
            refresh()
        }
    }

    override fun loadView() {
        refresh.isRefreshing = true
        refresh()
    }

    override fun stopLoad() {

    }

    override fun refresh(){
        val query = BmobQuery<Post>()
        query.order("-updatedAt")
        query.include("author")
        query.findObjects(object: FindListener<Post>() {
            override fun done(p0: MutableList<Post>?, p1: BmobException?) {
                if(p1 == null){
                    val adapter = PostAdapter(context!!, p0!!, user)
                    adapter.setOnBaseClickListener(OnPostClickListener(context!!, user, p0, adapter))
                    recycler.adapter = adapter
                    recycler.layoutManager = LinearLayoutManager(context)
                    refresh.isRefreshing = false
                } else {
                    Toasty.error(context!!, p1.message!!).show()
                    refresh.isRefreshing = false
                }
            }
        })
    }

    override fun push(code: Int, data: Intent?) {

    }
}