package com.zzapp.confessionwall.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zzapp.confessionwall.utils.User

/**
 * Project ConfessionWall
 * Date 2018-01-17
 *
 * @author zzzz
 */
abstract class BaseFragment : Fragment() {

    private var isOnce = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(setContentView(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(isOnce){
            initOnce()
            isOnce = false
        }
    }

    abstract fun setContentView() : Int
    abstract fun initOnce()
    abstract fun refresh(user: User?)
}