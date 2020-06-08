package com.afeka.picapp.services

import android.util.Log
import com.afeka.picapp.model.Photo
import com.afeka.picapp.fragments.PhotoFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class PhotoFragmentService(var photoFragment: PhotoFragment) {

    val photoListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            val photo = dataSnapshot.getValue<Photo>()
            photoFragment.photo = photo
            photoFragment.updateView()

        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w("error", "loadPost:onCancelled", databaseError.toException())
            // ...
        }
    }

    fun populatePhotoDetails(id: String) {
        var ref: DatabaseReference = Firebase.database.reference
        ref.child("images").child(id).addListenerForSingleValueEvent(photoListener)
    }

    fun newComment(id: String, author: String, comment: String) {
        Firebase.database.reference
            .child("images")
            .child(id)
            .child("comments")
            .child(author)
            .setValue(comment).addOnSuccessListener {
                populatePhotoDetails(id)
            }

    }
}