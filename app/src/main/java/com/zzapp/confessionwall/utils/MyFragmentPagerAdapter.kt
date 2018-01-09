package com.zzapp.confessionwall.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.zzapp.confessionwall.ui.FollowFragment
import com.zzapp.confessionwall.ui.HotFragment
import com.zzapp.confessionwall.ui.MeFragment
import com.zzapp.confessionwall.ui.MessageFragment

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class MyFragmentPagerAdapter(fragmentManager: FragmentManager, private val titles : List<String>) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> FollowFragment()
            1 -> MessageFragment()
            2 -> HotFragment()
            else -> MeFragment()
        }
    }

    override fun getCount(): Int {
        return titles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}