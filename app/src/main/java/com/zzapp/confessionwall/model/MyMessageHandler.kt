package com.zzapp.confessionwall.model

import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.event.OfflineMessageEvent
import cn.bmob.newim.listener.BmobIMMessageHandler

/**
 * Project ConfessionWall
 * Date 2018-02-02
 *
 * 即时通讯接收器
 *
 * @author zzzz
 */
class MyMessageHandler : BmobIMMessageHandler(){

    override fun onMessageReceive(p0: MessageEvent?) {
        //在线
    }

    override fun onOfflineReceive(p0: OfflineMessageEvent?) {
        //离线
    }
}