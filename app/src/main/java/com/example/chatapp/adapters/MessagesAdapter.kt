package com.example.chatapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.DeleteLayoutBinding
import com.example.chatapp.databinding.ReceiveLayoutBinding
import com.example.chatapp.databinding.SendLayoutBinding
import com.example.chatapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MessagesAdapter(
    private var context: Context,
    private var messagesList: ArrayList<Message>,
    private var senderRoom: String,
    private var receiverRoom: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    override fun getItemViewType(position: Int): Int {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return if (messagesList[position].senderID == auth.currentUser!!.uid) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 0) {
            val viewObj =
                LayoutInflater.from(parent.context).inflate(R.layout.send_layout, parent, false)
            SendViewHolder(viewObj)
        } else {
            val viewObj =
                LayoutInflater.from(parent.context).inflate(R.layout.receive_layout, parent, false)
            ReceiveViewHolder(viewObj)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messagesList[position]
        if(holder.javaClass==SendViewHolder::class.java){
            val viewHolder = holder as SendViewHolder
            if(!message.messageContentText.isNullOrEmpty()) {
                viewHolder.binding.textViewMessageContent.text = message.messageContentText
            }else{
                viewHolder.binding.textViewMessageContent.visibility=View.GONE
                viewHolder.binding.background.visibility=View.GONE
            }
            if(!message.messageContentImageUri.isNullOrEmpty()) {
                viewHolder.binding.imageViewAttachedImage.visibility=View.VISIBLE
                Glide.with(context).load(message.messageContentImageUri)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.imageViewAttachedImage)
                viewHolder.binding.textViewMessageContent.visibility=View.GONE
            }else{
                viewHolder.binding.imageViewAttachedImage.visibility=View.GONE
            }
            viewHolder.itemView.setOnLongClickListener {
                val myview=LayoutInflater.from(context).inflate(R.layout.delete_layout,null)
                var mybinding: DeleteLayoutBinding = DeleteLayoutBinding.bind(myview)

                //add alert dialog
                val progressDialogBuilder= AlertDialog.Builder(context)
                progressDialogBuilder.setView(mybinding.root)
                val progressDialog=progressDialogBuilder.create()
                progressDialog.show()

                mybinding.textViewDeleteForEveryone.setOnClickListener{

                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.messageID!!)
                        .setValue(null)
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(message.messageID!!)
                        .child("messageContentText")
                        .setValue("This Text Is Deleted!")
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(receiverRoom)
                        .child("messages")
                        .child(message.messageID!!)
                        .child("messageContentImageUri")
                        .setValue(null)
                    progressDialog.dismiss()
                }
                mybinding.textViewDeleteForMe.setOnClickListener{
                    FirebaseDatabase.getInstance().getReference("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.messageID!!)
                        .setValue(null)
                    progressDialog.dismiss()
                }
                mybinding.textViewCancel.setOnClickListener{
                    progressDialog.dismiss()
                }




                false
            }
        }else{
            val viewHolder = holder as ReceiveViewHolder
            if(!message.messageContentText.isNullOrEmpty()) {
                viewHolder.binding.textViewMessageContent.text = message.messageContentText
            }else{
                viewHolder.binding.textViewMessageContent.visibility=View.GONE
                viewHolder.binding.background.visibility=View.GONE
            }
            if(!message.messageContentImageUri.isNullOrEmpty()) {
                viewHolder.binding.imageViewAttachedImage.visibility=View.VISIBLE
                Glide.with(context).load(message.messageContentImageUri)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.imageViewAttachedImage)
            }else{
                viewHolder.binding.imageViewAttachedImage.visibility=View.GONE
            }
            viewHolder.itemView.setOnLongClickListener {
                val myview=LayoutInflater.from(context).inflate(R.layout.delete_layout,null)
                var mybinding: DeleteLayoutBinding = DeleteLayoutBinding.bind(myview)

                //add alert dialog
                val progressDialogBuilder= AlertDialog.Builder(context)
                progressDialogBuilder.setView(mybinding.root)
                val progressDialog=progressDialogBuilder.create()
                progressDialog.show()

                mybinding.textViewDeleteForEveryone.visibility=View.INVISIBLE
                mybinding.textViewDeleteForMe.setOnClickListener{
                    FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.messageID!!)
                        .setValue(null)
                    progressDialog.dismiss()
                }
                mybinding.textViewCancel.setOnClickListener{
                    progressDialog.dismiss()
                }


                false
            }
        }


    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    inner class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SendLayoutBinding = SendLayoutBinding.bind(itemView)

    }
    inner class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: ReceiveLayoutBinding = ReceiveLayoutBinding.bind(itemView)

    }

}