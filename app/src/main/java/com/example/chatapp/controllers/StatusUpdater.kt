package com.example.chatapp.controllers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class StatusUpdater {
    private lateinit var database: FirebaseDatabase
    private lateinit var auth:FirebaseAuth

    fun goOffline(){
        database= FirebaseDatabase.getInstance()
        auth= FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser!!.uid
        database.reference.child("Presence")
            .child(currentUserID)
            .setValue("Offline")
    }
    fun goOnline(){
        database= FirebaseDatabase.getInstance()
        auth= FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser!!.uid
        database.reference.child("Presence")
            .child(currentUserID)
            .setValue("Online")
    }
    fun showtyping(){
        database= FirebaseDatabase.getInstance()
        auth= FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser!!.uid
        database.reference.child("Presence")
            .child(currentUserID)
            .setValue("Typing")
    }
}