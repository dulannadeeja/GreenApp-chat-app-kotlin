package com.example.chatapp.controllers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.chatapp.model.Message
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageSender {
    private lateinit var messageObj: Message
    private lateinit var randomKey:String

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage(
        messageType: String,
        messageContent: String,
        currentUserID: String,
        selectedUserID: String,
        senderRoom: String,
        receiverRoom: String
    ) {


        var database = FirebaseDatabase.getInstance()
        randomKey = database.reference.push().key.toString()
        val dateTime = LocalDateTime.now()
        val dateTimeFormatted = dateTime.format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))


        when (messageType) {
            "Text" -> {
                messageObj = Message(
                    randomKey,
                    null,
                    dateTimeFormatted,
                    messageType,
                    messageContent,
                    null,
                    null,
                    null,
                    currentUserID,
                    selectedUserID
                )
            }
            "Image" -> {
                messageObj = Message(
                    randomKey,
                    null,
                    dateTimeFormatted,
                    messageType,
                    null,
                    messageContent,
                    null,
                    null,
                    currentUserID,
                    selectedUserID
                )
            }
            "Voice" -> {
                messageObj = Message(
                    randomKey,
                    null,
                    dateTimeFormatted,
                    messageType,
                    null,
                    null,
                    messageContent,
                    null,
                    currentUserID,
                    selectedUserID
                )
            }
            "Attachment" -> {
                messageObj = Message(
                    randomKey,
                    null,
                    dateTimeFormatted,
                    messageType,
                    null,
                    null,
                    null,
                    messageContent,
                    currentUserID,
                    selectedUserID
                )
            }
        }
        ChatChannelCreater().createChatChannel(
            randomKey!!,
            currentUserID,
            selectedUserID,
            senderRoom,
            receiverRoom
        )
        database.reference.child("chats")
            .child(senderRoom)
            .child("messages")
            .child(randomKey!!)
            .setValue(messageObj).addOnSuccessListener {
                database.reference.child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(randomKey)
                    .setValue(messageObj)
            }

    }



}