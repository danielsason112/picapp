package com.afeka.picapp.fragments

import PhotosGridAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afeka.picapp.MainActivity
import com.afeka.picapp.R
import com.afeka.picapp.UploadActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.HashSet
import kotlin.concurrent.schedule


private const val ARG_USER_NAME = "userName"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private var userName: String = ""

    private val MAX_RESULT_RECENT = 20
    private val SEARCH_QUERY_HINT = "plane, nature"

    private val storage = Firebase.storage

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PhotosGridAdapter
    private lateinit var uploadButton: Button
    private var imageList: HashSet<String> = HashSet<String>()
    private var isQuerying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userName = it.getString(ARG_USER_NAME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =  inflater.inflate(R.layout.fragment_main, container, false)
        val sglm = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        searchView = view.findViewById(R.id.search_view_photos)
        uploadButton = view.findViewById(R.id.button_upload)
        uploadButton.setOnClickListener { v ->
            val myIntent = Intent(this.activity, UploadActivity::class.java)
            myIntent.putExtra("userName", userName)
            this.activity?.startActivity(myIntent)
        }
        searchView.queryHint = SEARCH_QUERY_HINT
        searchView.setOnQueryTextListener(OnPhotosQueryTextListener())

        viewAdapter = PhotosGridAdapter(this.context as Context, imageList.toList(), activity as MainActivity)

        recyclerView = view.findViewById(R.id.recycler_view_photos)
        recyclerView.layoutManager = sglm
        recyclerView.adapter = viewAdapter



            return view
    }

    override fun onResume() {
        super.onResume()
        Timer("query", false).schedule(1500) {
            showRecentPhotos()
        }
    }

    override fun onStart() {
        super.onStart()
        showRecentPhotos()
    }

    inner class OnPhotosQueryTextListener : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            // Sometimes the listener is invoked twice for unknown reason, handled with the following flag
            Timer("querying", false).schedule(500) {
                isQuerying = false
            }
            if (query != null && !isQuerying) {
                isQuerying = true
                if (query.isNotEmpty()) {
                    showSearchResult(query.toString())
                }
            }
            return false;
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }

    }

    private fun showRecentPhotos() {
        var storageRef = storage.reference

        storageRef.list(MAX_RESULT_RECENT).addOnSuccessListener { listResult ->
            listResult.items.iterator().forEach { storageReference ->
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    addPhotoToGrid(uri.toString())
                }
            }
        }
    }

    private fun addPhotoToGrid(url: String) {
        imageList.add(url)
        recyclerView.swapAdapter(PhotosGridAdapter(this.context as Context,
                        imageList.toList(), activity as MainActivity
        ), false)
    }

    fun showSearchResult(pattern: String) {
        Log.d("SearchQuery", pattern)
        var storageRef = storage.reference
        imageList.clear()

        storageRef.list(MAX_RESULT_RECENT).addOnSuccessListener { listResult ->
            val filtered: List<StorageReference> = listResult.items.filter { it.name.contains(pattern) }
            if (filtered.isEmpty()) {
                Toast.makeText(this.context, "No result found", Toast.LENGTH_LONG).show()
            } else {
                filtered.iterator().forEach { storageReference ->
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        addPhotoToGrid(uri.toString())
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param userName Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        @JvmStatic
        fun newInstance(userName: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_NAME, userName)
                }
            }
    }
}