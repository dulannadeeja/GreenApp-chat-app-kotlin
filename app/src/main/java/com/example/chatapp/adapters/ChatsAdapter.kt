package com.example.chatapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ChatRowLayoutBinding
import com.example.chatapp.model.ChatChannel
import com.example.chatapp.model.Message
import com.example.chatapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatsAdapter(private var context: Context, private var chatChannelList: List<ChatChannel>) :
    RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsAdapter.ChatViewHolder {
        val viewObj =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_row_layout, parent, false)
        return ChatViewHolder(viewObj)

    }
    private val currentUserID=FirebaseAuth.getInstance().currentUser!!.uid
    private val database:FirebaseDatabase= FirebaseDatabase.getInstance()
    private var lastMessage= Message()

    override fun onBindViewHolder(holder: ChatsAdapter.ChatViewHolder, position: Int) {
        val chatChannel = chatChannelList[position]
        val members=chatChannel.members
        var receiverID: String =""
        var lastMessageID=chatChannel.lastMessage
        var roomID=chatChannel.roomID
        if (members != null) {
            for(member in members){
                if (member!=currentUserID){
                    receiverID=member
                }
            }
        }

        database.reference.child("Users")
            .child(receiverID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val receiver = dataSnapshot.getValue(UserData::class.java)!!
                    holder.binding.textViewUserName.text=receiver.userName
                    if (!receiver.profileUri.isNullOrEmpty()) {
                        Glide.with(context).load(receiver.profileUri.toString())
                            .placeholder(R.drawable.default_avator)
                            .into(holder.binding.imageViewUserProfilePicture)
                    }

                    holder.itemView.setOnClickListener {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("userID", receiver.userID)
                        intent.putExtra("userName", receiver.userName)
                        intent.putExtra("userProfileUri", receiver.profileUri)
                        context.startActivity(intent)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })

        if (roomID != null) {
            database.reference.child("chats")
                .child(roomID)
                .child("messages")
                .child(lastMessageID!!)
                .addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        lastMessage= snapshot.getValue(Message::class.java)!!
                        holder.binding.textViewUserLastMessage.text = lastMessage?.messageContentText
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw error.toException()
                    }

                })
        }




    }

    override fun getItemCount(): Int {
        return chatChannelList.size
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ChatRowLayoutBinding = ChatRowLayoutBinding.bind(itemView)

    }
}