package com.abs.clase12.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

interface FirebaseCallback {
    fun onSignUp(user: FirebaseUser?)
    fun onSigning(user: FirebaseUser?)
    fun onGoogleLogin(user: FirebaseUser?)
    fun onLogout()
}

class UserService(context: Context,listener: FirebaseCallback) {

    private val listener: FirebaseCallback
    private val context: Context
    private var firebaseAuth: FirebaseAuth

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        this.context = context
        this.listener = listener
    }

    fun signinWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    this@UserService.listener.onSigning(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(
                        this.context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun signupWithEmail(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    this@UserService.listener.onSignUp(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(
                        this.context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun sigInWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    this@UserService.listener.onGoogleLogin(firebaseAuth.currentUser)
                } else {
                    Toast.makeText(
                        this.context, "Authentication with Google failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        this@UserService.listener.onLogout()
    }

}