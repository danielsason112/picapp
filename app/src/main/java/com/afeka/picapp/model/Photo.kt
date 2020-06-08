package com.afeka.picapp.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Photo(var name: String? = null,
            var url: String? = null,
            var uploader: String? = null,
            var description: String? = null,
            var comments: HashMap<String,String>? = null,
            var location: String? = null,
            var uploaded: String? = null) {



}