package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatapp.databinding.ActivityProfileSetupBinding
import com.example.chatapp.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        //set view binding
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get initialized firebase auth
        auth= FirebaseAuth.getInstance()

        //start sendOtpActivity after pressed Agree Button
        binding.buttonAgree.setOnClickListener{
            val intent = Intent(this@WelcomeActivity,SendOTPActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if (auth.currentUser!==null){
            Toast.makeText(this@WelcomeActivity,"${auth.currentUser!!.uid}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@WelcomeActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}