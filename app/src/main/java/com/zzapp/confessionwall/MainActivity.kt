package com.zzapp.confessionwall

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import cn.bmob.push.BmobPush
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobInstallation
import cn.bmob.v3.BmobInstallationManager
import cn.bmob.v3.InstallationListener
import cn.bmob.v3.exception.BmobException
import com.zzapp.confessionwall.ui.FollowFragment
import com.zzapp.confessionwall.ui.HotFragment
import com.zzapp.confessionwall.ui.MeFragment
import com.zzapp.confessionwall.ui.MessageFragment
import com.zzapp.confessionwall.utils.MyFragmentPagerAdapter
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Project MeiZhi
 * Date 2017-12-10
 *
 * @author zzzz
 */
class MainActivity : AppCompatActivity() {

    val appkey = "0609b5cda2401bf3d1c4bae43b834950"

    private val titles: ArrayList<String> = ArrayList()

    companion object {
        val fragments: ArrayList<BaseFragment> = ArrayList()
    }

    private var first = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        Bmob.initialize(this, appkey)
        BmobInstallationManager.getInstance().initialize(object: InstallationListener<BmobInstallation>(){
            override fun done(p0: BmobInstallation?, p1: BmobException?) {
                if(p1 == null){

                } else {
                    Toasty.error(this@MainActivity, p1.message!!).show()
                }
            }
        })
        BmobPush.startWork(this)

        titles.add(getString(R.string.follow))
        titles.add(getString(R.string.message))
        titles.add(getString(R.string.hot))
        titles.add(getString(R.string.me))
        fragments.add(FollowFragment())
        fragments.add(MessageFragment())
        fragments.add(HotFragment())
        fragments.add(MeFragment())
        view_pager.adapter = MyFragmentPagerAdapter(supportFragmentManager, titles, fragments)
        view_pager.offscreenPageLimit = 3
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val second = System.currentTimeMillis()
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(second - first < 2000){
                System.exit(0)
            } else {
                Toasty.warning(this, "再点一次退出程序哦").show()
                first = System.currentTimeMillis()
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}