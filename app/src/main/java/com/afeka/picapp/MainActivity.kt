package com.afeka.picapp

import OnSwipeTouchListener
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import com.afeka.picapp.fragments.MainFragment
import com.afeka.picapp.fragments.PhotoFragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), OnPhotoClickListener {
    private val RC_SIGN_IN: Int = 123
    private val PHOTO_FRAGMENT_TAG = "PhotoFragment"
    private var currentUserName: String = ""

    private var mainFragment: MainFragment = MainFragment()
    private lateinit var layout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)

        layout = findViewById(R.id.fragment_container)
        val context: Context = this
        layout.setOnTouchListener(object: OnSwipeTouchListener(context) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                onBackPressed()
            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    currentUserName = user.displayName.toString()
                    mainFragment = MainFragment.newInstance(currentUserName)
                }
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, mainFragment)
                    .commit()
            } else {
                // Sign in failed.
                if (response != null) {
                    Log.d("fb", "failed ${response.error?.message}")
                }
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment = this.supportFragmentManager.findFragmentByTag(PHOTO_FRAGMENT_TAG)
        if(currentFragment != null && currentFragment.isVisible)
        {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.fragment_container, mainFragment)
                .commit()
        } else {
            super.onBackPressed()
        }

    }

    override fun clicked(url: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PhotoFragment.newInstance(url, currentUserName), PHOTO_FRAGMENT_TAG)
            .replace(R.id.fragment_container, PhotoFragment.newInstance(url, currentUserName), PHOTO_FRAGMENT_TAG)
            .commit()
    }
}