package com.zzapp.confessionwall.entity

import com.flyco.tablayout.listener.CustomTabEntity

/**
 * Project ConfessionWall
 * Date 2018-02-02
 *
 * 主界面的tab实体类
 *
 * @author zzzz
 */
class TabEntity(private val title: String, private val selectedIcon: Int, private val unSelectedIcon: Int) : CustomTabEntity {

    override fun getTabTitle(): String {
        return title
    }

    override fun getTabSelectedIcon(): Int {
        return selectedIcon
    }

    override fun getTabUnselectedIcon(): Int {
        return unSelectedIcon
    }
}