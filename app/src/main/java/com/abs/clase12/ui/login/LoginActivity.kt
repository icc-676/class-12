package com.abs.clase12.ui.login


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import android.widget.Toast

import com.abs.clase12.R
import com.abs.clase12.services.FirebaseCallback
import com.abs.clase12.services.UserService
import com.abs.clase12.utils.afterTextChanged
import com.abs.clase12.utils.isEmailValid
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity() : AppCompatActivity(), FirebaseCallback {

    lateinit var userService: UserService
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        userService = UserService(this, this)
        firebaseAuth = FirebaseAuth.getInstance()
        username.afterTextChanged {
            if (!it.isEmailValid()) {
                username.error = "Is not a valid email"
            }
        }
        password.afterTextChanged {
            if( it.length <6){
                password.error = "The password is not length enough "
            }
        }
        login.setOnClickListener {
            if (checkValues()) {
                progressBar.visibility = View.VISIBLE
                userService.signinWithEmail(
                    username.text.toString(),
                    password.text.toString()
                )
            }
        }
        register.setOnClickListener {
            if (checkValues()) {
                progressBar.visibility = View.VISIBLE
                userService.signupWithEmail(
                    username.text.toString(),
                    password.text.toString()
                )
            }
        }
        firebaseui.setOnClickListener {
            createSignInIntent()
        }
        google_button.setOnClickListener {
            loginUserWithGoogle()
        }
        logout.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            userService.logout()
        }

    }

    private fun checkValues(): Boolean {
        if (username.text.isNotEmpty() && password.text.isNotEmpty()) {
            return true
        } else {
            Toast.makeText(
                this, "Invalid credentials.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(
                    this, "Welcome ${user?.email}.",
                    Toast.LENGTH_SHORT
                ).show()
                print(user?.metadata)
            } else {
                Toast.makeText(
                    this, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == G_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                userService.sigInWithGoogle(account)
            } catch (e: ApiException) {
                println(e)
            }
        }
    }

    fun loginUserWithGoogle() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(this.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
        val siginIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(siginIntent, RC_SIGN_IN)
    }


    override fun onSignUp(user: FirebaseUser?) {
        progressBar.visibility = View.GONE
        if (user != null) {
            Toast.makeText(
                this, "Welcome ${user.email}.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSigning(user: FirebaseUser?) {
        progressBar.visibility = View.GONE
        if (user != null) {
            Toast.makeText(
                this, "Welcome ${user.email}.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onGoogleLogin(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(
                this, "Welcome ${user.email}.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onLogout() {
        progressBar.visibility = View.GONE
        Toast.makeText(
            this, "Logout successful.",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        private const val RC_SIGN_IN = 123
        private const val G_SIGN_IN = 1
    }
}

