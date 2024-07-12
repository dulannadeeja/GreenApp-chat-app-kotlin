package com.example.chatapp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.adapters.UserAdapter
import com.example.chatapp.controllers.PermissionManager
import com.example.chatapp.databinding.FragmentChatsBinding
import com.example.chatapp.databinding.FragmentContactsBinding
import com.example.chatapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private lateinit var userList: ArrayList<UserData>
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private var CONTACT_PERMISSION_REQUEST_CODE=3
    private var contactNumberList: MutableList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //view binding
        binding = FragmentContactsBinding.inflate(inflater,container,false)
        return  binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userList = ArrayList<UserData>()
        userAdapter = UserAdapter(requireContext(), userList)
        recyclerView = binding.recyclerViewUsers


        if(PermissionManager(requireContext(),requireActivity()).checkContactPermission()){
            contactNumberList=getContacts()
            Log.i("MYContacts","$contactNumberList")
        }else{
            PermissionManager(requireContext(),requireActivity()).grantContactPermission(CONTACT_PERMISSION_REQUEST_CODE)
        }


        val greenAppusers = arrayListOf<UserData>()
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
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
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                userAdapter = UserAdapter(requireContext(), greenAppusers)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }

    private fun getContacts(): MutableList<String> {
        val contactList: MutableList<String> = ArrayList()
        val contacts = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (contacts!!.moveToNext()) {
            val number = contacts.getString(
                contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER).toInt()
            )
            val fixednumber=number.replace("\\s".toRegex(), "")
            contactList.add(fixednumber)
        }
        contacts.close()
        return contactList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //do this on request Contact permissions
        if (requestCode == CONTACT_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactNumberList=getContacts()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

}