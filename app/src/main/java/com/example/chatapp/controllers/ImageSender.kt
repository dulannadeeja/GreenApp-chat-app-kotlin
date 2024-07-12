package com.example.chatapp.controllers

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.chatapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ImageSender(
    private val ReceiverUserID: String,
    private val capturedPhotoUri: Uri
) {
    private lateinit var storage: FirebaseStorage
    private lateinit var senderRoom:String
    private lateinit var receiverRoom:String
    private lateinit var currentUserID: String
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendImage() {

        storage = FirebaseStorage.getInstance()
        currentUserID=Firebase.auth.currentUser!!.uid
        senderRoom=currentUserID+ReceiverUserID
        receiverRoom=ReceiverUserID+currentUserID

        val calender = Calendar.getInstance()
        val storageReference = storage.reference
            .child("chatContentImages")
            .child("${calender.timeInMillis}+$currentUserID+$ReceiverUserID")
        storageReference.putFile(capturedPhotoUri!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val messageType = "Image"
                    MessageSender().sendMessage(
                        messageType,
                        uri.toString(),
                        currentUserID,
                        ReceiverUserID,
                        senderRoom,
                        receiverRoom
                    )

                }
            }
        }

    }

}