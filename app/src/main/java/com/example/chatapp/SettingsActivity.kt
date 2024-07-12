package com.example.chatapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.chatapp.controllers.StatusUpdater
import com.example.chatapp.databinding.ActivitySettingsBinding
import com.example.chatapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userInfo: UserData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //set view bindings
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing firebase instances
        auth= FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()

        val userID= auth.currentUser?.uid
        if(userID!=null){
            getUserInfo(userID)
        }

        userInfo= UserData()

        binding.imageViewBackIcon.setOnClickListener{
            onBackPressed()
        }
        binding.userInfoSection.setOnClickListener{
            val intent= Intent(this@SettingsActivity, ProfileActivity::class.java)
            startActivity(intent)

        }
    }

    private fun getUserInfo(userID: String) {
        database.reference.child("Users")
            .child(userID)
            .get()
            .addOnSuccessListener {
                userInfo = it.getValue(UserData::class.java)!!
                binding.textViewUserName.text=userInfo.userName
                binding.textViewUserBio.text=userInfo.bio
                if (!userInfo.profileUri.isNullOrEmpty()) {
                    Glide.with(this@SettingsActivity).load(userInfo.profileUri.toString())
                        .placeholder(R.drawable.default_avator)
                        .into(binding.imageViewUserProfilePicture)
                }
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