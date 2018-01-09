package com.zzapp.confessionwall.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zzapp.confessionwall.R

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class FollowFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.follow_frag, container, false)
    }
}