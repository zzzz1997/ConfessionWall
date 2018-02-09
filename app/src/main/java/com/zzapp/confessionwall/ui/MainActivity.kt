package com.zzapp.confessionwall.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMUserInfo
import cn.bmob.newim.core.ConnectionStatus
import cn.bmob.newim.event.MessageEvent
import cn.bmob.newim.listener.ConnectListener
import cn.bmob.newim.listener.ConnectStatusChangeListener
import cn.bmob.newim.notification.BmobNotificationManager
import cn.bmob.push.BmobPush
import cn.bmob.v3.*
import cn.bmob.v3.exception.BmobException
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.utils.MyFragmentPagerAdapter
import com.zzapp.confessionwall.entity.User
import com.zzapp.confessionwall.entity.TabEntity
import com.zzapp.confessionwall.ui.fragment.FollowFragment
import com.zzapp.confessionwall.ui.fragment.HotFragment
import com.zzapp.confessionwall.ui.fragment.MeFragment
import com.zzapp.confessionwall.ui.fragment.MessageFragment
import com.zzapp.confessionwall.utils.MyCode
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Project MeiZhi
 * Date 2017-12-10
 *
 * 主界面函数
 *
 * @author zzzz
 */
class MainActivity : AppCompatActivity() {

    private val appkey = "0609b5cda2401bf3d1c4bae43b834950"

    private val titles: ArrayList<String> = ArrayList()
    private val selectedIcons = listOf(R.drawable.follow_selected, R.drawable.message_selected,
            R.drawable.hot_selected, R.drawable.me_selected)
    private val unSelectIcons = listOf(R.drawable.follow_normal, R.drawable.message_normal,
            R.drawable.hot_normal, R.drawable.me_normal)

    private val tabEntities: ArrayList<CustomTabEntity> = ArrayList()

    companion object {
        //存储fragment的列表
        val fragments: ArrayList<BaseFragment> = ArrayList()
    }

    private var first = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        //初始化应用
        Bmob.initialize(this, appkey)

        val user = BmobUser.getCurrentUser(User::class.java)
        if(user != null){
            BmobIM.connect(user.objectId, object: ConnectListener(){
                override fun done(p0: String?, p1: BmobException?) {
                    if(p1 == null){
                        BmobIM.getInstance().updateUserInfo(BmobIMUserInfo(user.objectId, user.username, user.icon))
                    } else {
                        Toasty.error(this@MainActivity, p1.message!!).show()
                    }
                }
            })
            BmobIM.getInstance().setOnConnectStatusChangeListener(object: ConnectStatusChangeListener(){
                override fun onChange(p0: ConnectionStatus?) {
                    Toasty.info(this@MainActivity, p0!!.msg).show()
                }
            })
        }

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
        (0 until titles.size).mapTo(tabEntities) { TabEntity(titles[it], selectedIcons[it], unSelectIcons[it]) }

        fragments.add(FollowFragment())
        fragments.add(MessageFragment())
        fragments.add(HotFragment())
        fragments.add(MeFragment())
        view_pager.adapter = MyFragmentPagerAdapter(supportFragmentManager, titles, fragments)
        view_pager.offscreenPageLimit = 3
        view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                common_tab_layout.currentTab = position
            }
        })

        common_tab_layout.setTabData(tabEntities)
        common_tab_layout.setOnTabSelectListener(object: OnTabSelectListener{
            override fun onTabSelect(position: Int) {
                view_pager.currentItem = position
            }

            override fun onTabReselect(position: Int) {
                if(position == 1){
                    common_tab_layout.showMsg(1, ((Math.random() * 200).toInt()))
                }
            }
        })

        //BmobNotificationManager.getInstance(this).showNotification(MessageEvent(), Intent())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            MyCode.REQUEST_LOGIN -> {
                if(resultCode == AppCompatActivity.RESULT_OK){
                    val user = data!!.getSerializableExtra("user") as User
                    for(fragment in fragments){
                        fragment.user = user
                        fragment.refresh()
                    }
                }
            }
            MyCode.REQUEST_ADD_POST -> {
                if(resultCode == AppCompatActivity.RESULT_OK) {
                    fragments[0].push(MyCode.REQUEST_ADD_POST, data)
                }
            }
            else -> return
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val second = System.currentTimeMillis()
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(second - first < 2000){
                BmobIM.getInstance().disConnect()
                System.exit(0)
            } else {
                Toasty.warning(this, "再点一次退出程序哦").show()
                first = System.currentTimeMillis()
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        BmobIM.getInstance().clear()
        BmobIM.getInstance().disConnect()
    }
}