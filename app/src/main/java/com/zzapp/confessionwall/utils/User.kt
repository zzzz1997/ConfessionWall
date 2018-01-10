package com.zzapp.confessionwall.utils

import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobRelation

/**
 * Project ConfessionWall
 * Date 2017-12-12
 *
 * @author zzzz
 */
class User : BmobUser() {
    var icon: String? = null
    var follow: BmobRelation? = null
    var followNum = 0
}