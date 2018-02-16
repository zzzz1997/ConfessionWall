package com.zzapp.confessionwall.ui.fragment

import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.event.MessageEvent
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.adapter.MessageAdapter
import com.zzapp.confessionwall.ui.TalkActivity
import com.zzapp.confessionwall.utils.MyCode
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * 消息界面的fragment
 *
 * @author zzzz
 */
class MessageFragment : BaseFragment(){

    private lateinit var toolbar: Toolbar
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView

    private val dm = DisplayMetrics()

    private var lists: List<BmobIMConversation> = emptyList()
    private lateinit var adapter: MessageAdapter

    override fun setContentView(): Int {
        return R.layout.message_frag
    }

    override fun initView() {
        toolbar = findViewById(R.id.message_toolbar) as Toolbar
        refresh = findViewById(R.id.message_refresh) as SwipeRefreshLayout
        recycler = findViewById(R.id.message_recycler) as RecyclerView

        activity!!.windowManager.defaultDisplay.getMetrics(dm)

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

    override fun stopLoad() {}

    override fun refresh() {
        lists = BmobIM.getInstance().loadAllConversation()
        adapter = MessageAdapter(context!!, lists as MutableList<BmobIMConversation>, dm.widthPixels)
        adapter.setOnBaseClickListener(object: MessageAdapter.OnMessageClickListener{
            override fun onClicked(position: Int) {
                val intent = Intent(context!!, TalkActivity::class.java)
                intent.putExtra("conversation", lists[position])
                activity!!.startActivity(intent)
            }

            override fun onLongClicked(position: Int) {
                Toasty.info(context!!, "长按").show()
            }

            override fun onTopClicked(position: Int) {
                Toasty.info(context!!, "置顶").show()
            }

            override fun onDeleteClicked(position: Int) {
                Toasty.info(context!!, "删除").show()
            }
        })
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        refresh.isRefreshing = false
    }

    override fun push(code: Int, data: Intent?) {
        when(code){
            MyCode.PUSH_DEFAULT -> {
                refresh()
            }
            else -> return
        }
    }
}