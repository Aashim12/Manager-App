package com.example.manager

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1047779637234-45utcuf5d3nr4bmt6dgni5fs8d8b107h.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
     supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#146775")))
    }

    fun signout(view: View) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Money Manager")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to sign out ?")

        builder.setIcon(R.drawable.ic_baseline_info_24)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            Toast.makeText(this, "Signed out", Toast.LENGTH_LONG).show()
            sign()
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            //Toast.makeText(this, "clicked No", Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun sign() {
        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()

        }
    }
}

