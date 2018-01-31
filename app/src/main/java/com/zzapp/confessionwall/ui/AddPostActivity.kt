package com.zzapp.confessionwall.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Post
import com.zzapp.confessionwall.data.User
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.add_post.*

/**
 * Project ConfessionWall
 * Date 2018-01-16
 *
 * 新建动态的活动
 *
 * @author zzzz
 */
class AddPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_post)
        initView()
    }

    private fun initView(){
        setSupportActionBar(add_post_toolbar)
        supportActionBar!!.title = null
        val user = BmobUser.getCurrentUser(User::class.java)

        add_post_toolbar.setNavigationOnClickListener {
            finish()
        }

        add_post_toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.add_post_publish -> {
                    val content = add_post_edit.text.toString()
                    if(content.isEmpty()){
                        Toasty.warning(this@AddPostActivity, getString(R.string.content_cannot_be_null)).show()
                    } else {
                        val post = Post()
                        post.author = user
                        post.content = content
                        post.save(object: SaveListener<String>(){
                            override fun done(p0: String?, p1: BmobException?) {
                                if(p1 == null){
                                    val intent = Intent()
                                    intent.putExtra("post", post)
                                    setResult(AppCompatActivity.RESULT_OK, intent)
                                } else {
                                    Toasty.error(this@AddPostActivity, p1.message!!).show()
                                }
                                finish()
                            }
                        })
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_post_menu, menu)
        return true
    }
}