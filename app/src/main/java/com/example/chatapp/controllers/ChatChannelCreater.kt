package com.example.chatapp.controllers

import android.util.Log
import com.example.chatapp.model.ChatChannel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatChannelCreater {
    fun createChatChannel(
        lastMessageID: String,
        currentUserID: String,
        selectedUserID: String,
        senderRoom: String,
        receiverRoom: String
    ) {
        val database = FirebaseDatabase.getInstance()
        val membersArray = java.util.ArrayList<String>()
        val encryptionKey = AESEncryptor().generateKey().toString()
        membersArray.add("$currentUserID")
        membersArray.add("$selectedUserID")

        database.getReference("encryption")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {

                            val firstMember = snap.child("firstMember").getValue(true)
                            val secondMember = snap.child("secondMember").getValue(true)


                            if (firstMember in membersArray && secondMember in membersArray) {
                                Log.i("members", "$firstMember+$secondMember")
                            } else {
                                database.getReference("encryption")
                                    .child(senderRoom)
                                    .child("key")
                                    .setValue("$encryptionKey")

                                database.getReference("encryption")
                                    .child(senderRoom)
                                    .child("firstMember")
                                    .setValue("${membersArray[0]}")

                                database.getReference("encryption")
                                    .child(senderRoom)
                                    .child("secondMember")
                                    .setValue("${membersArray[1]}")
                            }


                        }
                    } else {
                        database.getReference("encryption")
                            .child(senderRoom)
                            .child("key")
                            .setValue("$encryptionKey")

                        database.getReference("encryption")
                            .child(senderRoom)
                            .child("firstMember")
                            .setValue("${membersArray[0]}")

                        database.getReference("encryption")
                            .child(senderRoom)
                            .child("secondMember")
                            .setValue("${membersArray[1]}")
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        val chatChannelSender = ChatChannel(senderRoom, lastMessageID, membersArray)

        database.reference.child("chatChannels")
            .child(currentUserID)
            .child(senderRoom)
            .setValue(chatChannelSender)

        val chatChannelReceiver = ChatChannel(receiverRoom, lastMessageID, membersArray)

        database.reference.child("chatChannels")
            .child(selectedUserID)
            .child(receiverRoom)
            .setValue(chatChannelReceiver)

    }
}