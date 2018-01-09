package com.zzapp.confessionwall.view

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
interface IUserView {

    /**
     * 登录成功
     */
    fun onSuccess()

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