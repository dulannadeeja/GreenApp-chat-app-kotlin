package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.chatapp.controllers.StatusUpdater
import com.example.chatapp.databinding.ActivityStatusViewBinding
import com.example.chatapp.databinding.ActivityUserProfileBinding
import com.example.chatapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var userID: String
    private lateinit var currentUser:UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        binding= ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database= FirebaseDatabase.getInstance()
        userID= intent.getStringExtra("userID").toString()
        Log.i("USERID","$userID")

        binding.imageViewBackIcon.setOnClickListener {
            onBackPressed()
        }

        binding.textMessage.setOnClickListener{
            val intent = Intent(this@UserProfileActivity, ChatActivity::class.java)
            intent.putExtra("userID", currentUser.userID)
            intent.putExtra("userName", currentUser.userName)
            intent.putExtra("userProfileUri", currentUser.profileUri)
            startActivity(intent)
        }

        binding.photoMessage.setOnClickListener{
            val intent = Intent(this@UserProfileActivity, ChatActivity::class.java)
            intent.putExtra("userID", currentUser.userID)
            intent.putExtra("userName", currentUser.userName)
            intent.putExtra("userProfileUri", currentUser.profileUri)
            startActivity(intent)
        }

        binding.voiceMessage.setOnClickListener{
            val intent = Intent(this@UserProfileActivity, ChatActivity::class.java)
            intent.putExtra("userID", currentUser.userID)
            intent.putExtra("userName", currentUser.userName)
            intent.putExtra("userProfileUri", currentUser.profileUri)
            startActivity(intent)
        }

        database.getReference("Users")
            .child(userID)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUser= snapshot.getValue(UserData::class.java)!!
                    updateUI()
                }

                private fun updateUI() {
                    if (!currentUser.profileUri.isNullOrEmpty()) {
                        Glide.with(applicationContext).load(currentUser.profileUri.toString())
                            .placeholder(R.drawable.default_avator)
                            .into(binding.imageViewProfilePicture)
                    }
                    binding.textViewUserName.text=currentUser.userName
                    binding.textViewPhone.text=currentUser.mobile
                    binding.textViewUserBio.text=currentUser.bio
                    binding.blockUser.text="block ${currentUser.userName}"
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


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