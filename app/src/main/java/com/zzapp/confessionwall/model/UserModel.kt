package com.zzapp.confessionwall.model

import cn.bmob.newim.BmobIM
import cn.bmob.newim.event.MessageEvent
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.LogInListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.zzapp.confessionwall.entity.User
import com.zzapp.confessionwall.model.listener.QueryUserListener
import com.zzapp.confessionwall.model.listener.UpdateCacheListener
import com.zzapp.confessionwall.view.IUserView

/**
 * Project ConfessionWall
 * Date 2018-02-08
 *
 * @author zzzz
 */
class UserModel {

    private val CANNOT_FOUND_USER = "查无此人"

    companion object{
        private val INSTANCE = UserModel()

        fun getInstance(): UserModel{
            return INSTANCE
        }
    }

    /**
     * 登录
     *
     * @param name 用户名
     * @param password 密码
     */
    fun login(activity: IUserView, name: String, password: String){
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

    /**
     * 注册
     *
     * @param name 用户名
     * @param password 密码
     * @param email 邮箱
     */
    fun register(activity: IUserView, name: String, password: String, email: String, icon: String){
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

    /**
     * 重置密码
     *
     * @param email 注册的邮箱
     */
    fun reset(activity: IUserView, email: String){
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

    /**
     * 更新用户资料和会话资料
     *
     * @param event 信息事件
     * @param listener 资料更新监听器
     */
    fun updateUserInfo(event: MessageEvent, listener: UpdateCacheListener){
        val conversation = event.conversation
        val info = event.fromUserInfo
        val msg = event.message
        val username = info.name
        val avatar = info.avatar
        val title = conversation.conversationTitle
        val icon = conversation.conversationIcon
        if(username != title || avatar != icon){
            UserModel.getInstance().queryUserInfo(info.userId, object: QueryUserListener(){
                override fun done(user: User?, e: BmobException?) {
                    if(e == null){
                        val name = user!!.username
                        val avatarIcon = user.icon
                        conversation.conversationIcon = avatarIcon
                        conversation.conversationTitle = name
                        info.name = name
                        info.avatar = avatarIcon
                        BmobIM.getInstance().updateUserInfo(info)
                        if(!msg.isTransient){
                            BmobIM.getInstance().updateConversation(conversation)
                        }
                    } else {

                    }
                    listener.done(null)
                }
            })
        } else {
            listener.done(null)
        }
    }

    /**
     * 查询指定用户
     *
     * @param objectId 用户的objectId
     * @param listener 用户查询监听器
     */
    fun queryUserInfo(objectId: String, listener: QueryUserListener){
        val query = BmobQuery<User>()
        query.addWhereEqualTo("objectId", objectId)
        query.findObjects(object: FindListener<User>(){
            override fun done(p0: MutableList<User>?, p1: BmobException?) {
                if(p1 == null){
                    if(p0 != null && p0.size > 0){
                        listener.done(p0[0], null)
                    } else {
                        listener.done(null, BmobException(CANNOT_FOUND_USER))
                    }
                } else {
                    listener.done(null, p1)
                }
            }
        })
    }
}