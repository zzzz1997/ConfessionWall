package com.zzapp.confessionwall.presenter

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
interface IUserPresenter {

    /**
     * 登录
     *
     * @param name 用户名
     * @param password 密码
     */
    fun login(name: String, password: String)

    /**
     * 注册
     *
     * @param name 用户名
     * @param password 密码
     * @param email 邮箱
     */
    fun register(name: String, password: String, email: String, icon: String)

    /**
     * 重置密码
     *
     * @param email 注册的邮箱
     */
    fun reset(email: String)
}