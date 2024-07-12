package com.example.chatapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.adapters.ChatsAdapter
import com.example.chatapp.controllers.ChatsReader
import com.example.chatapp.databinding.FragmentChatsBinding
import com.example.chatapp.model.ChatChannel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ChatsFragment : Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var currentUserID: String
    private lateinit var chatsList: ArrayList<ChatChannel>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressCircular.visibility = View.VISIBLE

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser!!.uid
        chatsList = ArrayList()
        recyclerView=binding.recyclerViewChats

        ChatsReader().getChatList(requireContext(),recyclerView)

        binding.progressCircular.visibility = View.GONE


    }

    fun updateUI(context: Context, chatsList: ArrayList<ChatChannel>, recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        val chatsAdapter = ChatsAdapter(context, chatsList)
        recyclerView.adapter = chatsAdapter
    }

}