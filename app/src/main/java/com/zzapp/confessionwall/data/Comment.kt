package com.zzapp.confessionwall.data

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobRelation
import com.zzapp.confessionwall.utils.User

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * @author zzzz
 */
class Comment : BmobObject() {
    var content : String? = null
    var author : User? = null
    var post : Post? = null
    var likes : BmobRelation? = null
    var likesNum = 0
}