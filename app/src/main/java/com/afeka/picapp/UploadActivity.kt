package com.afeka.picapp

import android.content.Intent
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afeka.picapp.fragments.MapsFragment
import com.afeka.picapp.model.Photo
import com.afeka.picapp.services.UploadService
import com.afeka.picapp.util.URLEncoder
import com.google.android.gms.maps.model.LatLng
import java.util.*


class UploadActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var imageView: ImageView
    private lateinit var geocoder: Geocoder
    private lateinit var openCameraButton: Button
    private lateinit var uploadButton: Button
    private lateinit var mapFragment: MapsFragment
    private lateinit var nameInput: EditText
    private lateinit var descriptionInput: EditText

    private var locationManager : LocationManager? = null
    private var photoBitmap: Bitmap? = null
    private var mLocation: String = "No location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        geocoder = Geocoder(this, Locale.getDefault())
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MapsFragment
        nameInput = findViewById(R.id.editTextPhotoName)
        descriptionInput = findViewById(R.id.editTextPhotoDescription)
        openCameraButton = findViewById(R.id.button_open_camera)
        openCameraButton.setOnClickListener { v ->
            dispatchTakePictureIntent()
        }
        uploadButton = findViewById(R.id.upload_button)
        uploadButton.setOnClickListener { v ->
            val name: String = nameInput.text.toString()
            val description: String = descriptionInput.text.toString()
            if(photoBitmap == null || name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please take a picture, and fill all the detail", Toast.LENGTH_LONG).show()
            } else {
                val uploader: String = intent.getStringExtra("userName")
                UploadService()
                    .uploadPhotoToStorage(photoBitmap!!,
                        Photo(
                            name, URLEncoder()
                                .encode(name), uploader, description, null, mLocation, Date().toString()
                        )
                    )
                Toast.makeText(this, "Uploaded Successfuly", Toast.LENGTH_LONG).show()
                this.finish()
            }
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        try {
            // Request location updates
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch(ex: SecurityException) {
            Log.d("myTag", "Security Exception, no location available")
        }

        imageView = findViewById(R.id.image_view_upload)


    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            mLocation = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .get(0).locality + ", " + geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .get(0).adminArea + ", " + geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .get(0).countryName
            mapFragment.updateLocation(LatLng(location.latitude, location.longitude))
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            photoBitmap = imageBitmap
            imageView.setImageBitmap(imageBitmap)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
}