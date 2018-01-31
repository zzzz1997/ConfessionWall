package com.zzapp.confessionwall.presenter

import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.LogInListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.zzapp.confessionwall.data.User
import com.zzapp.confessionwall.view.IUserView

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * presenter的实体类
 *
 * @author zzzz
 */
class UserPresenter(private val activity: IUserView) : IUserPresenter {

    override fun login(name: String, password: String) {
        activity.newDialog()
        BmobUser.loginByAccount<User>(name, password, object : LogInListener<User>() {
                    override fun done(user: User?, e: BmobException?) {
                        activity.dismissDialog()
                        if (e == null) {
                            activity.onSuccess(user)
                        } else {
                            activity.onFailure(e.message!!)
                        }
                    }
                })
    }

    override fun register(name: String, password: String, email: String, icon: String) {
        activity.newDialog()
        val bmobUser = User()
        bmobUser.username = name
        bmobUser.setPassword(password)
        bmobUser.email = email
        bmobUser.icon = icon
        bmobUser.signUp(object : SaveListener<User>() {
            override fun done(user: User?, e: BmobException?) {
                if (e == null) {
                    activity.onSuccess(user)
                } else {
                    activity.onFailure(e.message!!)
                }
            }
        })
    }

    override fun reset(email: String) {
        activity.newDialog()
        BmobUser.resetPasswordByEmail(email, object : UpdateListener(){
            override fun done(e: BmobException?) {
                if(e == null){
                    activity.onSuccess(null)
                } else {
                    activity.onFailure(e.message!!)
                }
            }
        })
    }
}