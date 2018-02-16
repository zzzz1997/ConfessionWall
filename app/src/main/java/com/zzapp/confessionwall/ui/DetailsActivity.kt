package com.zzapp.confessionwall.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.datatype.BmobPointer
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.entity.Post
import com.zzapp.confessionwall.entity.User
import com.zzapp.confessionwall.adapter.OnPostClickListener
import com.zzapp.confessionwall.adapter.PostAdapter
import es.dmoral.toasty.Toasty
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.CropTransformation
import kotlinx.android.synthetic.main.details.*

/**
 * Project ConfessionWall
 * Date 2018-01-31
 *
 * 用户详情界面
 *
 * @author zzzz
 */
class DetailsActivity : AppCompatActivity() {

    private val BLUR_RADIUS = 100

    private lateinit var user: User
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)

        initView()
    }

    /**
     * 初始化界面
     */
    private fun initView(){
        user = intent.getSerializableExtra("user") as User

        details_toolbar_layout.title = user.username
        details_toolbar.setNavigationOnClickListener {
            finish()
        }

        Glide.with(this)
                .load(user.icon)
                .apply(bitmapTransform(MultiTransformation(BlurTransformation(BLUR_RADIUS),
                        CropTransformation(60, 20, CropTransformation.CropType.CENTER))))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(details_blur_background)

        Glide.with(this)
                .load(user.icon)
                .into(details_icon)

        details_relation.text = "测试"
        details_signature.text = user.signature

        details_refresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light)
        details_refresh.setOnRefreshListener {
            refresh()
        }

        details_refresh.isRefreshing = true
        refresh()
    }

    /**
     * 界面刷新
     */
    private fun refresh(){
        val query = BmobQuery<Post>()
        query.addWhereEqualTo("author", BmobPointer(user))
        query.include("author")
        query.order("-updatedAt")
        query.findObjects(object: FindListener<Post>(){
            override fun done(p0: MutableList<Post>?, p1: BmobException?) {
                if(p1 == null){
                    if(p0!!.size <= 0){
                        details_warning.visibility = View.VISIBLE
                        details_warning.text = getString(R.string.no_post_warning)
                        details_recycler.visibility = View.GONE
                    } else {
                        details_warning.visibility = View.GONE
                        details_recycler.visibility = View.VISIBLE
                        adapter = PostAdapter(this@DetailsActivity, p0, user)
                        adapter.setOnBaseClickListener(OnPostClickListener(this@DetailsActivity, user, p0, adapter))
                        details_recycler.adapter = adapter
                        details_recycler.layoutManager = LinearLayoutManager(this@DetailsActivity)
                    }
                } else {
                    Toasty.error(this@DetailsActivity, p1.message!!).show()
                }
            }
        })
        details_refresh.isRefreshing = false
    }
}