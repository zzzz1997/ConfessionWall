package com.zzapp.confessionwall.ui.application

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import cn.bmob.newim.BmobIM
import com.zzapp.confessionwall.service.MyMessageHandler

/**
 * Project ConfessionWall
 * Date 2018-02-02
 *
 * 我的应用，初始化
 *
 * @author zzzz
 */
class MyApplication : Application() {

    companion object {
        //应用实例
        private lateinit var INSTANCE: MyApplication

        /**
         * 获取app实例
         *
         * @return app实例
         */
        fun getInstance(): MyApplication {
            return INSTANCE
        }
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        if(applicationInfo.packageName == getMyProcessName()){
            BmobIM.init(this)
            BmobIM.registerDefaultMessageHandler(MyMessageHandler(this))
        }
    }

    /**
     * 获取当前进程名称
     *
     * @return 当前进程名称
     */
    private fun getMyProcessName() : String?{
        val pid = android.os.Process.myPid()
        val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.runningAppProcesses
                .firstOrNull { it.pid == pid }
                ?.processName
    }
}