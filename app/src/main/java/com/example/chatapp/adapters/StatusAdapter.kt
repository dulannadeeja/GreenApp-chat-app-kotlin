package com.example.chatapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.StatusViewActivity
import com.example.chatapp.databinding.StatusLayoutBinding
import com.example.chatapp.model.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StatusAdapter(private var context: Context, private var statusList: List<Status>) :
    RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatusAdapter.StatusViewHolder {
        val viewObj =
            LayoutInflater.from(parent.context).inflate(R.layout.status_layout, parent, false)
        return StatusViewHolder(viewObj)
    }

    private var userName: String? = null
    private val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {

        val status = statusList[position]
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(status.userID!!)
            .child("userName")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    userName = dataSnapshot.value.toString()
                    holder.binding.textViewUserName.text = userName
                    holder.binding.textViewUserBio.text = "1 min ago"
                    if (!status.imageUri.isNullOrEmpty()) {
                        Glide.with(context).load(status.imageUri.toString())
                            .placeholder(R.drawable.default_avator)
                            .into(holder.binding.imageViewLastStatusImage)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    throw databaseError.toException()
                }
            })

        holder.itemView.setOnClickListener {

            val intent = Intent(context, StatusViewActivity::class.java)
            intent.putExtra("userID", status.userID)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return statusList.size
    }

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: StatusLayoutBinding = StatusLayoutBinding.bind(itemView)

    }

}
