package com.zzapp.confessionwall.ui

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.DownloadFileListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.utils.User
import com.zzapp.confessionwall.view.BaseFragment
import es.dmoral.toasty.Toasty
import java.io.File

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class MeFragment : BaseFragment() {

    private lateinit var toolbar: Toolbar
    private lateinit var image: ImageView
    private lateinit var name: TextView
    private lateinit var login: Button
    private lateinit var exit: Button

    private lateinit var user: User

    override fun setContentView(): Int {
        return R.layout.me_frag
    }

    override fun initView() {
        toolbar = findViewById(R.id.me_toolbar) as Toolbar
        image = findViewById(R.id.me_image) as ImageView
        name = findViewById(R.id.me_name) as TextView
        login = findViewById(R.id.start_login) as Button
        exit = findViewById(R.id.exit_login) as Button

        setHasOptionsMenu(true)
        (activity!! as AppCompatActivity).setSupportActionBar(toolbar)
        (activity!! as AppCompatActivity).supportActionBar!!.title = null
        Log.e("me", "init")

        user = BmobUser.getCurrentUser(User::class.java)

        image.setOnClickListener {
            startActivityForResult(Intent(activity, UserActivity::class.java), 0)
        }

        login.setOnClickListener {
            startActivityForResult(Intent(activity, LoginActivity::class.java), 1)
        }

        exit.setOnClickListener {
            BmobUser.logOut()
            refresh(user)
        }
    }

    override fun loadView() {
        refresh(user)
    }

    override fun stopLoad() {

    }

    override fun refresh(user: User?) {

        if(user != null){
            image.visibility = View.VISIBLE
            name.visibility = View.VISIBLE
            login.visibility = View.GONE
            exit.visibility = View.VISIBLE

            name.text = user.username

            val icon = if(user.icon == getString(R.string.default_icon)){
                File(activity!!.cacheDir.absolutePath + "/bmob/default.png")
            } else {
                File(activity!!.cacheDir.absolutePath + "/bmob/${user.username}.png")
            }
            if(!icon.exists()){
                BmobFile(icon.name, null, user.icon)
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
            image.visibility = View.GONE
            name.visibility = View.GONE
            login.visibility = View.VISIBLE
            exit.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            refresh(user)
        }
    }
}