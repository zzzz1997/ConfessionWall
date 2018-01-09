package com.zzapp.confessionwall

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import cn.bmob.v3.Bmob
import com.zzapp.confessionwall.utils.MyFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Project MeiZhi
 * Date 2017-12-10
 *
 * @author zzzz
 */
class MainActivity : AppCompatActivity() {

    private val titles: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        Bmob.initialize(this, "0609b5cda2401bf3d1c4bae43b834950")

        titles.add(getString(R.string.follow))
        titles.add(getString(R.string.message))
        titles.add(getString(R.string.hot))
        titles.add(getString(R.string.me))
        view_pager.adapter = MyFragmentPagerAdapter(supportFragmentManager, titles)
        tab_layout.setupWithViewPager(view_pager)
        tab_layout.getTabAt(0)!!.icon = ContextCompat.getDrawable(this, R.drawable.follow_selected)
        tab_layout.getTabAt(1)!!.icon = ContextCompat.getDrawable(this, R.drawable.message_normal)
        tab_layout.getTabAt(2)!!.icon = ContextCompat.getDrawable(this, R.drawable.hot_normal)
        tab_layout.getTabAt(3)!!.icon = ContextCompat.getDrawable(this, R.drawable.me_normal)
        tab_layout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab!!.icon = ContextCompat.getDrawable(this@MainActivity, when(tab.text){
                    getString(R.string.follow) -> R.drawable.follow_normal
                    getString(R.string.message) -> R.drawable.message_normal
                    getString(R.string.hot) -> R.drawable.hot_normal
                    else -> R.drawable.me_normal
                })
                tab_layout.setTabTextColors(ContextCompat.getColor(this@MainActivity, R.color.colorText),
                        ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab!!.icon = ContextCompat.getDrawable(this@MainActivity, when(tab.text){
                    getString(R.string.follow) -> R.drawable.follow_selected
                    getString(R.string.message) -> R.drawable.message_selected
                    getString(R.string.hot) -> R.drawable.hot_selected
                    else -> R.drawable.me_selected
                })
                tab_layout.setTabTextColors(ContextCompat.getColor(this@MainActivity, R.color.colorText),
                        ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
            }
        })
    }
}