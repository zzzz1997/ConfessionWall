package com.zzapp.confessionwall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import cn.bmob.v3.BmobUser
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.entity.User
import kotlinx.android.synthetic.main.user.*
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import es.dmoral.toasty.Toasty
import java.io.FileOutputStream


/**
 * Project ConfessionWall
 * Date 2017-12-12
 *
 * 用户界面的活动
 *
 * @author zzzz
 */
class UserActivity : AppCompatActivity() {

    private val REQUEST_ICON = 0
    private val REQUEST_UPLOAD = 1

    private val UPLOAD_CACHE_PYTH = Uri.parse("file:///" + Environment.getExternalStorageDirectory().absolutePath + "/head_portrait.png")

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user)

        initView()
    }

    private fun initView(){
        user_toolbar.setNavigationOnClickListener {
            finish()
        }

        user = BmobUser.getCurrentUser(User::class.java)

        val icon = if(user.icon == getString(R.string.default_icon)){
            File(cacheDir.absolutePath + "/bmob/default.png")
        } else {
            File(cacheDir.absolutePath + "/bmob/${user.username}.png")
        }
        if (icon.exists()){
            user_icon.setImageURI(Uri.fromFile(icon))
        } else {
            Toasty.error(this@UserActivity, getString(R.string.load_icon_failed)).show()
        }

        user_name.text = user.username
        user_email.text = user.email
        user_phone.text = user.mobilePhoneNumber
        user_create.text = user.createdAt

        user_icon_layout.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent, REQUEST_ICON)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_ICON){
            if(data != null){
                val intent = Intent("com.android.camera.action.CROP")
                intent.setDataAndType(data.data, "image/*")
                intent.putExtra("crop", "true")
                intent.putExtra("aspectX", 1)
                intent.putExtra("aspectY", 1)
                intent.putExtra("outputX", 300)
                intent.putExtra("outputY", 300)
                intent.putExtra("return-data", false)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, UPLOAD_CACHE_PYTH)
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
                intent.putExtra("noFaceDetection", true)
                startActivityForResult(intent, REQUEST_UPLOAD)
            }
        } else if(requestCode == REQUEST_UPLOAD) {
            val image = File(UPLOAD_CACHE_PYTH.path)
            if (image.exists()){
                val file = BmobFile(image)
                file.uploadblock(object : UploadFileListener(){
                    override fun done(p0: BmobException?) {
                        if (p0 == null){
                            user.icon = file.fileUrl
                            user.update(object : UpdateListener(){
                                override fun done(p0: BmobException?) {
                                    if(p0 == null){
                                        val bitmap = BitmapFactory.decodeFile(UPLOAD_CACHE_PYTH.path)
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(cacheDir.absolutePath + "/bmob/${user.username}.png"))
                                        user_icon.setImageURI(Uri.parse(cacheDir.absolutePath + "/bmob/${user.username}.png"))
                                    } else {
                                        Toasty.error(this@UserActivity, p0.message!!).show()
                                    }
                                }
                            })
                        } else {
                            Toasty.error(this@UserActivity, p0.message!!).show()
                        }
                    }
                })
            }
        }
    }
}