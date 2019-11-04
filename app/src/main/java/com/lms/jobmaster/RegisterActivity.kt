package com.lms.jobmaster

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.emailInput
import kotlinx.android.synthetic.main.activity_login.passwordInput
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var view: View
    private lateinit var auth: FirebaseAuth
    private lateinit var email: TextInputEditText
    private lateinit var fullName: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var passwordConfirm: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initializeInstances()

        // <!-- UI configs --> //
        val actionBar = supportActionBar
        actionBar!!.title = "Register"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)
        registerButton.setOnClickListener { register(view) }
    }

    private fun initializeInstances() {
        auth = FirebaseAuth.getInstance()
        email = emailInput
        fullName = nameInput
        password = passwordInput
        passwordConfirm = confirmInput
        view = findViewById(R.id.root_layout)
    }

    private fun register(view: View) {
        val emailI = email.text.toString()
        val fullName = fullName.text.toString()
        val passwordI = password.text.toString()
        val cPassword = confirmInput.text.toString()
        val db = FirebaseFirestore.getInstance()

        if (emailI.isNotEmpty() && fullName.isNotEmpty() && passwordI.isNotEmpty() && cPassword.isNotEmpty()) {
            registerButton.isEnabled = true
            auth.createUserWithEmailAndPassword(emailI, passwordI)
                .addOnCompleteListener(this) { task ->
                    when {
                        task.isSuccessful -> {

                            val user = hashMapOf(
                                "fullName" to fullName,
                                "email" to emailI
                            )

                            db.collection("Users")
                                .add(user)
                                .addOnSuccessListener { showDialogSuccess() }
                                .addOnFailureListener { e ->
                                    Log.w("Firebase error", "Error adding document", e)
                                }
                        }
                        else -> {
                            Snackbar.make(
                                view,
                                task.exception!!.message.toString(),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }
    }

    private fun showDialogSuccess() {
        var alertDialogBuilder = MaterialAlertDialogBuilder(
            this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
        )
            .setTitle("User created successfully!")
            .setMessage("Would you like login now?")
            .setPositiveButton("Yes") { _: DialogInterface, _: Int -> goToMain() }
            .setNegativeButton("Later", null)
            .show()

        alertDialogBuilder.show()
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        return startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            backLogin()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

