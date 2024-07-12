package com.example.chatapp.controllers

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.chatapp.ChatActivity
import com.example.chatapp.model.Message
import com.example.chatapp.model.Status
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class StatusSender(
    private val currentUserID: String,
    private val capturedPhotoUri: Uri
) {
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase
    private var currentStatusNumber:Int = 0
    private lateinit var status:Status

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendImage() {

        storage = FirebaseStorage.getInstance()
        database= FirebaseDatabase.getInstance()
        val dateTime = LocalDateTime.now()
        val dateTimeFormatted = dateTime.format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))

        getStatusNumber()

        val calender = Calendar.getInstance()
        val storageReference = storage.reference
            .child("statusImages")
            .child("${calender.timeInMillis}+$currentUserID")
        storageReference.putFile(capturedPhotoUri).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    status= Status(currentUserID,uri.toString(),dateTimeFormatted)
                    database.reference.child("status")
                        .child(currentUserID)
                        .child(currentStatusNumber.toString())
                        .setValue(status)

                }
            }
        }

    }

    private fun getStatusNumber() {

        database.getReference("status")
            .child(currentUserID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentStatusNumber=snapshot.childrenCount.toInt()
                }

                override fun onCancelled(error: DatabaseError) {
                    currentStatusNumber=0
                }

            })
    }

}