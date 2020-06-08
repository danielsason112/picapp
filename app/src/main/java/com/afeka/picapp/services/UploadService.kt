package com.afeka.picapp.services

import android.graphics.Bitmap
import com.afeka.picapp.model.Photo
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class UploadService {

    private val database = Firebase.database
    private val storage = Firebase.storage

    fun uploadPhotoToStorage(bitmap: Bitmap, photo: Photo) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        photo.name?.let {
            storage.reference.child(it).putBytes(stream.toByteArray()).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    photo.url = uri.toString()
                    createPhotoDetails(photo)
                }
            }
        }
    }

    private fun createPhotoDetails(photo: Photo) {
        database.reference
            .child("images")
            .child(HashService().hash(photo.url))
            .setValue(photo)
    }

}