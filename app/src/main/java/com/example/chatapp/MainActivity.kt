package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.chatapp.adapters.FragmentAdapter
import com.example.chatapp.adapters.MessagesAdapter
import com.example.chatapp.controllers.ImageSender
import com.example.chatapp.controllers.PermissionManager
import com.example.chatapp.controllers.StatusUpdater
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.fragments.ChatsFragment
import com.example.chatapp.fragments.ContactsFragment
import com.example.chatapp.fragments.StatusFragment
import com.example.chatapp.model.Message
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 2
    private lateinit var currentUserID: String
    private lateinit var currentPhotoPath:String
    private val GALLERY_PERMISSION_REQUEST_CODE = 1
    private val MEDIA_REQUEST_CODE=1
    private val CAPTURE_REQUEST_CODE = 2
    private var capturedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        setUpTabs()


        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                Handler().postDelayed({
                    setUpFlotingActionButton(position)
                }, 400)

            }

        })


    }

    private fun setUpFlotingActionButton(position: Int) {

        val flotingActionButton = findViewById<FloatingActionButton>(R.id.floating_action_button)
        flotingActionButton.hide()
        when (position) {
            0 -> {
                flotingActionButton.setImageResource(R.drawable.ic_message_black_24dp)
                flotingActionButton.show()
                flotingActionButton.setOnClickListener {
                    startActivity(Intent(this@MainActivity, ShowUsersActivity::class.java))
                }

            }
            1 -> {}
            2 -> {
                flotingActionButton.setImageResource(R.drawable.ic_camera)
                flotingActionButton.show()
                flotingActionButton.setOnClickListener {
                    // on below line we are creating a new bottom sheet dialog.
                    var bottomSheetDialog = BottomSheetDialog(this@MainActivity)

                    // on below line we are inflating a layout file which we have created.
                    val view = layoutInflater.inflate(R.layout.bottomsheet_add_status_layout, null)

                    val btnCamera = view.findViewById<ImageView>(R.id.imageViewCameraIcon)
                    val btnGallery = view.findViewById<ImageView>(R.id.imageViewGalleryIcon)

                    // on below line we are adding on click listeners
                    btnCamera.setOnClickListener {
                        openCamera()
                        bottomSheetDialog.dismiss()
                    }
                    btnGallery.setOnClickListener{
                        openGallery()
                        bottomSheetDialog.dismiss()
                    }

                    bottomSheetDialog.setCancelable(true)
                    bottomSheetDialog.setContentView(view)
                    bottomSheetDialog.show()
                }
            }
        }
    }

    private fun openGallery() {
        val isGranted=PermissionManager(this@MainActivity,this@MainActivity).checkGalleryPermission()
        if(isGranted){
            val galleyIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleyIntent, 2)
        }else{
            PermissionManager(this@MainActivity,this@MainActivity).grantGalleryPermission(GALLERY_PERMISSION_REQUEST_CODE)
        }
    }

    private fun openCamera() {
        val isGranted= PermissionManager(this@MainActivity,this@MainActivity).checkCameraPermission()
        if(isGranted){
            capturePhoto()
        }else{
            PermissionManager(this@MainActivity,this@MainActivity).grantCameraPermission(CAMERA_PERMISSION_REQUEST_CODE)
        }
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
                    Toast.makeText(this@MainActivity,"Error occurred while creating the File",
                        Toast.LENGTH_LONG).show()
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
                val intent=Intent(this@MainActivity,ReviewStatusActivity::class.java)
                intent.putExtra("pickedPhotoUri","$capturedPhotoUri")
                startActivity(intent)
            }
        }


        //do this on Media requests
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val pickedPhoto = data.data!!
            val intent=Intent(this@MainActivity,ReviewStatusActivity::class.java)
            intent.putExtra("pickedPhotoUri","$pickedPhoto")
            startActivity(intent)

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    //setup tabLayout items
    private fun setUpTabs() {
        val adapter = FragmentAdapter(supportFragmentManager)
        adapter.addFragment(ChatsFragment(), "chats")
        adapter.addFragment(ContactsFragment(), "contact")
        adapter.addFragment(StatusFragment(), "status")
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }


    //Attach main menu to the actionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id: Int = item.itemId

        when (id) {
            R.id.action_more -> {
                Log.i("MYTAG", "$id")
            }
            R.id.menu_search -> {
                Log.i("MYTAG", "$id")
            }
            R.id.action_new_group -> {
                Log.i("MYTAG", "$id")
            }
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }


        return super.onOptionsItemSelected(item)

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