package com.example.chatapp

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.chatapp.controllers.ImageSender
import com.example.chatapp.controllers.StatusSender
import com.example.chatapp.controllers.StatusUpdater
import com.example.chatapp.databinding.ActivityReviewStatusBinding
import com.example.chatapp.databinding.ActivityReviwePhotoBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ReviewStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewStatusBinding
    private lateinit var currentUserID: String
    private lateinit var photoUri: Uri
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_status)

        //set view binding
        binding = ActivityReviewStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        photoUri = intent.getStringExtra("pickedPhotoUri")!!.toUri()
        currentUserID = Firebase.auth.currentUser!!.uid

        binding.zoomageViewPhoto.setImageURI(photoUri)

        binding.floatingActionButtonSend.setOnClickListener {
            StatusSender(currentUserID,photoUri).sendImage()
            finish()
        }

        binding.imageViewBackIcon.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        StatusUpdater().goOffline()
    }

    override fun onResume() {
        super.onResume()
        StatusUpdater().goOnline()
    }

    override fun onStart() {
        super.onStart()
        StatusUpdater().goOnline()
    }
}

