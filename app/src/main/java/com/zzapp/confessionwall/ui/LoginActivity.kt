package com.zzapp.confessionwall.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.zzapp.confessionwall.R
import kotlinx.android.synthetic.main.login.*
import com.zzapp.confessionwall.presenter.UserPresenter
import com.zzapp.confessionwall.utils.User
import com.zzapp.confessionwall.view.IUserView
import es.dmoral.toasty.Toasty

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * 登录的活动
 *
 * @author zzzz
 */
class LoginActivity : AppCompatActivity(), IUserView {

    private lateinit var userPresenter: UserPresenter
    private lateinit var dialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        initView()
    }

    private fun initView(){
        userPresenter = UserPresenter(this)

        login_toolbar.setNavigationOnClickListener {
            finish()
        }

        login_name.hint = getString(R.string.name)
        val nameEdit = login_name.editText!!

        login_password.hint = getString(R.string.password)
        val passwordEdit = login_password.editText!!

        forget_password.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgetActivity::class.java))
        }

        quick_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        login.setOnClickListener {
            if(nameEdit.text.toString().length < 2){
                login_name.error = getString(R.string.name_too_short)
                login_name.isErrorEnabled = true
                return@setOnClickListener
            }
            login_name.isErrorEnabled = false
            if(passwordEdit.text.toString().length < 6){
                login_password.error = getString(R.string.password_too_short)
                login_password.isErrorEnabled = true
                return@setOnClickListener
            }
            login_password.isErrorEnabled = false

            userPresenter.login(login_name.editText!!.text.toString(), login_password.editText!!.text.toString())
        }
    }

    override fun onSuccess(user: User?) {
        Toasty.success(this@LoginActivity, getString(R.string.login_success)).show()
        val intent = Intent()
        intent.putExtra("user", user)
        setResult(AppCompatActivity.RESULT_OK, intent)
        finish()
    }

    override fun onFailure(string: String) {
        Toasty.error(this@LoginActivity, string).show()
    }

    override fun newDialog() {
        val view  = layoutInflater.inflate(R.layout.progress_dialog, null)
        view.findViewById<TextView>(R.id.progress_dialog_text).text = getString(R.string.log_in)
        dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()
        dialog.show()
    }

    override fun dismissDialog() {
        dialog.dismiss()
    }
}