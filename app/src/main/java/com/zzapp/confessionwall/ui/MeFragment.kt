package com.zzapp.confessionwall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.DownloadFileListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.utils.User
import com.zzapp.confessionwall.view.IBaseView
import kotlinx.android.synthetic.main.me_frag.*
import java.io.File

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class MeFragment : Fragment(), IBaseView {

    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.me_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        me_image.setOnClickListener {
            startActivityForResult(Intent(activity, UserActivity::class.java), 0)
        }

        start_login.setOnClickListener {
            startActivityForResult(Intent(activity, LoginActivity::class.java), 1)
        }

        exit_login.setOnClickListener {
            BmobUser.logOut()
            fresh()
        }

        fresh()
    }

    override fun fresh() {
        user = BmobUser.getCurrentUser(User::class.java)

        if(user != null){
            me_image.visibility = View.VISIBLE
            me_name.visibility = View.VISIBLE
            start_login.visibility = View.GONE
            exit_login.visibility = View.VISIBLE

            me_name.text = user!!.username

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
                            me_image.setImageURI(Uri.fromFile(icon))
                        } else {
                            toast(getString(R.string.load_icon_failed))
                        }
                    }
                })
            } else {
                me_image.setImageURI(Uri.fromFile(icon))
            }
        } else {
            me_image.visibility = View.GONE
            me_name.visibility = View.GONE
            start_login.visibility = View.VISIBLE
            exit_login.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            fresh()
        }
    }

    private fun toast(string: String){
        Toast.makeText(activity!!.applicationContext, string, Toast.LENGTH_SHORT).show()
    }
}