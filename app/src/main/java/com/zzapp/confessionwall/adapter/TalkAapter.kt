package com.zzapp.confessionwall.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import cn.bmob.newim.bean.BmobIMMessage
import com.bumptech.glide.Glide
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.adapter.base.BaseAdapter
import com.zzapp.confessionwall.entity.User
import com.zzapp.confessionwall.view.CircleImageView

/**
 * Project ConfessionWall
 * Date 2018-02-15
 *
 * 聊天界面适配器
 *
 * @author zzzz
 */
class TalkAapter(private val context: Context, private val messages: MutableList<BmobIMMessage>, private val user: User, private val icon: String)
    : BaseAdapter<BmobIMMessage, TalkAapter.TalkViewHolder, TalkAapter.OnTalkClickListener>(messages), View.OnClickListener, View.OnLongClickListener {

    private lateinit var onTalkClickListener: OnTalkClickListener

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TalkViewHolder {
        return TalkViewHolder(LayoutInflater.from(context).inflate(R.layout.talk_item, parent, false))
    }

    override fun onBindViewHolder(holder: TalkViewHolder?, position: Int) {
        if(messages[position].fromId != user.objectId){
            holder!!.meLayout.visibility = View.GONE
            Glide.with(context)
                    .load(icon)
                    .into(holder.userIcon)
            holder.userContent.text = messages[position].content

            holder.userContent.tag = position
            holder.userIcon.setOnClickListener(this)
            holder.userContent.setOnLongClickListener(this)
        } else {
            holder!!.userLayout.visibility = View.GONE
            Glide.with(context)
                    .load(user.icon)
                    .into(holder.meIcon)
            holder.meContent.text = messages[position].content

            holder.meContent.tag = position
            holder.meIcon.setOnClickListener(this)
            holder.meContent.setOnLongClickListener(this)
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.talk_item_user_icon -> {
                onTalkClickListener.onUserIconClicked()
            }
            R.id.talk_item_me_icon -> {
                onTalkClickListener.onMeIconClicked()
            }
            else -> return
        }
    }

    override fun onLongClick(p0: View?): Boolean {
        onTalkClickListener.onLongClicked(p0!!, p0.tag as Int)
        return true
    }

    override fun setOnBaseClickListener(onBaseClickListener: OnTalkClickListener) {
        this.onTalkClickListener = onBaseClickListener
    }

    class TalkViewHolder(itemView: View) : BaseViewHolder(itemView){
        val userLayout = itemView.findViewById<LinearLayout>(R.id.talk_item_user_layout)!!
        val userIcon = itemView.findViewById<CircleImageView>(R.id.talk_item_user_icon)!!
        val userContent = itemView.findViewById<TextView>(R.id.talk_item_user_content)!!
        val meLayout = itemView.findViewById<LinearLayout>(R.id.talk_item_me_layout)!!
        val meIcon = itemView.findViewById<CircleImageView>(R.id.talk_item_me_icon)!!
        val meContent = itemView.findViewById<TextView>(R.id.talk_item_me_content)!!
    }

    interface OnTalkClickListener : OnBaseClickListener {
        fun onUserIconClicked()
        fun onMeIconClicked()
        fun onLongClicked(view: View, position: Int)
    }
}