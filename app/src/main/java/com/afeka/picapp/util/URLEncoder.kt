package com.afeka.picapp.util

class URLEncoder {

    fun encode(name: String): String {
        return "https://firebasestorage.googleapis.com/v0/b/picapp-e01d0.appspot.com/o/${name.replace(" ", "%20")}?alt=media&token=bbe33095-ee81-4c1e-833d-ac71efa02dde"
    }

}