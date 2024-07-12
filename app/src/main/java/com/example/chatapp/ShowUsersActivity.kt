package com.example.chatapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapters.UserAdapter
import com.example.chatapp.controllers.ContactManager
import com.example.chatapp.controllers.PermissionManager
import com.example.chatapp.controllers.StatusUpdater
import com.example.chatapp.databinding.ActivityShowUsersBinding
import com.example.chatapp.model.Contact
import com.example.chatapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ShowUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowUsersBinding
    private lateinit var userList: ArrayList<UserData>
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private var contactCount: Int = 0
    private var CONTACT_PERMISSION_REQUEST_CODE=3
    private var contactNumberList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)

        binding = ActivityShowUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userList = ArrayList<UserData>()
        recyclerView = binding.recyclerViewUsers

        if(PermissionManager(this@ShowUsersActivity,this@ShowUsersActivity).checkContactPermission()){
            contactNumberList=ContactManager().getContacts(contentResolver)
            Log.i("MYContacts","$contactNumberList")
        }else{
            PermissionManager(this@ShowUsersActivity,this@ShowUsersActivity).grantContactPermission(CONTACT_PERMISSION_REQUEST_CODE)
        }

        val greenAppusers = arrayListOf<UserData>()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                contactCount = dataSnapshot.childrenCount.toInt()
                greenAppusers.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(UserData::class.java)
                    if (user != null) {
                        if (user.userID != auth.currentUser!!.uid) {
                            if(user.mobile in contactNumberList){
                                greenAppusers.add(user)
                            }

                        }
                    }
                }
                userAdapter = UserAdapter(this@ShowUsersActivity, greenAppusers)
                recyclerView.adapter = userAdapter
                recyclerView.layoutManager = LinearLayoutManager(this@ShowUsersActivity)
                binding.textViewContactsCount.text =
                    (contactCount - 1).toString() + " " + "contacts"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })


    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //do this on request Contact permissions
        if (requestCode == CONTACT_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactNumberList= ContactManager().getContacts(contentResolver)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onPause() {
        super.onPause()
        StatusUpdater().goOffline()
    }

    override fun onResume() {
        super.onResume()
        StatusUpdater().goOnline()
    }

    override fun onStart() {
        super.onStart()
        StatusUpdater().goOnline()
    }



}
