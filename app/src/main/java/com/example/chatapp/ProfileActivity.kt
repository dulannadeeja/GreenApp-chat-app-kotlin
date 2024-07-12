package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.chatapp.controllers.StatusUpdater
import com.example.chatapp.databinding.ActivityProfileBinding
import com.example.chatapp.model.UserData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userInfo: UserData
    private lateinit var pickedPhoto: Uri
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var storage: FirebaseStorage
    private lateinit var currentUserID: String
    private lateinit var progressDialogBuilder:AlertDialog.Builder
    private lateinit var progressDialog:AlertDialog
    private lateinit var currentPhotoPath: String
    private var callBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var storedVerificationId:String
    private lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    private lateinit var OTPbottomSheetDialog:BottomSheetDialog
    private lateinit var fixedPhone:String

    var photoFileTemp:File?=null
    val CAMERA_REQUEST_CODE=42
    val CAPTURE_REQUEST_CODE=41

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //set view bindings
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //add alert dialog
        progressDialogBuilder = AlertDialog.Builder(this@ProfileActivity)
        progressDialogBuilder.setView(R.layout.update_profile_progress_layout)
        progressDialog = progressDialogBuilder.create()

        //initializing firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        currentUserID = auth.currentUser!!.uid

        if (currentUserID != null) {
            getUserInfo(currentUserID)
        }

        userInfo = UserData()

        binding.fabUploadProfile.setOnClickListener {
            showBottomSheetPickPhoto()
        }
        binding.buttonEditName.setOnClickListener{
            showBottomSheetEditName()
        }
        binding.buttonEditInfo.setOnClickListener{
            showBottomSheetEditInfo()
        }
        binding.buttonEditPhone.setOnClickListener{
            showBottomSheetEditPhone()
        }
        binding.imageViewProfilePicture.setOnClickListener{
            val intent=Intent(this@ProfileActivity,ProfilePictureActivity::class.java)
            intent.putExtra("profileUri","${userInfo.profileUri.toString()}")
            startActivity(intent)
        }
        binding.buttonSignout.setOnClickListener{
            //add alert dialog
            val progressDialogBuilder = AlertDialog.Builder(this@ProfileActivity)
            val view = layoutInflater.inflate(R.layout.sign_out_progress_layout, null)
            val noButton=view.findViewById<Button>(R.id.buttonNo)
            val yesButton=view.findViewById<Button>(R.id.buttonYes)
            progressDialogBuilder.setView(view)
            val progressDialog = progressDialogBuilder.create()
            progressDialog.show()
            noButton.setOnClickListener{
                progressDialog.dismiss()
            }
            yesButton.setOnClickListener{
                Firebase.auth.signOut()
                progressDialog.dismiss()
                val intent=Intent(this@ProfileActivity,SplashScreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun showBottomSheetEditPhone() {
        // on below line we are creating a new bottom sheet dialog.
        bottomSheetDialog = BottomSheetDialog(this@ProfileActivity)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottomsheet_edit_profile_info_layout, null)
        view.findViewById<TextView>(R.id.textViewTitle).text="Enter Your Phone"
        view.findViewById<EditText>(R.id.editTextField).inputType = InputType.TYPE_CLASS_NUMBER
        view.findViewById<EditText>(R.id.editTextField).hint="94722146452"

        val btnCancel = view.findViewById<MaterialButton>(R.id.buttonCancel)
        val btnSave = view.findViewById<MaterialButton>(R.id.buttonSave)

        // on below line we are adding on click listeners
        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        btnSave.setOnClickListener {
            val editTextField=view.findViewById<EditText>(R.id.editTextField)
            val phone:String=editTextField.text.toString().trim()
            if (!phone.isNullOrEmpty()) {
                fixedPhone="+94${phone}"
                bottomSheetDialog.dismiss()
                popUpVerifyOtpBottomsheet()
                startPhoneNumberVerification(fixedPhone)

            }
        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun popUpVerifyOtpBottomsheet() {

        // on below line we are creating a new bottom sheet dialog.
        OTPbottomSheetDialog = BottomSheetDialog(this@ProfileActivity)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottomsheet_edit_profile_info_layout, null)
        view.findViewById<TextView>(R.id.textViewTitle).text="Enter The OTP"
        view.findViewById<EditText>(R.id.editTextField).inputType = InputType.TYPE_CLASS_NUMBER
        view.findViewById<EditText>(R.id.editTextField).hint="Code that we send to your mobile"

        val btnCancel = view.findViewById<MaterialButton>(R.id.buttonCancel)
        val btnSave = view.findViewById<MaterialButton>(R.id.buttonSave)

        // on below line we are adding on click listeners
        btnCancel.setOnClickListener {
            OTPbottomSheetDialog.dismiss()
        }
        btnSave.setOnClickListener {
            val editTextField=view.findViewById<EditText>(R.id.editTextField)
            val OTP:String=editTextField.text.toString().trim()
            if (!OTP.isNullOrEmpty()) {
                Log.i("MYTAG","$OTP")
                Log.i("MYTAG","$storedVerificationId")
                verifyPhoneNumberWithCode(storedVerificationId,OTP)
            }
        }

        OTPbottomSheetDialog.setCancelable(true)
        OTPbottomSheetDialog.setContentView(view)
        OTPbottomSheetDialog.show()
    }

    private fun savePhoneChange(phone: String) {



        database.reference.child("Users")
            .child(currentUserID)
            .child("mobile")
            .setValue(phone)

    }

    init {
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.i("MYTAG", "onVerificationCompleted:$credential")
                Firebase.auth.currentUser!!.updatePhoneNumber(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("MYTAG", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@ProfileActivity, "Invalid request.", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(this@ProfileActivity, "SMS quota exceeded.", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        // [END phone_auth_callbacks]
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callBacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }

    // [START verify_with_code]
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        Log.i("MYTAG", "verifyPhoneNumberWithCode")
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        Firebase.auth.currentUser!!.updatePhoneNumber(credential).addOnSuccessListener {
            Log.i("MYTAG", "updated Successfully")
            savePhoneChange(fixedPhone)
            getUserInfo(currentUserID)
            OTPbottomSheetDialog.dismiss()
        }.addOnFailureListener{
            Log.i("MYTAG", "updated cant get Successful")
        }
    }
    // [END verify_with_code]

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        progressDialog.show()
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callBacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]


    private fun showBottomSheetEditInfo() {
        // on below line we are creating a new bottom sheet dialog.
        bottomSheetDialog = BottomSheetDialog(this@ProfileActivity)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottomsheet_edit_profile_info_layout, null)
        view.findViewById<TextView>(R.id.textViewTitle).text="Enter Your Info"
        view.findViewById<EditText>(R.id.editTextField).hint="your info"

        val btnCancel = view.findViewById<MaterialButton>(R.id.buttonCancel)
        val btnSave = view.findViewById<MaterialButton>(R.id.buttonSave)

        // on below line we are adding on click listeners
        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        btnSave.setOnClickListener {
            val editTextField=view.findViewById<EditText>(R.id.editTextField)
            val info:String=editTextField.text.toString()
            if (!info.isNullOrEmpty()) {
                saveInfoChange(info)
                getUserInfo(currentUserID)
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun saveInfoChange(info: String) {
        database.reference.child("Users")
            .child(currentUserID)
            .child("bio")
            .setValue(info)
    }

    private fun showBottomSheetEditName() {
        // on below line we are creating a new bottom sheet dialog.
        bottomSheetDialog = BottomSheetDialog(this@ProfileActivity)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottomsheet_edit_profile_info_layout, null)

        val btnCancel = view.findViewById<MaterialButton>(R.id.buttonCancel)
        val btnSave = view.findViewById<MaterialButton>(R.id.buttonSave)

        // on below line we are adding on click listeners
        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        btnSave.setOnClickListener {
            val editTextField=view.findViewById<EditText>(R.id.editTextField)
            val name:String=editTextField.text.toString()
            if (!name.isNullOrEmpty()) {
                saveNameChange(name)
                getUserInfo(currentUserID)
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun saveNameChange(name: String) {
        database.reference.child("Users")
            .child(currentUserID)
            .child("userName")
            .setValue(name)
    }

    private fun showBottomSheetPickPhoto() {
        // on below line we are creating a new bottom sheet dialog.
        bottomSheetDialog = BottomSheetDialog(this@ProfileActivity)

        // on below line we are inflating a layout file which we have created.
        val view = layoutInflater.inflate(R.layout.bottomsheet_profile_upload_layout, null)

        val btnCamera = view.findViewById<ImageView>(R.id.imageViewCameraIcon)
        val btnGallery = view.findViewById<ImageView>(R.id.imageViewGalleryIcon)

        // on below line we are adding on click listeners
        btnCamera.setOnClickListener {
            openCamera()
        }
        btnGallery.setOnClickListener {
            openGallery()
        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
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

    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //if not access granted do this
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        } else {
            //if have permissions do this
            createImageFile()
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Log.i("MYTAG","Error occurred while creating the File")
                        null
                    }
                    photoFileTemp=photoFile
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.chatapp",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_REQUEST_CODE)
                    }
                }
            }

        }
    }

    private fun openGallery() {
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

    private fun uploadSelectedImage(pickedPhoto:Uri) {

        progressDialog.show()
        val storageReference = storage.reference.child("Profile").child(currentUserID)
        storageReference.putFile(pickedPhoto!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    database.reference.child("Users")
                        .child(currentUserID)
                        .child("profileUri")
                        .setValue(imageUrl)
                    getUserInfo(currentUserID)


                }
            }
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                progressDialog.dismiss()
            }
        }, 5000)

    }

    private fun getUserInfo(userID: String) {
        database.reference.child("Users")
            .child(userID)
            .get()
            .addOnSuccessListener {
                userInfo = it.getValue(UserData::class.java)!!
                binding.textViewUserName.text = userInfo.userName
                binding.textViewInfo.text = userInfo.bio
                binding.textViewPhone.text = userInfo.mobile
                if (!userInfo.profileUri.isNullOrEmpty()) {
                    Glide.with(this@ProfileActivity).load(userInfo.profileUri.toString())
                        .placeholder(R.drawable.default_avator)
                        .into(binding.imageViewProfilePicture)
                }
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
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAPTURE_REQUEST_CODE)

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            pickedPhoto = data.data!!
            if (pickedPhoto != null) {

                uploadSelectedImage(pickedPhoto)
                bottomSheetDialog.dismiss()

            }

        }
        if(requestCode==CAPTURE_REQUEST_CODE && resultCode==Activity.RESULT_OK){


            pickedPhoto=Uri.fromFile(photoFileTemp)
            uploadSelectedImage(pickedPhoto)
            bottomSheetDialog.dismiss()


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
