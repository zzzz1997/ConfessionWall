package com.zzapp.confessionwall.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.zzapp.confessionwall.view.BaseFragment

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * 切换fragment的适配器
 *
 * @author zzzz
 */
class MyFragmentPagerAdapter(fragmentManager: FragmentManager, private val titles : List<String>, private val fragments: ArrayList<BaseFragment>) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return titles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}