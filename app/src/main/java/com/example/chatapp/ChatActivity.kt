package com.example.chatapp


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.adapters.MessagesAdapter
import com.example.chatapp.controllers.*
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {
    private lateinit var currentUserID: String
    private lateinit var selectedUserID: String
    private lateinit var selectedUserName: String
    private lateinit var selectedUserProPicUri: String
    private lateinit var userNameTextView: TextView
    private lateinit var userStatusTextView: TextView
    private lateinit var userProPicImageView: ImageView
    private lateinit var chatBoxEditText: EditText
    private lateinit var cameraButton: ImageButton
    private lateinit var attachmentButton: ImageButton
    private lateinit var voiceButton:ImageButton
    private lateinit var sendButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityChatBinding
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var messagesArrayList: ArrayList<Message>
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var date: Date
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialogBuilder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog
    private lateinit var messageType: String
    private lateinit var currentPhotoPath:String
    private val GALLERY_PERMISSION_REQUEST_CODE = 1
    private val MEDIA_REQUEST_CODE=1
    private val CAMERA_PERMISSION_REQUEST_CODE = 2
    private val CAPTURE_REQUEST_CODE = 2
    private var capturedPhotoUri: Uri? = null
    private var isVisiblePopupMenu:Boolean=false


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //set view binding
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        //add alert dialog
        progressDialogBuilder = AlertDialog.Builder(this)
        progressDialogBuilder.setView(R.layout.uploading_progress_layout)
        progressDialog = progressDialogBuilder.create()

        //initializing firebase instances
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        userNameTextView = binding.textViewUserName
        userStatusTextView = binding.textViewStatus
        userProPicImageView = binding.imageViewUserProfilePicture
        chatBoxEditText = binding.EditTextMessageBox
        cameraButton = binding.buttonCamera
        attachmentButton = binding.buttonAttachment
        voiceButton=binding.buttonVoice
        sendButton = binding.buttonSend
        backButton = binding.buttonBack
        recyclerView = binding.recyclerViewChatHistory


        binding.textViewStatus.visibility = View.VISIBLE

        selectedUserID = intent.getStringExtra("userID").toString()
        selectedUserName = intent.getStringExtra("userName").toString()
        selectedUserProPicUri = intent.getStringExtra("userProfileUri").toString()
        currentUserID = auth.currentUser!!.uid
        senderRoom=currentUserID+selectedUserID
        receiverRoom=selectedUserID+currentUserID
        date = Date()
        messagesArrayList = ArrayList<Message>()

        userNameTextView.text = selectedUserName
        if (!selectedUserProPicUri.isEmpty()) {
            Glide.with(this).load(selectedUserProPicUri)
                .placeholder(R.drawable.default_avator)
                .into(userProPicImageView)
        }

        binding.imageViewUserProfilePicture.setOnClickListener{
            val intent=Intent(this@ChatActivity,UserProfileActivity::class.java)
            intent.putExtra("userID","$selectedUserID")
            startActivity(intent)
        }



        //Send Button Listener
        sendButton.setOnClickListener {
            playClickSound()
            val messageContentText = chatBoxEditText.text.toString().trim()
            if (!messageContentText.isNullOrEmpty()) {
                messageType="Text"
                MessageSender().sendMessage(messageType,messageContentText,currentUserID,selectedUserID,senderRoom,receiverRoom)
                chatBoxEditText.text.clear()
            }
        }

        //back button listener
        backButton.setOnClickListener {
            finish()
        }

        //attachment button listener
        attachmentButton.setOnClickListener {

            when(isVisiblePopupMenu){
                false->{
                    binding.popUpMenuLinearLayout.visibility=View.VISIBLE
                    isVisiblePopupMenu=true
                }
                true->{
                    binding.popUpMenuLinearLayout.visibility=View.GONE
                    isVisiblePopupMenu=false
                }
            }

            binding.imageViewAudioButton.setOnClickListener{

            }
            binding.imageViewCameraButton.setOnClickListener{

                val isGranted=PermissionManager(this@ChatActivity,this@ChatActivity).checkCameraPermission()
                if(isGranted){
                    capturePhoto()
                }else{
                    PermissionManager(this@ChatActivity,this@ChatActivity).grantCameraPermission(CAMERA_PERMISSION_REQUEST_CODE)
                }
            }
            binding.imageViewLocationButton.setOnClickListener{}
            binding.imageViewContactButton.setOnClickListener{}
            binding.imageViewGalleryButton.setOnClickListener{
                val isGranted=PermissionManager(this@ChatActivity,this@ChatActivity).checkGalleryPermission()
                if(isGranted){
                    val galleyIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleyIntent, 2)
                }else{
                    PermissionManager(this@ChatActivity,this@ChatActivity).grantGalleryPermission(GALLERY_PERMISSION_REQUEST_CODE)
                }
            }
            binding.imageViewDocumentButton.setOnClickListener{}

        }

        //camera button listener
        cameraButton.setOnClickListener {
            val isGranted=PermissionManager(this@ChatActivity,this@ChatActivity).checkCameraPermission()
            if(isGranted){
                capturePhoto()
            }else{
                PermissionManager(this@ChatActivity,this@ChatActivity).grantCameraPermission(CAMERA_PERMISSION_REQUEST_CODE)
            }
        }

        chatBoxEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            val handler = Handler()
            override fun afterTextChanged(p0: Editable?) {
                database.reference.child("Presence")
                    .child(currentUserID)
                    .setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping, 1000)
            }

            var userStoppedTyping = Runnable {
                database.reference.child("Presence")
                    .child(currentUserID)
                    .setValue("Online")
            }
        })

        database.reference.child("Presence").child(selectedUserID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val handler = Handler()
                    handler.postDelayed({
                        // do something after 1000ms
                        if (snapshot.exists()) {
                            val status = snapshot.getValue(String::class.java)
                            if (status == "offline") {
                                binding.textViewStatus.visibility = View.GONE
                            } else {
                                binding.textViewStatus.visibility = View.VISIBLE
                                binding.textViewStatus.text = status
                            }
                        } else {
                            binding.textViewStatus.visibility = View.GONE
                        }
                    }, 1000)

                }

                override fun onCancelled(error: DatabaseError) {}
            })

        MessagesReader().readMessages(this@ChatActivity,recyclerView,senderRoom,receiverRoom)

    }

    fun updateUI(context: Context,recyclerView: RecyclerView,messagesArrayList: ArrayList<Message>,senderRoom:String,receiverRoom:String) {
        //set recycler view
        val messagesAdapter = MessagesAdapter(context,messagesArrayList, senderRoom, receiverRoom)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = messagesAdapter
        recyclerView.scrollToPosition(messagesAdapter.itemCount - 1)
    }

    @Throws(IOException::class)
    private fun createTempImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun capturePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createTempImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                        Toast.makeText(this@ChatActivity,"Error occurred while creating the File",Toast.LENGTH_LONG).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.chatapp",
                        it
                    )
                    capturedPhotoUri=photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAPTURE_REQUEST_CODE)
                }
            }
        }
    }

    private fun playClickSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.click_sound)
        mediaPlayer.start()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        //do this on request media permissions
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleyIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleyIntent,MEDIA_REQUEST_CODE)
            }
        }

        //do this on request camera permissions
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAPTURE_REQUEST_CODE)

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //do this on capture requests
        if (requestCode == CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if(capturedPhotoUri!=null) {
                ImageSender(selectedUserID,capturedPhotoUri!!).sendImage()
            }
        }


        //do this on Media requests
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val pickedPhoto = data.data!!
            val intent=Intent(this@ChatActivity,ReviewPhotoActivity::class.java)
            intent.putExtra("pickedPhotoUri","$pickedPhoto")
            intent.putExtra("receiverUserID","$selectedUserID")
            startActivity(intent)

        }

        super.onActivityResult(requestCode, resultCode, data)
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

