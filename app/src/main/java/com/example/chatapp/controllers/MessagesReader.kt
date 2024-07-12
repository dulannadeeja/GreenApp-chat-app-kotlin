package com.example.chatapp.controllers

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ChatActivity
import com.example.chatapp.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessagesReader {
    private lateinit var  database:FirebaseDatabase
    private lateinit var messagesArrayList: ArrayList<Message>
    private lateinit var message: Message

    fun readMessages (context: Context,recyclerView:RecyclerView,senderRoom:String,receiverRoom:String){
        database= FirebaseDatabase.getInstance()
        messagesArrayList= ArrayList()
        database.getReference("chats")
            .child(senderRoom)
            .child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messagesArrayList.clear()
                    for (singleSnapshot in snapshot.children) {
                        message = singleSnapshot.getValue(Message::class.java)!!
                        message.messageID = singleSnapshot.key
                        messagesArrayList.add(message)
                    }

                    ChatActivity().updateUI(context, recyclerView,messagesArrayList,senderRoom,receiverRoom)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}