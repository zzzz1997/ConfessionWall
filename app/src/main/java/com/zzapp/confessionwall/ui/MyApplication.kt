package com.zzapp.confessionwall.ui

import android.app.Application
import cn.bmob.newim.BmobIM
import com.zzapp.confessionwall.model.MyMessageHandler
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Project ConfessionWall
 * Date 2018-02-02
 *
 * 我的应用，初始化
 *
 * @author zzzz
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if(applicationInfo.packageName.equals(getMyProcessName())){
            BmobIM.init(this)
            BmobIM.registerDefaultMessageHandler(MyMessageHandler())
        }
    }

    /**
     * 获取当前进程名称
     *
     * @return 当前进程名称
     */
    private fun getMyProcessName() : String?{
        return try {
            val file = File("/proc/" + android.os.Process.myPid() + "/cmdline")
            val bufferedReader = BufferedReader(FileReader(file))
            val processName = bufferedReader.readLine().trim()
            bufferedReader.close()
            processName
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}