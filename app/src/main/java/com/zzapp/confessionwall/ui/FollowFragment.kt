package com.zzapp.confessionwall.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.follow_frag.*

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class FollowFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.follow_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.e("follow", "created")
        val user = BmobUser.getCurrentUser(User::class.java)
        follow_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        follow_refresh.setOnRefreshListener {
            refresh(user)
        }

        follow_refresh.isRefreshing = true
        refresh(user)

        follow_recycler.layoutManager = LinearLayoutManager(context)
    }

    private fun refresh(user: User?){
        if(user == null){
            Toasty.warning(context!!, getString(R.string.please_login)).show()
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
                                        val adapter = PostAdapter(context!!, p0, user)
                                        adapter.setOnPostClickListener(OnPostClickListener(context!!, user, p0))
                                        follow_recycler.adapter = adapter
                                        follow_recycler.layoutManager = LinearLayoutManager(context)
                                    }
                                } else {
                                    Toasty.error(context!!, p1.message as CharSequence).show()
                                }
                            }
                        })
                    } else {
                        Toasty.error(context!!, p1.message as CharSequence).show()
                    }
                }
            })
        }
        follow_refresh.isRefreshing = false
    }
}