package com.zzapp.confessionwall.ui

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.utils.User
import com.zzapp.confessionwall.view.BaseFragment
import kotlinx.android.synthetic.main.message_frag.*

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * @author zzzz
 */
class MessageFragment : BaseFragment() {

    override fun setContentView(): Int {
        return R.layout.message_frag
    }

    override fun initOnce() {
        setHasOptionsMenu(true)
        (activity!! as AppCompatActivity).setSupportActionBar(message_toolbar)
        (activity!! as AppCompatActivity).supportActionBar!!.title = null
        Log.e("message", "init")
    }

    override fun refresh(user: User?) {}
}