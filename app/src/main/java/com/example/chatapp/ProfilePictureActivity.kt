package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.chatapp.controllers.StatusUpdater
import com.example.chatapp.databinding.ActivityProfileBinding
import com.example.chatapp.databinding.ActivityProfilePictureBinding

class ProfilePictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePictureBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture)

        //set view bindings
        binding = ActivityProfilePictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileUri= intent.getStringExtra("profileUri")
        if (profileUri!=null) {
            Glide.with(this@ProfilePictureActivity).load(profileUri)
                .placeholder(R.drawable.placeholder)
                .into(binding.zoomageViewProfilePicture)
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