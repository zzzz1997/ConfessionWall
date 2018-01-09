package com.zzapp.confessionwall.utils

import java.security.MessageDigest

/**
 * Project ConfessionWall
 * Date 2017-12-11
 *
 * @author zzzz
 */
object MD5 {

    fun md5(input: String): String{
        if(input.isEmpty()){
            return ""
        }
        return try{
            val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")
            val inputByteArray = input.toByteArray(charset("utf-8"))
            messageDigest.update(inputByteArray)
            val resultByteArray = messageDigest.digest()
            byteArrayToHex(resultByteArray).toUpperCase()
        }
        catch (e: Exception){
            ""
        }
    }

    private fun byteArrayToHex(byteArray: ByteArray): String {
        val sb = StringBuffer()
        for (b in byteArray) {
            val i :Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }

        return sb.toString()
    }
}