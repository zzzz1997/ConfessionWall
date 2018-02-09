package com.zzapp.confessionwall.ui.fragment

import android.content.Intent
import android.support.v7.widget.Toolbar
import android.widget.TextView
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import cn.bmob.newim.bean.BmobIMMessage
import cn.bmob.newim.bean.BmobIMTextMessage
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.newim.core.BmobIMClient
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.listener.MessageListHandler
import cn.bmob.newim.listener.MessageSendListener
import cn.bmob.v3.exception.BmobException
import com.zzapp.confessionwall.R
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
class MessageFragment : BaseFragment(), MessageListHandler {

    private lateinit var toolbar: Toolbar
    private lateinit var test: TextView

    override fun setContentView(): Int {
        return R.layout.message_frag
    }

    override fun initView() {
        toolbar = findViewById(R.id.message_toolbar) as Toolbar
        test = findViewById(R.id.message_test) as TextView

        test.setOnClickListener {
            val msg = BmobIMTextMessage()
            msg.content = "消息1"
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
    }

    override fun loadView() {

    }

    override fun stopLoad() {

    }

    override fun refresh() {}

    override fun onMessageReceive(p0: MutableList<MessageEvent>?) {
        test.text = "聊天页面接收到消息：${p0!!.size}"
    }

    override fun push(code: Int, data: Intent?) {

    }
}