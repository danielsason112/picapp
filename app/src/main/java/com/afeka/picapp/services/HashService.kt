package com.afeka.picapp.services

import java.security.MessageDigest

class HashService {

    fun hash(input: String?): String {
        if (input != null) {
            return MessageDigest
                .getInstance("MD5")
                .digest(input.toByteArray())
                .fold("", { str, it -> str + "%02x".format(it) })
        }
        return ""
    }
}