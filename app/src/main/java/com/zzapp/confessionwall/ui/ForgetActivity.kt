package com.zzapp.confessionwall.ui

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.presenter.UserPresenter
import com.zzapp.confessionwall.utils.User
import com.zzapp.confessionwall.view.IUserView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.forget.*
import java.util.regex.Pattern

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * 重置密码的活动
 *
 * @author zzzz
 */
class ForgetActivity : AppCompatActivity(), IUserView {

    private lateinit var userPresenter: UserPresenter
    private lateinit var dialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget)

        initView()
    }

    private fun initView(){
        userPresenter = UserPresenter(this)

        forget_toolbar.setNavigationOnClickListener {
            finish()
        }

        forget_email.hint = getString(R.string.forget_email)
        val emailEdit = forget_email.editText!!

        reset.setOnClickListener {
            val pattern = Pattern.compile("^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|" +
                    "(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(]?)$")
            if(!pattern.matcher(emailEdit.text.toString()).matches()){
                forget_email.error = getString(R.string.email_wrong_format)
                forget_email.isErrorEnabled = true
                return@setOnClickListener
            }
            forget_email.isErrorEnabled = false

            userPresenter.reset(emailEdit.text.toString())
        }
    }

    override fun onSuccess(user: User?) {
        Toasty.success(this@ForgetActivity, getString(R.string.reset_success)).show()
        finish()
    }

    override fun onFailure(string: String) {
        Toasty.error(this@ForgetActivity, string).show()
    }

    override fun newDialog() {
        val view  = layoutInflater.inflate(R.layout.progress_dialog, null)
        view.findViewById<TextView>(R.id.progress_dialog_text).text = getString(R.string.resetting)
        dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()
        dialog.show()
    }

    override fun dismissDialog() {
        dialog.dismiss()
    }
}