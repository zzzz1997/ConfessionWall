package com.zzapp.confessionwall.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.data.Post
import kotlinx.android.synthetic.main.dynamic.*

/**
 * Project ConfessionWall
 * Date 2018-01-09
 *
 * @author zzzz
 */
class DynamicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamic)

        initView()
    }

    private fun initView(){
        val post = intent.getSerializableExtra("post") as Post

        dynamic_toolbar.setNavigationOnClickListener {
            finish()
        }

        Glide.with(this)
                .load(post.author!!.icon)
                .into(dynamic_icon)
        dynamic_name.text = post.author!!.username
        dynamic_content.text = post.content
    }
}