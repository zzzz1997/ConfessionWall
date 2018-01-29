package com.zzapp.confessionwall.utils

import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobRelation

/**
 * Project ConfessionWall
 * Date 2017-12-12
 *
 * 用户实体类
 *
 * @author zzzz
 */
class User : BmobUser() {
    //用户头像地址
    var icon: String? = null
    //用户关注关系
    var follow: BmobRelation? = null
    //用户关注人数
    var followNum = 0
    //用户收集的动态关系
    var collections : BmobRelation? = null
}