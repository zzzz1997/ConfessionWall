package com.zzapp.confessionwall.data

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobRelation
import com.zzapp.confessionwall.utils.User
import java.io.Serializable

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * 动态实体类
 *
 * @author zzzz
 */
class Post : BmobObject(), Serializable{
    //动态的内容
    var content : String? = null
    //动态的作者
    var author : User? = null
    //动态的评论数
    var commentsNum = 0
    //动态的点赞关系
    var likes : BmobRelation? = null
    //动态的点赞数
    var likesNum = 0
}