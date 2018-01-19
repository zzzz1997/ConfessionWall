package com.zzapp.confessionwall.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.bmob.push.PushConstants
import com.zzapp.confessionwall.MainActivity
import com.zzapp.confessionwall.R
import org.json.JSONObject

/**
 * Project ConfessionWall
 * Date 2018-01-19
 *
 * @author zzzz
 */
class MyPushMessageReceiver : BroadcastReceiver() {

    private val MSG = "msg"
    private val ALERT = "alert"

    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1!!.action == PushConstants.ACTION_MESSAGE){
            val json = JSONObject(p1.getStringExtra(MSG))

            val preferences = p0!!.getSharedPreferences(p0.getString(R.string.data_preference), Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString(p0.getString(R.string.my_msg), json.getString(ALERT))
            editor.putBoolean(p0.getString(R.string.is_closed), false)
            editor.apply()

            MainActivity.fragments[0].push()
        }
    }
}