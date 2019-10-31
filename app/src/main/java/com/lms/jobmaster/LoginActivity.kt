package com.lms.jobmaster

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var view: View
    private lateinit var auth: FirebaseAuth
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private var counterIntents = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email = emailInput
        password = passwordInput
        view = findViewById(R.id.root0_layout)
        onStart()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            loginButton.setOnClickListener { signIn(view) }
        } else {
            updateUI(currentUser)
        }

    }

    private fun signIn(view: View) {

        val p0 = email.text.toString()
        val p1 = password.text.toString()
        if(p0.isNotEmpty() && p1.isNotEmpty()){
            loginButton.isEnabled = true
            auth.signInWithEmailAndPassword(p0, p1).addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        val user = auth.currentUser
                        updateUI(user)
                    }
                    else -> {
                        Log.w("Failed", task.exception)
                        Snackbar.make(
                            view,
                            task.exception!!.message.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                        counterIntents += 1
                        if (counterIntents == 3) {
                            showDialog()
                        }
                    }
                }
            }

        }
    }


    private fun showDialog() {
        var alertDialogBuilder = MaterialAlertDialogBuilder(
            this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
        )
            .setTitle("Not founded account")
            .setMessage("We think that you don't have account yet, would you like create your account now?")
            .setPositiveButton("Yes") { _: DialogInterface, _: Int -> goToRegister() }
            .setNegativeButton("No", null)
            .show()

        alertDialogBuilder.show()
    }

    private fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        return startActivity(intent)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("User data", currentUser)
        startActivity(intent)
    }
}

