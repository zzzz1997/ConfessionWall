package com.zzapp.confessionwall.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zzapp.confessionwall.R

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * @author zzzz
 */
class MessageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.message_frag, container, false)
    }
}