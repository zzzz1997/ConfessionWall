package com.zzapp.confessionwall.ui.fragment

import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.TextView
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.bean.BmobIMMessage
import cn.bmob.newim.bean.BmobIMTextMessage
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.newim.core.BmobIMClient
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.listener.MessageSendListener
import cn.bmob.v3.exception.BmobException
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.adapter.MessageAdapter
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

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
    private lateinit var test: TextView
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var recycler: RecyclerView

    private var lists: List<BmobIMConversation> = emptyList()
    private lateinit var adapter: MessageAdapter

    override fun setContentView(): Int {
        return R.layout.message_frag
    }

    override fun initView() {
        EventBus.getDefault().register(this)

        toolbar = findViewById(R.id.message_toolbar) as Toolbar
        test = findViewById(R.id.message_test) as TextView
        refresh = findViewById(R.id.message_refresh) as SwipeRefreshLayout
        recycler = findViewById(R.id.message_recycler) as RecyclerView

        test.setOnClickListener {
            val msg = BmobIMTextMessage()
            msg.content = "消息" + (Math.random() * 100).toInt()
            val map = HashMap<String, Any>(1)
            map["level"] = 1

            msg.setExtraMap(map)
            val conversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(),
                    BmobIM.getInstance().startPrivateConversation(BmobIMUserInfo("8aa9353690",
                            "123456", "http://bmob-cdn-15551.b0.upaiyun.com/2017/12/18/02536b86cf7146669cf2a2f7b8edabe4.png"), null))
            conversationManager.sendMessage(msg, object: MessageSendListener(){
                override fun done(p0: BmobIMMessage?, p1: BmobException?) {
                    if(p1 == null){
                        Toasty.success(context!!, "发送成功").show()
                    } else {
                        Toasty.error(context!!, p1.message!!).show()
                    }
                }
            })
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
        lists = BmobIM.getInstance().loadAllConversation()
        adapter = MessageAdapter(context!!, lists as MutableList<BmobIMConversation>, user)
        adapter.setOnBaseClickListener(object: MessageAdapter.OnMessageClickListener{
            override fun onClicked(position: Int) {
                Toasty.info(context!!, "点击").show()
            }

            override fun onLongClicked(position: Int) {
                Toasty.info(context!!, "长按").show()
            }
        })
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(context)
        refresh.isRefreshing = false
    }

    @Subscribe fun onEvent(event: MessageEvent){
        for(i in 0 until lists.size){
            if(lists[i].conversationId == event.conversation.conversationId){
                adapter.fresh(i)
                return
            }
        }
        adapter.insert(event.conversation, 0)
    }

    override fun push(code: Int, data: Intent?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}