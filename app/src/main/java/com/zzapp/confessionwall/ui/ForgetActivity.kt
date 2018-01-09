package com.zzapp.confessionwall.ui

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.presenter.UserPresenter
import com.zzapp.confessionwall.utils.MD5
import com.zzapp.confessionwall.view.IUserView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.forget.*
import org.mindrot.jbcrypt.BCrypt
import java.util.regex.Pattern

/**
 * Project ConfessionWall
 * Date 2017-12-11
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

        encryption_password.hint = getString(R.string.encryption_password)
        val passwordEdit = encryption_password.editText!!

        encryption_name.hint = getString(R.string.name)
        val nameEdit = encryption_name.editText!!

        encryption.setOnClickListener {
            if(nameEdit.text.toString().length < 2){
                encryption_name.error = getString(R.string.name_too_short)
                encryption_name.isErrorEnabled = true
                return@setOnClickListener
            }
            encryption_name.isErrorEnabled = false
            if(passwordEdit.text.toString().length < 6){
                encryption_password.error = getString(R.string.password_too_short)
                encryption_password.isErrorEnabled = true
                return@setOnClickListener
            }
            encryption_password.isErrorEnabled = false

            encryption_text.text = BCrypt.hashpw(passwordEdit.text.toString(), "$2a$12$" + MD5.md5(nameEdit.text.toString()))
        }

        encryption_text.setOnLongClickListener{
            val cb = this.getSystemService(android.app.Service.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(null, encryption_text.text.toString())
            cb.primaryClip = clipData
            Toasty.success(this@ForgetActivity, getString(R.string.copy_success)).show()
            true
        }
    }

    override fun onSuccess() {
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