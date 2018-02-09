package com.zzapp.confessionwall.model.listener

import cn.bmob.newim.listener.BmobListener1
import cn.bmob.v3.exception.BmobException
import com.zzapp.confessionwall.entity.User

/**
 * Project ConfessionWall
 * Date 2018-02-08
 *
 * 用户查询监听器
 *
 * @author zzzz
 */
abstract class QueryUserListener : BmobListener1<User>() {

    override fun postDone(p0: User?, p1: BmobException?) {
        done(p0, p1)
    }

    /**
     * 监听器处理
     *
     * @param e 错误信息
     */
    abstract fun done(user: User?, e: BmobException?)
}