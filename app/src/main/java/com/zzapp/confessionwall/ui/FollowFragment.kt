package com.zzapp.confessionwall.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
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
import kotlinx.android.synthetic.main.follow_frag.*

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class FollowFragment : BaseFragment() {

    private lateinit var adapter: PostAdapter

    private val ADD_POST = 0

    override fun setContentView(): Int {
        return R.layout.follow_frag
    }

    override fun initOnce() {
        setHasOptionsMenu(true)
        (activity!! as AppCompatActivity).setSupportActionBar(follow_toolbar)
        (activity!! as AppCompatActivity).supportActionBar!!.title = null
        Log.e("follow", "init")

        val user = BmobUser.getCurrentUser(User::class.java)

        follow_toolbar.setOnMenuItemClickListener {
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

        follow_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        follow_refresh.setOnRefreshListener {
            refresh(user)
        }

        follow_refresh.isRefreshing = true
        refresh(user)

        follow_recycler.layoutManager = LinearLayoutManager(context)
    }

    override fun refresh(user: User?) {
        if(user == null){
            follow_warning.visibility = View.VISIBLE
            follow_warning.text = getString(R.string.please_login)
            follow_recycler.visibility = View.GONE
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
                                        follow_warning.visibility = View.VISIBLE
                                        follow_warning.text = getString(R.string.no_post_warning)
                                        follow_recycler.visibility = View.GONE
                                    } else {
                                        follow_warning.visibility = View.GONE
                                        follow_recycler.visibility = View.VISIBLE
                                        adapter = PostAdapter(context!!, p0, user)
                                        adapter.setOnPostClickListener(OnPostClickListener(context!!, user, p0, adapter))
                                        follow_recycler.adapter = adapter
                                        follow_recycler.layoutManager = LinearLayoutManager(context)
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
        follow_refresh.isRefreshing = false
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
                        follow_recycler.scrollToPosition(0)
                        Toasty.success(context!!, getString(R.string.success_published)).show()
                    } catch (e: Exception){
                        Toasty.error(context!!, e.message!!).show()
                    }
                }
            }
        }
    }
}