package com.example.chatapp

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.example.chatapp.controllers.ImageSender
import com.example.chatapp.controllers.StatusUpdater
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.databinding.ActivityReviwePhotoBinding

class ReviewPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviwePhotoBinding
    private lateinit var receiverUserID:String
    private lateinit var photoUri: Uri
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviwe_photo)

        //set view binding
        binding = ActivityReviwePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        photoUri=intent.getStringExtra("pickedPhotoUri")!!.toUri()
        receiverUserID= intent.getStringExtra("receiverUserID")!!

        binding.zoomageViewPhoto.setImageURI(photoUri)

        binding.floatingActionButtonSend.setOnClickListener{
            ImageSender(receiverUserID!!,photoUri).sendImage()
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