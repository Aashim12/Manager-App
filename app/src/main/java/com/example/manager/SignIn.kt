package com.example.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignIn : AppCompatActivity()
{
    private val RC_SIGN_IN: Int = 123
    private lateinit var auth: FirebaseAuth
    private val TAG= " SignInActivity Tag"
    private lateinit var googleSignInClient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1047779637234-45utcuf5d3nr4bmt6dgni5fs8d8b107h.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signInButton.setOnClickListener {
            signIn()
        }
        auth = Firebase.auth

    }
    override fun onStart() {
        super.onStart()
        val currentuser= auth.currentUser
        updatedUI(currentuser)
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handlesignInresult(task)

        }


    }
        private fun handlesignInresult(completedTask:  Task<GoogleSignInAccount>?) {
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = completedTask?.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken,null)   // Global scope , work in background thread, a coroutine
        signInButton.visibility= View.GONE
        phonelogin.visibility=View.GONE
        green_money.visibility=View.GONE
        progressbar.visibility= View.VISIBLE

        GlobalScope.launch(Dispatchers.IO){
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main){
                updatedUI(firebaseUser)        // with context main dispatch switches from background thread to main thread
            }
        }
    }
    private fun updatedUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser!= null){

            val mainActivityIntent = Intent(this,HomeActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }
        else{
            signInButton.visibility=View.VISIBLE
            progressbar.visibility=View.GONE
            phonelogin.visibility=View.VISIBLE
        }
    }

    fun phoneLogin(view: android.view.View) {
        val intent = Intent(this,PhoneSignIn::class.java)
        startActivity(intent)
    }


}