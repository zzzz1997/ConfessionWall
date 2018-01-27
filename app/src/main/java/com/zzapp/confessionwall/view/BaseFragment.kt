package com.zzapp.confessionwall.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bmob.v3.BmobUser
import com.zzapp.confessionwall.utils.User

/**
 * Project ConfessionWall
 * Date 2018-01-17
 *
 * @author zzzz
 */
abstract class BaseFragment : Fragment() {

    private var v: View? = null

    var user: User? = null

    private var isInit = false
    private var isLoad = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(setContentView(), container, false)
        user = BmobUser.getCurrentUser(User::class.java)
        initView()
        isInit = true
        toLoad()
        return v
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        toLoad()
    }

    private fun toLoad(){
        if(isInit){
            if(userVisibleHint){
                loadView()
                isLoad = true
            } else {
                if(isLoad){
                    stopLoad()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isInit = false
        isLoad = false
    }

    fun findViewById(id: Int) : View{
        return v!!.findViewById(id)
    }

    abstract fun setContentView() : Int
    abstract fun initView()
    abstract fun loadView()
    abstract fun stopLoad()
    abstract fun refresh()
    abstract fun push()
}