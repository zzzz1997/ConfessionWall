package com.zzapp.confessionwall.ui

import android.support.v7.widget.Toolbar
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.utils.User
import com.zzapp.confessionwall.view.BaseFragment

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * @author zzzz
 */
class MessageFragment : BaseFragment() {

    private lateinit var toolbar: Toolbar

    override fun setContentView(): Int {
        return R.layout.message_frag
    }

    override fun initView() {
        toolbar = findViewById(R.id.message_toolbar) as Toolbar
    }

    override fun loadView() {

    }

    override fun stopLoad() {

    }

    override fun refresh(user: User?) {}

    override fun push() {

    }
}