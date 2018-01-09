package com.zzapp.confessionwall.ui

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.presenter.UserPresenter
import com.zzapp.confessionwall.view.IUserView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.register.*
import java.util.regex.Pattern

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
class RegisterActivity : AppCompatActivity(), IUserView {

    private lateinit var userPresenter: UserPresenter
    private lateinit var dialog : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        initView()
    }

    private fun initView(){
        userPresenter = UserPresenter(this)

        register_toolbar.setNavigationOnClickListener {
            finish()
        }

        register_name.hint = getString(R.string.name)
        val nameEdit = register_name.editText!!

        register_password.hint = getString(R.string.password)
        val passwordEdit = register_password.editText!!

        password_again.hint = getString(R.string.password_again)
        val againEdit = password_again.editText!!

        register_email.hint = getString(R.string.email)
        val emailEdit = register_email.editText!!

        register.setOnClickListener {
            if(nameEdit.text.toString().length < 2){
                register_name.error = getString(R.string.name_too_short)
                register_name.isErrorEnabled = true
                return@setOnClickListener
            }
            register_name.isErrorEnabled = false
            if(passwordEdit.text.toString().length < 6){
                register_password.error = getString(R.string.password_too_short)
                register_password.isErrorEnabled = true
                return@setOnClickListener
            }
            register_password.isErrorEnabled = false
            if(againEdit.text.toString() != passwordEdit.text.toString()){
                password_again.error = getString(R.string.password_not_matching)
                password_again.isErrorEnabled = true
                return@setOnClickListener
            }
            password_again.isErrorEnabled = false
            val pattern = Pattern.compile("^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|" +
                    "(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(]?)$")
            if(!pattern.matcher(emailEdit.text.toString()).matches()){
                register_email.error = getString(R.string.email_wrong_format)
                register_email.isErrorEnabled = true
                return@setOnClickListener
            }
            register_email.isErrorEnabled = false

            userPresenter.register(nameEdit.text.toString(), passwordEdit.text.toString(), emailEdit.text.toString(), getString(R.string.default_icon))
        }
    }

    override fun onSuccess() {
        Toasty.success(this@RegisterActivity, getString(R.string.register_success)).show()
        finish()
    }

    override fun onFailure(string: String) {
        Toasty.error(this@RegisterActivity, string).show()
    }

    override fun newDialog() {
        val view  = layoutInflater.inflate(R.layout.progress_dialog, null)
        view.findViewById<TextView>(R.id.progress_dialog_text).text = getString(R.string.registering)
        dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()
        dialog.show()
    }

    override fun dismissDialog() {
        dialog.dismiss()
    }
}