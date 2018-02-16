package com.zzapp.confessionwall.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import cn.bmob.newim.BmobIM
import cn.bmob.newim.bean.BmobIMConversation
import com.bumptech.glide.Glide
import com.zzapp.confessionwall.R
import com.zzapp.confessionwall.adapter.base.BaseAdapter
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
class MessageAdapter(private val context: Context, private val conversation: MutableList<BmobIMConversation>, private val screenWidth: Int)
    : BaseAdapter<BmobIMConversation, MessageAdapter.MessageViewHolder, MessageAdapter.OnMessageClickListener>(conversation),
        View.OnClickListener, View.OnLongClickListener {

    private lateinit var layoutParams: ViewGroup.LayoutParams
    @SuppressLint("SimpleDateFormat")
    private val  simpleDateFormat = SimpleDateFormat(context.getString(R.string.date_format))

    private lateinit var onMessageClickListener: OnMessageClickListener

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageViewHolder {
        return MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.message_item, parent, false))
    }

    override fun onBindViewHolder(holder: MessageViewHolder?, position: Int) {
        layoutParams = holder!!.mIn.layoutParams
        layoutParams.width = screenWidth

        Glide.with(context)
                .load(conversation[position].conversationIcon)
                .into(holder.icon)
        holder.name.text = conversation[position].conversationTitle
        holder.time.text = simpleDateFormat.format(Date(conversation[position].updateTime))
        holder.content.text = conversation[position].messages[0].content
        QBadgeView(context).bindTarget(holder.badge).badgeNumber = BmobIM.getInstance().getUnReadCount(conversation[position].conversationId).toInt()

        holder.itemView.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action){
                MotionEvent.ACTION_UP -> {
                    val scrollX = holder.scrollView.scrollX
                    val width = holder.out.layoutParams.width
                    if(scrollX < width / 2){
                        holder.scrollView.smoothScrollTo(0, 0)
                    } else {
                        holder.scrollView.smoothScrollTo(width, 0)
                    }
                    true
                }
                else -> false
            }
        }

        if(holder.scrollView.scrollX != 0){
            holder.scrollView.smoothScrollTo(0, 0)
        }

        holder.mIn.tag = position
        holder.top.tag = position
        holder.del.tag = position
        holder.mIn.setOnClickListener(this)
        holder.mIn.setOnLongClickListener(this)
        holder.top.setOnClickListener(this)
        holder.del.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.message_item_in -> {
                val horizontalScrollView = p0.parent.parent as HorizontalScrollView
                if(horizontalScrollView.scrollX != 0){
                    horizontalScrollView.smoothScrollTo(0, 0)
                } else {
                    onMessageClickListener.onClicked(p0.tag as Int)
                }
            }
            R.id.message_item_top -> onMessageClickListener.onTopClicked(p0.tag as Int)
            R.id.message_item_delete -> onMessageClickListener.onDeleteClicked(p0.tag as Int)
            else -> return
        }
    }

    override fun onLongClick(p0: View?): Boolean {
        val horizontalScrollView = p0!!.parent.parent as HorizontalScrollView
        return if(horizontalScrollView.scrollX != 0){
            horizontalScrollView.smoothScrollTo(0, 0)
            false
        } else {
            onMessageClickListener.onLongClicked(p0.tag as Int)
            true
        }
    }

    override fun setOnBaseClickListener(onBaseClickListener: OnMessageClickListener) {
        this.onMessageClickListener = onBaseClickListener
    }

    class MessageViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val scrollView = itemView.findViewById<HorizontalScrollView>(R.id.message_item_scroll_view)!!
        val mIn = itemView.findViewById<LinearLayout>(R.id.message_item_in)!!
        val icon = itemView.findViewById<CircleImageView>(R.id.message_item_icon)!!
        val name = itemView.findViewById<TextView>(R.id.message_item_name)!!
        val time = itemView.findViewById<TextView>(R.id.message_item_time)!!
        val content = itemView.findViewById<TextView>(R.id.message_item_content)!!
        val badge = itemView.findViewById<View>(R.id.message_item_badge)!!
        val out = itemView.findViewById<LinearLayout>(R.id.message_item_out)!!
        val top = itemView.findViewById<TextView>(R.id.message_item_top)!!
        val del = itemView.findViewById<TextView>(R.id.message_item_delete)!!
    }

    interface OnMessageClickListener : OnBaseClickListener {
        fun onClicked(position: Int)
        fun onLongClicked(position: Int)
        fun onTopClicked(position: Int)
        fun onDeleteClicked(position: Int)
    }
}