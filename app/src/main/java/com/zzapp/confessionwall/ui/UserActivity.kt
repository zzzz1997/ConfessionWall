package com.zzapp.confessionwall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import cn.bmob.v3.BmobUser
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.utils.User
import kotlinx.android.synthetic.main.user.*
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.datatype.BmobRelation
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import com.zzapp.confessionwall.data.Comment
import com.zzapp.confessionwall.data.Post
import es.dmoral.toasty.Toasty
import java.io.FileOutputStream


/**
 * Project ConfessionWall
 * Date 2017-12-12
 *
 * @author zzzz
 */
class UserActivity : AppCompatActivity() {

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
            startActivityForResult(intent, 0)
        }

        //查询用户评论
        user_name.setOnClickListener {
            val query = BmobQuery<Comment>()
            val post = Post()
            post.objectId = "32c3d29268"
            query.addWhereEqualTo("post", BmobPointer(post))
            query.include("user,post.author")
            query.findObjects(object: FindListener<Comment>(){
                override fun done(p0: MutableList<Comment>?, p1: BmobException?) {
                    if(p1 == null){
                        Toasty.info(this@UserActivity, "" + p0!!.size).show()
                    } else {
                        Toasty.error(this@UserActivity, p1.message as CharSequence).show()
                    }
                }
            })
        }

        //用户评论
        user_email.setOnClickListener {
            val post = Post()
            post.objectId = "32c3d29268"
            val comment = Comment()
            comment.content = "测试评论"
            comment.user = user
            comment.post = post
            comment.save(object: SaveListener<String>(){
                override fun done(p0: String?, p1: BmobException?) {
                    if(p1 == null){
                        val query = BmobQuery<Comment>()
                        query.addWhereEqualTo("post", BmobPointer(post))
                        query.include("user,post.author")
                        query.findObjects(object: FindListener<Comment>(){
                            override fun done(p0: MutableList<Comment>?, p1: BmobException?) {
                                if(p1 == null){
                                    post.commentsNum = p0!!.size
                                    post.update(object: UpdateListener(){
                                        override fun done(p0: BmobException?) {
                                            if(p0 == null){
                                                Toasty.success(this@UserActivity,"成功").show()
                                            } else {
                                                Toasty.error(this@UserActivity, p0.message as CharSequence).show()
                                            }
                                        }
                                    })
                                } else {
                                    Toasty.error(this@UserActivity, p1.message as CharSequence).show()
                                }
                            }
                        })
                    } else {
                        Toasty.error(this@UserActivity, p1.message as CharSequence).show()
                    }
                }
            })
        }

        //用户贴数
        user_phone.setOnClickListener {
            val query = BmobQuery<Post>()
            query.addWhereEqualTo("author", user)
            query.order("-updatedAt")
            query.include("author")
            query.findObjects(object: FindListener<Post>(){
                override fun done(p0: MutableList<Post>?, p1: BmobException?) {
                    if(p1 == null){
                        Toasty.info(this@UserActivity, "" + p0!!.size).show()
                    } else {
                        Toasty.error(this@UserActivity, p1.message as CharSequence).show()
                    }
                }
            })
        }

        //发布新帖
        user_create.setOnClickListener {
            val post = Post()
            post.content = "测试贴"
            post.author = user
            post.save(object: SaveListener<String>(){
                override fun done(p0: String?, p1: BmobException?) {
                    if(p1 == null){
                        Toasty.success(this@UserActivity, "成功").show()
                    } else {
                        Toasty.error(this@UserActivity, p1.message as CharSequence).show()
                    }
                }
            })
        }

        //收藏
        user_create.setOnLongClickListener {
            val post = Post()
            post.objectId = "32c3d29268"
            val relation = BmobRelation()
            relation.add(user)
            post.collections = relation
            post.update(object: UpdateListener(){
                override fun done(p0: BmobException?) {
                    if(p0 == null){
                        Toasty.success(this@UserActivity, "成功").show()
                    } else {
                        Toasty.error(this@UserActivity, p0.message as CharSequence).show()
                    }
                }
            })

            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0){
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
                startActivityForResult(intent, 1)
            }
        } else if(requestCode == 1) {
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
                                        Toasty.error(this@UserActivity, p0.message as CharSequence).show()
                                    }
                                }
                            })
                        } else {
                            Toasty.error(this@UserActivity, p0.message as CharSequence).show()
                        }
                    }
                })
            }
        }
    }
}