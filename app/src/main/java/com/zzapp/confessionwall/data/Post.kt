package com.zzapp.confessionwall.data

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobRelation
import com.zzapp.confessionwall.utils.User
import java.io.Serializable

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * @author zzzz
 */
class Post : BmobObject(), Serializable{
    var content : String? = null
    var author : User? = null
    var collections : BmobRelation? = null
    var commentsNum = 0
    var likes : BmobRelation? = null
    var likesNum = 0
}