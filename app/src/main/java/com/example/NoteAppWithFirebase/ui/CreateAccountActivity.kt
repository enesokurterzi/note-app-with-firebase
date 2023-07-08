package com.example.NoteAppWithFirebase.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.NoteAppWithFirebase.configs.Util
import com.example.NoteAppWithFirebase.databinding.ActivityCreateAccountBinding
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.createAccountBtn.setOnClickListener { createAccount() }
        binding.loginTextViewBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun createAccount() {
        binding.apply {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            val isValidated = validateData(email, password, confirmPassword)
            if (!isValidated) {
                return
            }

            createAccountInFirebase(email, password)

        }
    }

    private fun createAccountInFirebase(email: String, password: String) {
        changeInProgress(true)

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {task ->
                changeInProgress(false)
                if (task.isSuccessful) {
                    Util.showToast(this,
                        "Successfully create account, check email to verify")
                    firebaseAuth.currentUser!!.sendEmailVerification()
                    firebaseAuth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Util.showToast(this, task.exception!!.localizedMessage!!)
                }

        }

    }

    private fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                createAccountBtn.visibility = View.GONE
            }
        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                createAccountBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun validateData(email: String, password: String, confirmPassword: String): Boolean {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Email is invalid"
            return false
        }
        if (password.length < 6) {
            binding.passwordEditText.error = "Password length is invalid"
            return false
        }
        if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = "Password not matched"
        }
        return true
    }

}