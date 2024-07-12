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
import com.example.chatapp.UserProfileActivity
import com.example.chatapp.databinding.UserLayoutBinding
import com.example.chatapp.model.UserData



class UserAdapter(private var context: Context, private var userList: List<UserData>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.UserViewHolder {
        val viewObj =
            LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(viewObj)
    }

    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {
        val user = userList[position]
            holder.binding.textViewUserName.text = user.userName
            holder.binding.textViewUserBio.text = user.bio
            if (!user.profileUri.isNullOrEmpty()) {
                Glide.with(context).load(user.profileUri.toString())
                    .placeholder(R.drawable.default_avator)
                    .into(holder.binding.imageViewUserProfilePicture)
                Log.i("TAG", "${user.profileUri}")
            }
        holder.itemView.setOnClickListener {
            val intent=Intent(context, UserProfileActivity::class.java)
            intent.putExtra("userID","${user.userID}")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: UserLayoutBinding = UserLayoutBinding.bind(itemView)

    }
}