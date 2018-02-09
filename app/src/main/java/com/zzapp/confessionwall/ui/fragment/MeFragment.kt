package com.zzapp.confessionwall.ui.fragment

import android.content.Intent
import android.net.Uri
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.Button
import android.widget.ImageView
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.DownloadFileListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.ui.LoginActivity
import com.zzapp.confessionwall.ui.UserActivity
import com.zzapp.confessionwall.utils.MyCode
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty
import java.io.File

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * 用户界面的fragment
 *
 * @author zzzz
 */
class MeFragment : BaseFragment() {

    private lateinit var layout: CollapsingToolbarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var image: ImageView
    private lateinit var login: Button
    private lateinit var exit: Button

    override fun setContentView(): Int {
        return R.layout.me_frag
    }

    override fun initView() {
        layout = findViewById(R.id.me_toolbar_layout) as CollapsingToolbarLayout
        toolbar = findViewById(R.id.me_toolbar) as Toolbar
        image = findViewById(R.id.me_image) as ImageView
        login = findViewById(R.id.start_login) as Button
        exit = findViewById(R.id.exit_login) as Button

        (activity!! as AppCompatActivity).setSupportActionBar(toolbar)

        image.setOnClickListener {
            activity!!.startActivityForResult(Intent(activity, UserActivity::class.java), MyCode.REQUEST_ICON)
        }

        login.setOnClickListener {
            activity!!.startActivityForResult(Intent(activity, LoginActivity::class.java), MyCode.REQUEST_LOGIN)
        }

        exit.setOnClickListener {
            BmobUser.logOut()
            user = null
            refresh()
        }
    }

    override fun loadView() {
        refresh()
    }

    override fun stopLoad() {

    }

    override fun refresh() {
        if(user != null){
            image.visibility = View.VISIBLE
            login.visibility = View.GONE
            exit.visibility = View.VISIBLE

            layout.title = user!!.username

            val icon = if(user!!.icon == getString(R.string.default_icon)){
                File(activity!!.cacheDir.absolutePath + "/bmob/default.png")
            } else {
                File(activity!!.cacheDir.absolutePath + "/bmob/${user!!.username}.png")
            }
            if(!icon.exists()){
                BmobFile(icon.name, null, user!!.icon)
                        .download(icon, object : DownloadFileListener(){
                    override fun onProgress(p0: Int?, p1: Long) {}

                    override fun done(p0: String?, p1: BmobException?) {
                        if(p1 == null){
                            image.setImageURI(Uri.fromFile(icon))
                        } else {
                            Toasty.error(context!!, getString(R.string.load_icon_failed)).show()
                        }
                    }
                })
            } else {
                image.setImageURI(Uri.fromFile(icon))
            }
        } else {
            layout.title = getString(R.string.me)
            image.visibility = View.GONE
            login.visibility = View.VISIBLE
            exit.visibility = View.GONE
        }
    }

    override fun push(code: Int, data: Intent?) {

    }
}