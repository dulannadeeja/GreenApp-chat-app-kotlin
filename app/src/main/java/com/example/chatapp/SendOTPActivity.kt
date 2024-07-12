package com.example.chatapp


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import com.example.chatapp.databinding.ActivitySendOtpactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SendOTPActivity : AppCompatActivity() {

    private lateinit var mobileNumber: String
    private lateinit var binding: ActivitySendOtpactivityBinding
    private lateinit var sendOTPButton:Button
    private lateinit var mobileEditText: EditText
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_otpactivity)

        //view binding
        binding= ActivitySendOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Request Focus On input field
        binding.editTextMobile.requestFocus()

        //initializing views
        sendOTPButton=binding.buttonSendOTP
        mobileEditText=binding.editTextMobile

        // Initialize Firebase Auth
        auth = Firebase.auth

        //inspect mobile number while typing
        mobileEditText.addTextChangedListener {
            mobileNumber=mobileEditText.text.toString().trim(){it<= (' ')}
            inspectMobileNumberInput(mobileNumber)
        }

        //sendOTP button click event
        sendOTPButton.setOnClickListener {
            mobileNumber=mobileEditText.text.toString().trim(){it<= (' ')}
            validateMobileNumber(mobileNumber)
        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun inspectMobileNumberInput(mobileNumber:String) {
        if(mobileNumber.isNotEmpty()){
            if(mobileNumber[0].equals('0',true) || mobileNumber.length<9){
                //set mobile input field color to red
                mobileEditText.setTextColor(getColor(R.color.colorRed))
            }else{
                //set mobile input field color to textPrimary color
                mobileEditText.setTextColor(getColor(R.color.colorTextPrimary))
            }
        }
    }

    //validate mobile number format
    private fun validateMobileNumber(mobileNumber: String) {
        if(mobileNumber.isNotEmpty()) {
            if(mobileNumber.length<9 || mobileNumber.length>9) {
                //Mobile number is in invalid format
                Toast.makeText(this@SendOTPActivity, "Check Your Mobile Number", Toast.LENGTH_SHORT).show()
            }else{
                if(mobileNumber[0].equals('0',true)){
                    Toast.makeText(this@SendOTPActivity, "Don't Put 0 to the start", Toast.LENGTH_SHORT).show()
                }else{
                    //Mobile number is ready to go

                    val mobileNumberFixed= "+94$mobileNumber"
                    val intent=Intent(this@SendOTPActivity,VerifyOTPActivity::class.java)
                    intent.putExtra("MobileNumberFixed",mobileNumberFixed)
                    startActivity(intent)
                    finish()
                }

            }
        }else{
            //Mobile number is empty
            Toast.makeText(this@SendOTPActivity, "Enter Your Mobile Number.", Toast.LENGTH_SHORT).show()
        }

    }

}


