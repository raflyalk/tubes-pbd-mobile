package com.example.freya

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * A login screen that offers login via email/password.
 */


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:$connectionResult")
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = "LoginClass"
        private val RC_SIGN_IN = 9001
    }

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: TextView
    private lateinit var btnResetPassword: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var btnGoogleLogin: SignInButton


    private var mGoogleApiClient: GoogleApiClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mDatabaseRef = FirebaseDatabase.getInstance().getReference()
        mFirebaseAuth = FirebaseAuth.getInstance()

        if (mFirebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnLogin = findViewById(R.id.btn_login)
        inputEmail = findViewById(R.id.email)
        inputPassword = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)
        btnGoogleLogin = findViewById(R.id.sign_in_button)
        btnSignUp = findViewById(R.id.link_signup)


        btnLogin.setOnClickListener {
            loginButtonOnClick()
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }


        //Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.def_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        btnGoogleLogin.setOnClickListener {
            googleSignIn()
        }
    }

    //Login Email
    fun loginButtonOnClick() {
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Email can't be Empty !!", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Password can't be Empty !!", Toast.LENGTH_SHORT).show()
            return
        }

        println(email)
        println(password)

        emailSignIn(email, password)


        //progressBar.visibility = View.VISIBLE
        println("End")
    }

    private fun emailSignIn(n_email: String, n_password: String) {
        Log.d(TAG, "SignIn")
        if (!validateForm()) {
            return
        }

        mFirebaseAuth.signInWithEmailAndPassword(n_email, n_password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult>() { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val firebaseUser = mFirebaseAuth.currentUser!!
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException())
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(inputEmail.text)) {
            inputEmail.setError("Required")
            result = false
        } else {
            inputEmail.setError(null)
        }

        if (TextUtils.isEmpty(inputPassword.text.toString())) {
            inputPassword.setError("Required")
            result = false
        } else {
            inputPassword.setError(null)
        }
        return result
    }

    private fun googleSignIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
        finish()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful) {
                    Log.w(TAG, "signInWithCredential", task.exception)
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
    }
}
