package com.example.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.CallRedirectionService
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_phone_sign_in.*
import java.util.concurrent.TimeUnit

class PhoneSignIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId:String
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_sign_in)
    auth= FirebaseAuth.getInstance()

 btn_getotp.setOnClickListener {
     var phone = phoneno.text.toString().trim()
    if (phone.isNotEmpty()){
          sendVerificationcode("+91$phone")

    }
     else{
         Toast.makeText(this,"Enter phone number",Toast.LENGTH_LONG).show()
    }
 }
btn_signup.setOnClickListener {
    var otp = get_otp.text.toString().trim()
    if (otp.isNotEmpty()) {
        verifyCode(otp)
        otp_layout.visibility=View.GONE

    } else {
        Toast.makeText(this, "Enter otp", Toast.LENGTH_LONG).show()
    }
}
    callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
          val code = credential.smsCode
            if(code!=null){
                get_otp.setText(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // Show a message and update the UI
            Toast.makeText(applicationContext,"Failed",Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
           phone_layout.visibility=View.GONE
            otp_layout.visibility=View.VISIBLE

        }
    }

    }
    private fun sendVerificationcode(phoneNo:String){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNo)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
     private fun verifyCode(code:String){
         val credential:PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerificationId, code)
         signUp(credential)
     }
    private fun signUp(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCredential:success")
                val user:FirebaseUser? = task.result?.user

                    val intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // Sign in failed, display a message and update the UI
                  //  Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                      Toast.makeText(this,"Code entered is incorrect",Toast.LENGTH_LONG).show()
                    get_otp.setText("")
                    }
                    // Update UI
                }

            }

    }
}