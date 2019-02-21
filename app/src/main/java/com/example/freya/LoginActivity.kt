package com.example.freya

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

/**
 * A login screen that offers login via email/password.
 */


class LoginActivity : AppCompatActivity() {
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnResetPassword: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnLogin = findViewById(R.id.btn_login)
        inputEmail = findViewById(R.id.email)
        inputPassword = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener(View.OnClickListener {
            fun onClick(v: View) {
                var email = inputEmail.text.toString()
                val password = inputPassword.text.toString()

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(applicationContext, "Email can't be Empty !!", Toast.LENGTH_SHORT).show()
                    return
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(applicationContext, "Password can't be Empty !!", Toast.LENGTH_SHORT).show()
                    return
                }

                progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, OnCompleteListener<AuthResult>() {
                        fun onComplete(task: Task<AuthResult>) {
                            progressBar.visibility = View.GONE
                            if (!task.isSuccessful) {
                                if (password.length < 6) {
                                    inputPassword.setError(getString(R.string.minimum_password))
                                } else {
                                    Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
                                }
                            } else {
                                var intent = Intent(this, MainActivity::class.java)


                            }
                        }
                    })
            }
        })

        //Configure Google Sign In
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
}
