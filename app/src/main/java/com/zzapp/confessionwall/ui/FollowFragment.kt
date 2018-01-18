package com.zzapp.confessionwall.ui

import android.app.Activity
import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.TextView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobPointer
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
class FollowFragment : BaseFragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView
    private lateinit var warning: TextView

    private var user: User? = null
    private lateinit var adapter: PostAdapter

    private val ADD_POST = 0

    override fun setContentView(): Int {
        return R.layout.follow_frag
    }

    override fun initView() {
        toolbar = findViewById(R.id.follow_toolbar) as Toolbar
        refresh = findViewById(R.id.follow_refresh) as SwipeRefreshLayout
        recycler = findViewById(R.id.follow_recycler) as RecyclerView
        warning = findViewById(R.id.follow_warning) as TextView

        setHasOptionsMenu(true)
        (activity!! as AppCompatActivity).setSupportActionBar(toolbar)
        (activity!! as AppCompatActivity).supportActionBar!!.title = null
        Log.e("follow", "init")

        user = BmobUser.getCurrentUser(User::class.java)

        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.follow_menu_add -> {
                    if(user != null) {
                        startActivityForResult(Intent(context, AddPostActivity::class.java), ADD_POST)
                    } else {
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }

        refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        refresh.setOnRefreshListener {
            refresh(user)
        }

        recycler.layoutManager = LinearLayoutManager(context)
    }

    override fun loadView() {
        refresh.isRefreshing = true
        refresh(user)
    }

    override fun stopLoad() {

    }

    override fun refresh(user: User?) {
        if(user == null){
            warning.visibility = View.VISIBLE
            warning.text = getString(R.string.please_login)
            recycler.visibility = View.GONE
        } else {
            val query = BmobQuery<User>()
            query.addWhereRelatedTo("follow", BmobPointer(user))
            query.findObjects(object: FindListener<User>(){
                override fun done(p0: MutableList<User>?, p1: BmobException?) {
                    if(p1 == null){
                        var list: List<String> = emptyList()
                        list += user.objectId
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
                                        adapter.setOnPostClickListener(OnPostClickListener(context!!, user, p0, adapter))
                                        recycler.adapter = adapter
                                        recycler.layoutManager = LinearLayoutManager(context)
                                    }
                                } else {
                                    Toasty.error(context!!, p1.message!!).show()
                                }
                            }
                        })
                    } else {
                        Toasty.error(context!!, p1.message!!).show()
                    }
                }
            })
        }
        refresh.isRefreshing = false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.follow_menu, menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_POST -> {
                if(resultCode == Activity.RESULT_OK){
                    val post = data!!.getSerializableExtra("post") as Post
                    try {
                        adapter.insert(post, 0)
                        recycler.scrollToPosition(0)
                        Toasty.success(context!!, getString(R.string.success_published)).show()
                    } catch (e: Exception){
                        Toasty.error(context!!, e.message!!).show()
                    }
                }
            }
        }
    }
}