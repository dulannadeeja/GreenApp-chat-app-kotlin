package com.example.chatapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.StatusViewActivity
import com.example.chatapp.adapters.StatusAdapter
import com.example.chatapp.controllers.StatusReader
import com.example.chatapp.databinding.FragmentStatusBinding
import com.example.chatapp.model.Status
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.util.stream.Stream


class StatusFragment : Fragment() {
private lateinit var binding: FragmentStatusBinding
private lateinit var recyclerView:RecyclerView
private lateinit var imageView: ImageView
private lateinit var currentUserID:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //view binding
        binding = FragmentStatusBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView=binding.recyclerViewStatus
        imageView=binding.imageViewCurrentUserStatusImage
        currentUserID=FirebaseAuth.getInstance().currentUser!!.uid

        StatusReader().getStatusList(requireActivity().contentResolver,requireContext(), recyclerView, imageView as CircleImageView)

        binding.linearLayoutMyStatus.setOnClickListener{
            val intent = Intent(context, StatusViewActivity::class.java)
            intent.putExtra("userID", currentUserID)
            startActivity(intent)
        }

    }

    fun setCurrentUserStatus(context: Context, imageView: CircleImageView, status: Status) {
        if (!status.imageUri.isNullOrEmpty()) {
            Glide.with(context).load(status.imageUri.toString())
                .placeholder(R.drawable.default_avator)
                .into(imageView)
        }
    }

    fun updateUI(context: Context, statusList: ArrayList<Status>, recyclerView: RecyclerView) {

        recyclerView.layoutManager = LinearLayoutManager(context)
        val statusAdapter = StatusAdapter(context, statusList)
        recyclerView.adapter = statusAdapter
    }
}