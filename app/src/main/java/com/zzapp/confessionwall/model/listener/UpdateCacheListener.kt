package com.zzapp.confessionwall.model.listener

import cn.bmob.newim.listener.BmobListener1
import cn.bmob.v3.exception.BmobException

/**
 * Project ConfessionWall
 * Date 2018-02-08
 *
 * 资料更新监听器
 *
 * @author zzzz
 */
abstract class UpdateCacheListener : BmobListener1<Any>() {

    override fun postDone(p0: Any?, p1: BmobException?) {
        done(p1)
    }

    /**
     * 监听器处理
     *
     * @param e 错误信息
     */
    abstract fun done(e: BmobException?)
}