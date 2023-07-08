package com.example.NoteAppWithFirebase.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.NoteAppWithFirebase.R
import com.example.NoteAppWithFirebase.configs.Util
import com.example.NoteAppWithFirebase.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener { loginUser() }
        binding.createAccountTextViewBtn.setOnClickListener { goToCreate() }
        binding.forgotPasswordTextViewBtn.setOnClickListener { alertSet() }


    }

    private fun loginUser() {
        binding.apply {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val isValidated = validateData(email, password)
            if (!isValidated) {
                return
            }

            loginAccountInFirebase(email, password)

        }

    }

    private fun loginAccountInFirebase(email: String, password: String) {
        changeInProgress(true)

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
            changeInProgress(false)
            if (task.isSuccessful) {
                if (firebaseAuth.currentUser!!.isEmailVerified) {
                    goToMain()
                } else {
                    Util.showToast(this,
                        "Email not verified, please verify your email.")
                }

            } else {
                Util.showToast(this, task.exception!!.localizedMessage!!)
            }
        }

    }

    private fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
            }
        } else {
            binding.apply {
                progressBar.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun validateData(email: String, password: String): Boolean {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Email is invalid"
            return false
        }
        if (password.length < 6) {
            binding.passwordEditText.error = "Password length is invalid"
            return false
        }
        return true
    }

    private fun goToCreate() {
        startActivity(Intent(this, CreateAccountActivity::class.java))
        finish()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun alertSet() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val view = layoutInflater.inflate(R.layout.dialog_forgot, null)
        val userEmail = view.findViewById<EditText>(R.id.forgot_email_edit_text)

        builder.setView(view)
        val dialog = builder.create()

        view.findViewById<Button>(R.id.reset_button).setOnClickListener {
            compareEmail(userEmail)
            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            dialog.dismiss()
        }

        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }

    private fun compareEmail(email: EditText) {
        if (email.text.toString().isEmpty()) {
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            return
        }

        firebaseAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Util.showToast(this, "Check your email.")
            } else {
                Util.showToast(this, task.exception!!.localizedMessage!!)
            }
        }

    }


}