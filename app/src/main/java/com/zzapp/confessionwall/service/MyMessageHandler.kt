package com.zzapp.confessionwall.service

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.bmob.newim.bean.BmobIMMessage
import cn.bmob.newim.bean.BmobIMMessageType
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.event.OfflineMessageEvent
import cn.bmob.newim.listener.BmobIMMessageHandler
import cn.bmob.newim.notification.BmobNotificationManager
import cn.bmob.v3.exception.BmobException
import com.zzapp.confessionwall.model.UserModel
import com.zzapp.confessionwall.model.listener.UpdateCacheListener
import com.zzapp.confessionwall.ui.MainActivity
import es.dmoral.toasty.Toasty

/**
 * Project ConfessionWall
 * Date 2018-02-02
 *
 * 即时通讯接收器
 *
 * @author zzzz
 */
class MyMessageHandler(private val context: Context) : BmobIMMessageHandler(){

    override fun onMessageReceive(p0: MessageEvent?) {
        //在线
        Log.e("zzMessageHandler", "在线")
        if(p0 != null) {
            executeMessage(p0)
            Toasty.info(context, p0.message.content).toString()
        }
    }

    override fun onOfflineReceive(p0: OfflineMessageEvent?) {
        //离线
        Log.e("zzMessageHandler", "离线")
        if(p0 != null){
            Toasty.info(context, "" + p0.totalNumber).toString()
        }
    }

    private fun executeMessage(event: MessageEvent){
        UserModel.getInstance().updateUserInfo(event, object: UpdateCacheListener(){
            override fun done(e: BmobException?) {
                val msg = event.message
                if(BmobIMMessageType.getMessageTypeValue(msg.msgType) == 0){
                    //自定义消息
                } else {
                    processSDKMessage(msg, event)
                }
            }
        })
    }

    private fun processSDKMessage(msg: BmobIMMessage, event: MessageEvent){
        if(BmobNotificationManager.getInstance(context).isShowNotification){
            val pendingIntent = Intent(context, MainActivity::class.java)
            pendingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            BmobNotificationManager.getInstance(context).showNotification(event, pendingIntent)
        } else {

        }
    }
}