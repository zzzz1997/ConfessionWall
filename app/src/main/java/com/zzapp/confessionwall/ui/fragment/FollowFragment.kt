package com.zzapp.confessionwall.ui.fragment

import android.content.Context
import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.entity.Post
import com.zzapp.confessionwall.adapter.OnPostClickListener
import com.zzapp.confessionwall.adapter.PostAdapter
import com.zzapp.confessionwall.entity.User
import com.zzapp.confessionwall.ui.AddPostActivity
import com.zzapp.confessionwall.ui.LoginActivity
import com.zzapp.confessionwall.utils.MyCode
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * 关注界面的fragment
 *
 * @author zzzz
 */
class FollowFragment : BaseFragment() {

    private val preferences by lazy { context!!.getSharedPreferences(context!!.getString(R.string.data_preference), Context.MODE_PRIVATE) }

    private lateinit var toolbar: Toolbar
    private lateinit var warning: TextView
    private lateinit var layout: CardView
    private lateinit var broadcast: TextView
    private lateinit var close: ImageView
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView

    private lateinit var adapter: PostAdapter

    override fun setContentView(): Int {
        return R.layout.follow_frag
    }

    override fun initView() {
        toolbar = findViewById(R.id.follow_toolbar) as Toolbar
        warning = findViewById(R.id.follow_warning) as TextView
        layout = findViewById(R.id.my_broadcast) as CardView
        broadcast = findViewById(R.id.my_broadcast_text) as TextView
        close = findViewById(R.id.my_broadcast_close) as ImageView
        refresh = findViewById(R.id.follow_refresh) as SwipeRefreshLayout
        recycler = findViewById(R.id.follow_recycler) as RecyclerView

        toolbar.inflateMenu(R.menu.follow_menu)

        val msg = preferences.getString(context!!.getString(R.string.my_msg), null)
        val isClosed = preferences.getBoolean(context!!.getString(R.string.is_closed), false)

        if(msg != null && !isClosed){
            layout.visibility = View.VISIBLE
            broadcast.text = msg
        }

        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.follow_menu_add -> {
                    if(user != null) {
                        activity!!.startActivityForResult(Intent(context, AddPostActivity::class.java), MyCode.REQUEST_ADD_POST)
                    } else {
                        activity!!.startActivity(Intent(context, LoginActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }

        broadcast.setOnClickListener {
            Toasty.info(context!!, broadcast.text).show()
        }

        close.setOnClickListener {
            layout.visibility = View.GONE
            val editor = preferences.edit()
            editor.putBoolean(context!!.getString(R.string.is_closed), true)
            editor.apply()
        }

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

    override fun refresh() {
        if(user == null){
            warning.visibility = View.VISIBLE
            warning.text = getString(R.string.please_login)
            recycler.visibility = View.GONE
            refresh.isRefreshing = false
        } else {
            val query = BmobQuery<User>()
            query.addWhereRelatedTo("follow", BmobPointer(user))
            query.findObjects(object: FindListener<User>(){
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if(p1 == null){
                        var list: List<String> = emptyList()
                        list += user!!.objectId
                        if (p0 != null) {
                            for(i in p0){
                                list += i.objectId
                            }
                        }
                        val innerQuery = BmobQuery<User>()
                        innerQuery.addWhereContainedIn("objectId", list)
                        val postQuery = BmobQuery<Post>()
                        postQuery.addWhereMatchesQuery("author", "_User", innerQuery)
                        postQuery.include("author")
                        postQuery.order("-updatedAt")
                        postQuery.findObjects(object: FindListener<Post>(){
                            override fun done(p0: MutableList<Post>?, p1: BmobException?) {
                                if(p1 == null){
                                    if(p0!!.isEmpty()){
                                        warning.visibility = View.VISIBLE
                                        warning.text = getString(R.string.no_post_warning)
                                        recycler.visibility = View.GONE
                                    } else {
                                        warning.visibility = View.GONE
                                        recycler.visibility = View.VISIBLE
                                        adapter = PostAdapter(context!!, p0, user)
                                        adapter.setOnBaseClickListener(OnPostClickListener(context!!, user, p0, adapter))
                                        recycler.adapter = adapter
                                        recycler.layoutManager = LinearLayoutManager(context)
                                        refresh.isRefreshing = false
                                    }
                                } else {
                                    Toasty.error(context!!, p1.message!!).show()
                                    refresh.isRefreshing = false
                                }
                            }
                        })
                    } else {
                        Toasty.error(context!!, p1.message!!).show()
                        refresh.isRefreshing = false
                    }
                }
            })
        }
    }

    override fun push(code: Int, data: Intent?) {
        when(code){
            MyCode.REQUEST_ADD_POST -> {
                val post = data!!.getSerializableExtra("post") as Post
                try {
                    adapter.insert(post, 0)
                    recycler.scrollToPosition(0)
                    Toasty.success(context!!, getString(R.string.success_published)).show()
                } catch (e: Exception){
                    Toasty.error(context!!, e.message!!).show()
                }
            }
            MyCode.PUSH_MESSAGE -> {
                val msg = preferences.getString(context!!.getString(R.string.my_msg), null)
                val isClosed = preferences.getBoolean(context!!.getString(R.string.is_closed), false)

                if(msg != null && !isClosed){
                    layout.visibility = View.VISIBLE
                    broadcast.text = msg
                }
            }
            else -> return
        }
    }
}