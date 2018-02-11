package com.zzapp.confessionwall.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import com.bumptech.glide.Glide
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.entity.User
import com.zzapp.confessionwall.view.CircleImageView
import q.rorbin.badgeview.QBadgeView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Project ConfessionWall
 * Date 2018-02-11
 *
 * 消息界面的适配器
 *
 * @author zzzz
 */
class MessageAdapter(private val context: Context, private val conversation: MutableList<BmobIMConversation>, private val user: User?)
    : BaseAdapter<BmobIMConversation, MessageAdapter.MessageViewHolder, MessageAdapter.OnMessageClickListener>(conversation),
        View.OnClickListener, View.OnLongClickListener {

    @SuppressLint("SimpleDateFormat")
    private val  simpleDateFormat = SimpleDateFormat(context.getString(R.string.date_format))

    private lateinit var onMessageClickListener: OnMessageClickListener

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_item, parent, false))
    }

    override fun onBindViewHolder(holder: MessageViewHolder?, position: Int) {
        Glide.with(context)
                .load(conversation[position].conversationIcon)
                .into(holder!!.icon)
        holder.name.text = conversation[position].conversationTitle
        holder.time.text = simpleDateFormat.format(Date(conversation[position].updateTime))
        holder.content.text = conversation[position].messages[0].content
        QBadgeView(context).bindTarget(holder.badge).badgeNumber = BmobIM.getInstance().getUnReadCount(conversation[position].conversationId).toInt()

        conversation[position]
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(this)
        holder.itemView.setOnLongClickListener(this)
    }

    override fun onClick(p0: View?) {
        onMessageClickListener.onClicked(p0!!.tag as Int)
    }

    override fun onLongClick(p0: View?): Boolean {
        onMessageClickListener.onLongClicked(p0!!.tag as Int)
        return true
    }

    override fun setOnBaseClickListener(onBaseClickListener: OnMessageClickListener) {
        this.onMessageClickListener = onBaseClickListener
    }

    class MessageViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val icon = itemView.findViewById<CircleImageView>(R.id.message_item_icon)!!
        val name = itemView.findViewById<TextView>(R.id.message_item_name)!!
        val time = itemView.findViewById<TextView>(R.id.message_item_time)!!
        val content = itemView.findViewById<TextView>(R.id.message_item_content)!!
        val badge = itemView.findViewById<View>(R.id.message_item_badge)!!
    }

    interface OnMessageClickListener : OnBaseClickListener {
        fun onClicked(position: Int)
        fun onLongClicked(position: Int)
    }
}