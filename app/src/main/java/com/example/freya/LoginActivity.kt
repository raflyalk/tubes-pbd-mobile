package com.example.freya

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */


class LoginActivity : AppCompatActivity() {
    companion object {
        private val TAG = "ClassName"
        private val RC_SIGN_IN = 9001
    }
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnResetPassword: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var btnGoogleLogin: SignInButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mDatabaseRef = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnLogin = findViewById(R.id.btn_login)
        inputEmail = findViewById(R.id.email)
        inputPassword = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)
        btnGoogleLogin = findViewById(R.id.sign_in_button)


        btnLogin.setOnClickListener {
            loginButtonOnClick()
        }


        //Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogleLogin.setOnClickListener {
            googleSignIn()
        }
    }

    //Login Email Masih error
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

        auth.signInWithEmailAndPassword(n_email, n_password)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult>() { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val firebaseUser = auth.currentUser!!
//                    updateUI(firebaseUser)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException())
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            })
    }

    //
    override fun onStart() {
        super.onStart()
        var account = GoogleSignIn.getLastSignedInAccount(this)
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun onAuthSuccess(user: FirebaseUser) {

        val username = usernameFromEmail(user.email as String)

        writeNewAdmin(user.uid, username, user.email as String)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun usernameFromEmail(n_email: String): String {
        if (n_email.contains("@")) {
            return n_email.split("@")[0]
        } else {
            return n_email
        }
    }

    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(email.text)) {
            email.setError("Required")
            result = false
        } else {
            email.setError(null)
        }

        if (TextUtils.isEmpty(password.text.toString())) {
            password.setError("Required")
            result = false
        } else {
            password.setError(null)
        }
        return result
    }

    private fun writeNewAdmin(userId: String, n_name: String, n_email: String) {
        val admin = Admin(n_name, n_email)

        mDatabaseRef.child("admins").child(userId).setValue(admin)
    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//    }
//
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//
//            updateUI(account)
//        } catch (e: ApiException) {
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode())
//            updateUI(null)
//        }
//    }
//
//    fun updateUI(account: GoogleSignInAccount?) {
//        //UI Update
//    }
}
