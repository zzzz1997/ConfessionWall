package com.zzapp.confessionwall.view

import com.zzapp.confessionwall.entity.User

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * 处理用户操作的接口
 *
 * @author zzzz
 */
interface IUserView {

    /**
     * 登录成功
     */
    fun onSuccess(user: User?)

    /**
     * 登录失败
     *
     * @param string 失败信息
     */
    fun onFailure(string: String)

    /**
     * 新建progress dialog提示
     */
    fun newDialog()

    /**
     * 取消dialog
     */
    fun dismissDialog()
}