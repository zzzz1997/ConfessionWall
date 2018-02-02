package com.zzapp.confessionwall.entity

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobRelation
import com.zzapp.confessionwall.entity.Post
import com.zzapp.confessionwall.entity.User

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * 评论实体类
 *
 * @author zzzz
 */
class Comment : BmobObject() {
    //评论文本内容
    var content : String? = null
    //评论作者
    var author : User? = null
    //评论关联的动态
    var post : Post? = null
    //评论的点赞关系
    var likes : BmobRelation? = null
    //评论的点赞数
    var likesNum = 0
}
