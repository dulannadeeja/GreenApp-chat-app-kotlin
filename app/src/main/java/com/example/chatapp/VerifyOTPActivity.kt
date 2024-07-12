package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.chatapp.databinding.ActivityVerifyOtpactivityBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class VerifyOTPActivity : AppCompatActivity() {

    private lateinit var getVerifiedButton: Button
    private lateinit var resendOTPTextView: TextView
    private lateinit var codePart1Node:EditText
    private lateinit var codePart2Node:EditText
    private lateinit var codePart3Node:EditText
    private lateinit var codePart4Node:EditText
    private lateinit var codePart5Node:EditText
    private lateinit var codePart6Node:EditText
    private lateinit var mobileNumberTextView: TextView
    private lateinit var codeFixed:String
    private lateinit var binding: ActivityVerifyOtpactivityBinding
    private lateinit var progressDialog: AlertDialog
    private lateinit var progressDialogBuilder: AlertDialog.Builder

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var callBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otpactivity)

        //view binding
        binding= ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //add alert dialog
        progressDialogBuilder= AlertDialog.Builder(this)
        progressDialogBuilder.setView(R.layout.sending_otp_progress_layout)
        progressDialog=progressDialogBuilder.create()
        progressDialog.show()


        //get mobile number
        val mobileNumber=intent.getStringExtra("MobileNumberFixed")

        //set mobile number
        mobileNumberTextView=binding.textViewMobileNumber
        mobileNumberTextView.text="$mobileNumber"

        // Initialize Firebase Auth
        auth = Firebase.auth

        if (mobileNumber != null) {
            startPhoneNumberVerification(mobileNumber)
        }

        codePart1Node=binding.editTextcode1
        codePart2Node=binding.editTextcode2
        codePart3Node=binding.editTextcode3
        codePart4Node=binding.editTextcode4
        codePart5Node=binding.editTextcode5
        codePart6Node=binding.editTextcode6

        inputController()

        //set Verify Button Click Listener
        getVerifiedButton=binding.buttonVerifyOTP
        getVerifiedButton.setOnClickListener {
            if(codePart1Node.text.toString().trim().isNotEmpty() ||codePart2Node.text.toString().trim().isNotEmpty() ||codePart3Node.text.toString().trim().isNotEmpty() ||codePart4Node.text.toString().trim().isNotEmpty() ||codePart5Node.text.toString().trim().isNotEmpty() ||codePart6Node.text.toString().trim().isNotEmpty()){
                codeFixed="${codePart1Node.text.toString().trim()}${codePart2Node.text.toString().trim()}${codePart3Node.text.toString().trim()}${codePart4Node.text.toString().trim()}${codePart5Node.text.toString().trim()}${codePart6Node.text.toString().trim()}"
                verifyPhoneNumberWithCode(storedVerificationId,codeFixed)
            }

        }

        //set Resend OTP click event
        resendOTPTextView=binding.textViewResendOTP
        resendOTPTextView.setOnClickListener{
            if (mobileNumber != null) {
                resendVerificationCode(mobileNumber,resendToken)
                Toast.makeText(this@VerifyOTPActivity, "Code Resent.", Toast.LENGTH_SHORT).show()
            }
        }


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
                Log.d("TAG", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(this@VerifyOTPActivity, "Invalid request.", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(this@VerifyOTPActivity, "SMS quota exceeded.", Toast.LENGTH_SHORT).show()
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
                progressDialog.dismiss()
            }
        }
        // [END phone_auth_callbacks]
    }

    private fun inputController(){

        codePart1Node.doOnTextChanged { charSequence: CharSequence?, _: Int, _: Int, _: Int ->
            if (charSequence.toString().trim().isNotEmpty()) {
                codePart2Node.requestFocus()
            }
        }
        codePart2Node.doOnTextChanged { charSequence: CharSequence?, _: Int, _: Int, _: Int ->
            if (charSequence.toString().trim().isNotEmpty()) {
                codePart3Node.requestFocus()
            }
        }
        codePart3Node.doOnTextChanged { charSequence: CharSequence?, _: Int, _: Int, _: Int ->
            if (charSequence.toString().trim().isNotEmpty()) {
                codePart4Node.requestFocus()
            }
        }
        codePart4Node.doOnTextChanged { charSequence: CharSequence?, _: Int, _: Int, _: Int ->
            if (charSequence.toString().trim().isNotEmpty()) {
                codePart5Node.requestFocus()
            }
        }
        codePart5Node.doOnTextChanged { charSequence: CharSequence?, _: Int, _: Int, _: Int ->
            if (charSequence.toString().trim().isNotEmpty()) {
                codePart6Node.requestFocus()
            }
        }
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
    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val intent = Intent(this@VerifyOTPActivity,ProfileSetupActivity::class.java)
                    intent.putExtra("userID", auth.currentUser?.uid)
                    startActivity(intent)
                    finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this@VerifyOTPActivity, "Invalid verification code.", Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                }
            }
    }
    // [END sign_in_with_phone]

    // [START verify_with_code]
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
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
}
