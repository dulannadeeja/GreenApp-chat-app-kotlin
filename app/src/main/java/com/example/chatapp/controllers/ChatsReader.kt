package com.example.chatapp.controllers

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.fragments.ChatsFragment
import com.example.chatapp.model.ChatChannel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class ChatsReader {

    private val database = FirebaseDatabase.getInstance()
    private val chatsList = ArrayList<ChatChannel>()
    private val currentUserID = Firebase.auth.currentUser!!.uid

    fun getChatList(context: Context, recyclerView: RecyclerView) {


            database.reference.child("chatChannels")
                .child(currentUserID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatsList.clear()
                        for (channelSnap in snapshot.children) {
                            val chatChannel = channelSnap.getValue(ChatChannel::class.java)
                            if (chatChannel != null) {
                                chatsList.add(chatChannel)
                            }
                        }
                        ChatsFragment().updateUI(context,chatsList,recyclerView)

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


    }



}
