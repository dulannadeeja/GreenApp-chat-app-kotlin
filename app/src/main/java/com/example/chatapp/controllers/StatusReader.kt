package com.example.chatapp.controllers

import android.content.ContentResolver
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapters.UserAdapter
import com.example.chatapp.fragments.StatusFragment
import com.example.chatapp.model.Status
import com.example.chatapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class StatusReader {

    private val database = FirebaseDatabase.getInstance()
    private val statusList = ArrayList<Status>()
    private var currentUserStatus=Status()
    private val currentUserID = Firebase.auth.currentUser!!.uid

    fun getStatusList(contentResolver: ContentResolver,context: Context, recyclerView: RecyclerView, imageView: CircleImageView) {



        val contactNumberList=ContactManager().getContacts(contentResolver)

        val greenAppUsers = arrayListOf<String>()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                greenAppUsers.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(UserData::class.java)

                    if (user != null) {
                            if(user.mobile in contactNumberList){
                                greenAppUsers.add(user.userID!!)
                            }
                    }
                }

                database.reference.child("status")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(statusSnapshot: DataSnapshot) {
                            for (snap in statusSnapshot.children){
                                val count=snap.childrenCount.toInt()
                                val userID=snap.key.toString()
                                if(userID!=currentUserID) {

                                    if(userID in greenAppUsers){
                                        getLastStatus(userID, count - 1)
                                    }
                                }else{
                                    getCurrentUserStatus(userID,count-1)
                                }
                            }

                        }

                        private fun getCurrentUserStatus(userID: String, i: Int) {
                            database.reference.child("status")
                                .child(userID)
                                .child(i.toString())
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(statusSnapshot: DataSnapshot) {
                                        val lastStatus=statusSnapshot.getValue(Status::class.java)
                                        if (lastStatus != null) {
                                            currentUserStatus=lastStatus
                                        }
                                        StatusFragment().setCurrentUserStatus(context,imageView,currentUserStatus)
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                })
                        }

                        private fun getLastStatus(userID: String, i: Int) {

                            database.reference.child("status")
                                .child(userID)
                                .child(i.toString())
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(statusSnapshot: DataSnapshot) {
                                        statusList.clear()
                                        val lastStatus=statusSnapshot.getValue(Status::class.java)
                                        if (lastStatus != null) {
                                            statusList.add(lastStatus)
                                            Log.i("COUNT","$statusList")
                                        }
                                        StatusFragment().updateUI(context,statusList,recyclerView)
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                })
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })






    }

}