package com.example.chatapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.chatapp.databinding.ActivityStatusViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException


class StatusViewActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {
    private lateinit var storiesProgressView: StoriesProgressView
    private lateinit var database: FirebaseDatabase
    private lateinit var imageView: ImageView
    private lateinit var reverseView: View
    private lateinit var skipView:View
    private lateinit var imagesArrayList: ArrayList<String>
    private var PROGRESS_COUNT = 6
    private val STORIE_DURATION = 5000L
    private var pressTime = 0L
    private var limit = 500L
    private var counter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_view)

        storiesProgressView = findViewById(R.id.storiesProgressView)
        imageView=findViewById(R.id.image)
        reverseView=findViewById(R.id.reverse)
        skipView=findViewById(R.id.skip)



        //get extra from previous activity
        val userID = intent.getStringExtra("userID")

        //initializing
        database= FirebaseDatabase.getInstance()
        imagesArrayList = ArrayList()



        database.getReference("status")
            .child(userID!!)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onDataChange(snapshot: DataSnapshot) {
                    PROGRESS_COUNT = snapshot.childrenCount.toInt()
                    for (snap in snapshot.children) {
                        imagesArrayList.add(snap.child("imageUri").value.toString())
                    }


                        lifecycleScope.launch(Dispatchers.Default) {
                            downloadmagesAndSave(imagesArrayList)
                            val bitmapList=loadImagesFromInternalStorage()
                            Log.i("bitmap","$bitmapList")


                    }



                    storiesProgressView.setStoriesCount(PROGRESS_COUNT)
                    storiesProgressView.setStoryDuration(STORIE_DURATION)
                    storiesProgressView.setStoriesListener(this@StatusViewActivity)

                    Glide.with(applicationContext)
                        .load(imagesArrayList[counter])
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.i("GLIDE", "image loaded")
                                counter = 0
                                storiesProgressView.startStories(counter)
                                return false
                            }

                        }).into(imageView)


                    // bind reverse view
                    reverseView.setOnClickListener { storiesProgressView.reverse() }
                    reverseView.setOnTouchListener { v, event ->
                        when (event!!.action) {
                            MotionEvent.ACTION_DOWN -> {
                                pressTime = System.currentTimeMillis()
                                storiesProgressView.pause()
                                return@setOnTouchListener false
                            }
                            MotionEvent.ACTION_UP -> {
                                val now = System.currentTimeMillis()
                                storiesProgressView.resume()
                                return@setOnTouchListener limit < now - pressTime
                            }
                            else -> {
                                return@setOnTouchListener false
                            }
                        }
                    }

                    // bind skip view
                    skipView.setOnClickListener { storiesProgressView.skip() }
                    skipView.setOnTouchListener { v, event ->
                        when (event!!.action) {
                            MotionEvent.ACTION_DOWN -> {
                                pressTime = System.currentTimeMillis()
                                storiesProgressView.pause()
                                return@setOnTouchListener false
                            }
                            MotionEvent.ACTION_UP -> {
                                val now = System.currentTimeMillis()
                                storiesProgressView.resume()
                                return@setOnTouchListener limit < now - pressTime
                            }
                            else -> {
                                return@setOnTouchListener false
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


    }

    override fun onNext() {
        imageView.setImageResource(R.drawable.placeholder)
        Glide.with(applicationContext).load(imagesArrayList[++counter])
            .placeholder(R.drawable.default_avator)
            .into(imageView)

    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        Glide.with(applicationContext).load(imagesArrayList[--counter])
            .placeholder(R.drawable.default_avator)
            .into(imageView)

    }

    override fun onComplete() {
        onBackPressed()
    }

    override fun onDestroy() {
        // Very important !
        storiesProgressView.destroy()
        super.onDestroy()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView.pause()
                return false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView.resume()
                return limit < now - pressTime
            }
        }
        return super.onTouchEvent(event)
    }

    private fun downloadmagesAndSave(uriList: ArrayList<String>){

        var counter = 0
        for (uri in uriList) {

            val requestOptions = RequestOptions().override(100)
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)

            val bitmap = Glide.with(applicationContext)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .submit()
                .get()

            try {
                openFileOutput("statusImage$counter.jpg", MODE_PRIVATE).use{fileOutputStream->
                    if(!bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream)){
                        throw IOException("Couldn't save bitmap.")
                    }
                }
                Log.i("image", "statusImage$counter.jpg saved successfully.")

            } catch (e: IOException) {
                e.printStackTrace()
                Log.i("image", "Failed to save image.")
            }
            counter++
        }
    }

    private suspend fun loadImagesFromInternalStorage(): List<Bitmap>{
        val files=filesDir.listFiles()
        val filterdFiles=files.filter{it.canRead()&&it.isFile&&it.name.startsWith("statusImage")}.map{
            val bytes=it.readBytes()
            val bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            bitmap
        }
        return filterdFiles
    }
}

