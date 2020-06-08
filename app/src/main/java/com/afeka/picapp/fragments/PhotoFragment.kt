package com.afeka.picapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afeka.picapp.adapters.CommentsAdapter
import com.afeka.picapp.R
import com.afeka.picapp.model.Photo
import com.afeka.picapp.services.HashService
import com.afeka.picapp.services.PhotoFragmentService
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timerTask

private const val ARG_URL = "url"
private const val ARG_USER_NAME = "userName"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoFragment : Fragment() {
    private var url: String? = null
    private var userName: String? = null
    var photo: Photo? = null
    private var isFirstClicked: Boolean = false
    private var timer: Timer = Timer("doubleClick", false)
    private lateinit var imageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var uploadedTextView: TextView
    private lateinit var uploaderTextView: TextView
    private lateinit var commentButton: Button
    private lateinit var commentEditText: EditText
    private lateinit var commentsRv: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            url = it.getString(ARG_URL)
            userName = it.getString(ARG_USER_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_photo, container, false)

        imageView = view.findViewById(R.id.imageView)
        nameTextView = view.findViewById(R.id.photo_name)
        descriptionTextView = view.findViewById(R.id.photo_description)
        addressTextView = view.findViewById(R.id.photo_location)
        uploadedTextView = view.findViewById(R.id.photo_uploaded)
        uploaderTextView = view.findViewById(R.id.photo_uploader)
        commentButton = view.findViewById(R.id.commentButton)
        commentEditText = view.findViewById(R.id.editTextTextComment)
        commentButton.setOnClickListener { v ->
            if (isFirstClicked) {
                timer.cancel()
                timer.purge()
                commentButton.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.shake))
                isFirstClicked = false
            } else {
                isFirstClicked = true
                timer = Timer("doubleClick", false)
                    timer.schedule(timerTask {
                    commentOnPhoto(url, commentEditText.text.toString())
                    isFirstClicked = false
                }, 500)


            }

        }

        viewManager = LinearLayoutManager(this.context)
        viewAdapter = CommentsAdapter(HashMap())
        commentsRv = view.findViewById<RecyclerView>(R.id.comments_rv).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        Picasso.get()
            .load(url)
            .fit()
            .into(imageView)

        PhotoFragmentService(this).populatePhotoDetails(
            HashService().hash(url))

        return view
    }

    private fun commentOnPhoto(url: String?, comment: String?) {
       userName?.let {
            if (comment != null) {
                PhotoFragmentService(this).newComment(
                    HashService().hash(url),
                    it, comment)
            }
        }
    }

    fun updateView() {
        if (photo != null) {
            nameTextView.text = photo!!.name
            descriptionTextView.text = photo!!.description
            addressTextView.text = photo!!.location
            uploadedTextView.text = photo!!.uploaded
            uploaderTextView.text = "uploaded by ${photo!!.uploader}"

            if (photo!!.comments != null) {
                commentsRv.swapAdapter(photo!!.comments?.let {
                    CommentsAdapter(
                        it
                    )
                }, false)
            }

        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param url Photo url.
         * @param userName Current user name.
         * @return A new instance of fragment PhotoFragment.
         */
        @JvmStatic
        fun newInstance(url: String, userName: String) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URL, url)
                    putString(ARG_USER_NAME, userName)
                }
            }
    }
}