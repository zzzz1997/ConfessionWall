package com.zzapp.confessionwall.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import com.zzapp.confessionwall.R
import kotlinx.android.synthetic.main.splash.*
import java.util.*

/**
 * Project ConfessionWall
 * Date 2018-01-02
 *
 * 欢迎界面的活动
 *
 * @author zzzz
 */
class SplashActivity : Activity(){

    private val COUNT_TIME = 3
    private val INTERVAL_TIME = 1000L

    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        initView()
    }

    private fun initView(){
        var time = COUNT_TIME
        timer.schedule(object: TimerTask(){
            override fun run() {
                handler.sendEmptyMessage(time)
                time--
            }
        }, 0, INTERVAL_TIME)

        count_down.setOnClickListener {
            timer.cancel()
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            this@SplashActivity.finish()
        }
    }

    private val handler = @SuppressLint("HandlerLeak")
    object: android.os.Handler(){
        override fun handleMessage(msg: Message?) {
            if(msg!!.what > 0){
                count_down.text = "" + msg.what + getString(R.string.skip)
            } else {
                timer.cancel()
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                this@SplashActivity.finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}