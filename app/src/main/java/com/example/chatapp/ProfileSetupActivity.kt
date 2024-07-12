package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatapp.databinding.ActivityProfileSetupBinding
import com.example.chatapp.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private lateinit var profilePictureImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var saveProfileButton: Button
    private lateinit var progressDialogBuilder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private var pickedPhoto: Uri? = null
    private var pickedBitmap: Bitmap? = null
    private var imageUrl: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        //set view binding
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profilePictureImageView = binding.imageViewProfilePicture
        nameEditText = binding.editTextName
        bioEditText = binding.editTextBio
        saveProfileButton = binding.buttonSaveProfile

        //initializing firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        //add alert dialog
        progressDialogBuilder = AlertDialog.Builder(this)
        progressDialogBuilder.setView(R.layout.update_profile_progress_layout)
        progressDialog = progressDialogBuilder.create()

        //profile picture imageView click event
        profilePictureImageView.setOnClickListener {
            pickPhoto()
        }

        //save profile Button click event
        saveProfileButton.setOnClickListener {
            saveProfileInfo()
        }

    }

    private fun saveProfileInfo() {

        val userName: String? = nameEditText.text.toString().trim()
        val userBio: String? = bioEditText.text.toString().trim()
        val userMobile = auth.currentUser!!.phoneNumber
        val userID = auth.currentUser!!.uid

        //If User Not Provide a name show an error
        if (userName.isNullOrEmpty()) {
            nameEditText.setError("Please Enter Your Name")
        } else {
            //start showing progress window
            progressDialog.show()

            if (pickedPhoto != null) {
                val storageReference =
                    storage.reference.child("Profile").child(auth.currentUser!!.uid)
                storageReference.putFile(pickedPhoto!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                            val userObj =
                                UserData(userID, userMobile!!, userName, userBio, imageUrl)
                            database.reference.child("Users").child(userID).setValue(userObj)
                            progressDialog.dismiss()
                            startActivity(
                                Intent(
                                    this@ProfileSetupActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }
                    } else {
                        imageUrl = ""
                        val userObj = UserData(userID, userMobile!!, userName, userBio, imageUrl)
                        database.reference.child("Users").child(userID).setValue(userObj)
                        progressDialog.dismiss()
                        startActivity(Intent(this@ProfileSetupActivity, MainActivity::class.java))
                        finish()
                    }
                }
            } else {
                imageUrl = ""
                val userObj = UserData(userID, userMobile!!, userName, userBio, imageUrl)
                database.reference.child("Users").child(userID).setValue(userObj)
                progressDialog.dismiss()
                startActivity(Intent(this@ProfileSetupActivity, MainActivity::class.java))
                finish()
            }
        }


    }

    //pick a photo from gallery
    private fun pickPhoto() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //if not access granted do this
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            //if have permissions do this
            val galleyIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleyIntent, 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleyIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleyIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            pickedPhoto = data.data!!
            if (pickedPhoto != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, pickedPhoto!!)
                    pickedBitmap = ImageDecoder.decodeBitmap(source)
                    profilePictureImageView.setImageBitmap(pickedBitmap)
                } else {
                    pickedBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, pickedPhoto)
                    profilePictureImageView.setImageBitmap(pickedBitmap)
                    profilePictureImageView.scaleType=ImageView.ScaleType.CENTER_CROP
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}