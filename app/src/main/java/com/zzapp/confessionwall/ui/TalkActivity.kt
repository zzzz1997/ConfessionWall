package com.zzapp.confessionwall.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.bean.BmobIMMessage
import cn.bmob.newim.bean.BmobIMTextMessage
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.newim.core.BmobIMClient
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.listener.MessageSendListener
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.adapter.TalkAapter
import com.zzapp.confessionwall.entity.User
import com.zzapp.confessionwall.model.listener.SoftKeyBoardListener
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.talk.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * Project ConfessionWall
 * Date 2018-02-15
 *
 * 聊天活动界面
 *
 * @author zzzz
 */
class TalkActivity : AppCompatActivity() {

    private lateinit var conversation: BmobIMConversation

    private lateinit var user: User

    private lateinit var adapter: TalkAapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.talk)
        initView()
    }

    /**
     * 初始化界面
     */
    private fun initView(){
        EventBus.getDefault().register(this)

        conversation = intent.getSerializableExtra("conversation") as BmobIMConversation

        user = BmobUser.getCurrentUser(User::class.java)

        talk_toolbar.setNavigationOnClickListener {
            finish()
        }

        talk_name.text = conversation.conversationTitle

        talk_send.setOnClickListener {
            val content = talk_edit.text.toString()
            if(!content.isEmpty()) {
                val message = BmobIMTextMessage()
                message.content = content
                val map = HashMap<String, Any>(1)
                map["level"] = 1

                message.setExtraMap(map)
                val conversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(),
                        BmobIM.getInstance().startPrivateConversation(BmobIMUserInfo(conversation.conversationId, conversation.conversationTitle,conversation.conversationIcon), null))
                conversationManager.sendMessage(message, object : MessageSendListener() {
                    override fun done(p0: BmobIMMessage?, p1: BmobException?) {
                        if (p1 == null) {
                            addNewMessage(message)
                            talk_edit.text = null
                            Toasty.success(this@TalkActivity, "发送成功").show()
                        } else {
                            Toasty.error(this@TalkActivity, p1.message!!).show()
                        }
                    }
                })
            }
        }

        SoftKeyBoardListener(this).setOnSoftKeyBoardChangeListener(object: SoftKeyBoardListener.OnSoftKeyBoardChangeListener{
            override fun keyBoardShow(height: Int) {
                talk_recycler.scrollToPosition(adapter.itemCount - 1)
            }

            override fun keyBoardHide(height: Int) {

            }
        })

        initRecycler()
    }

    /**
     * EventBus的接收器
     *
     * @param event 接收到的消息事件
     */
    @Subscribe
    fun onEvent(event: MessageEvent){
        addNewMessage(event.message)
    }

    /**
     * 初始化RecyclerView
     */
    private fun initRecycler() {
        val messages = conversation.messages
        messages.reverse()
        adapter = TalkAapter(this, messages, user, conversation.conversationIcon)
        adapter.setOnBaseClickListener(object : TalkAapter.OnTalkClickListener {
            override fun onUserIconClicked() {
                Toasty.info(this@TalkActivity, conversation.conversationId).show()
            }

            override fun onMeIconClicked() {
                Toasty.info(this@TalkActivity, user.objectId).show()
            }

            override fun onLongClicked(view: View, position: Int) {
                Toasty.info(this@TalkActivity, messages[position].content).show()
            }
        })
        talk_recycler.adapter = adapter
        talk_recycler.layoutManager = LinearLayoutManager(this)
        talk_recycler.scrollToPosition(adapter.itemCount - 1)
    }

    /**
     * 添加新的消息到列表
     *
     * @param message 消息
     */
    private fun addNewMessage(message: BmobIMMessage){
        adapter.insert(message, adapter.itemCount)
        talk_recycler.scrollToPosition(adapter.itemCount - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}