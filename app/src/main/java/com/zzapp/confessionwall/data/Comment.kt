package com.zzapp.confessionwall.data

import cn.bmob.v3.BmobObject
import com.zzapp.confessionwall.utils.User

/**
 * Project ConfessionWall
 * Date 2018-01-06
 *
 * @author zzzz
 */
class Comment : BmobObject() {
    var content : String? = null
    var user : User? = null
    var post : Post? = null
}