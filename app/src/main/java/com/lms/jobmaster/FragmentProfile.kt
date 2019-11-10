package com.lms.jobmaster

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlin.math.absoluteValue
import kotlin.math.exp

class FragmentProfile: Fragment(){
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var profile_string: String

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        edit_Button.setOnClickListener{editOrSaveEvent()}
        cancel_Button.setOnClickListener{cancelEvent()}
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View?{
        super.onStart()
        val currentUser = auth.currentUser
        db = FirebaseFirestore.getInstance()
        profile_string = getString(R.string.profile_collection)
        if(currentUser != null){
            updateEditTexts()
        }
        else{
            //Redirect to login
        }
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    fun saveData(){
        val currentUser = auth.currentUser
        if(currentUser != null){
            db.collection(profile_string).document(currentUser.uid).get().addOnSuccessListener { document ->
                if(document != null){
                    val fullname = fullName_EditText.text.toString()
                    val email = email_EditText.text.toString()
                    val experience = experience_EditText.text.toString()
                    if(document.exists()){
                        val profileObject = hashMapOf<String, Any>(
                                "fullName" to fullname,
                                "email" to email,
                                "experience" to experience
                        )
                        db.collection(profile_string).document(currentUser.uid).update(profileObject)
                                .addOnSuccessListener { Log.d(TAG, "Se actualizo el perfil correctamente") }
                                .addOnFailureListener { e -> Log.w(TAG, "Ocurrio un error en la actualizacion del perfil", e) }
                        Log.d(TAG, "")
                    }
                    else{
                        val profileObject = hashMapOf(
                                "uid" to currentUser.uid,
                                "fullName" to fullname,
                                "email" to email,
                                "experience" to experience
                        )
                        db.collection(profile_string).document(currentUser.uid).set(profileObject)
                                .addOnSuccessListener { Log.d(TAG, "Se registro el perfil correctamente") }
                                .addOnFailureListener { e -> Log.w(TAG, "Ocurrio un error en el registro del perfil", e) }
                        Log.d(TAG, "")
                    }
                }
            }
        }
    }
    private fun editOrSaveEvent(){
        if(fullName_EditText.isEnabled){
            saveData()
            cancel_Button.visibility = View.INVISIBLE
            fullName_EditText.isEnabled = false
            email_EditText.isEnabled = false
            experience_EditText.isEnabled = false
            edit_Button.setText(R.string.edit)
            Toast.makeText(context ,R.string.text_changes_saved, Toast.LENGTH_SHORT).show()
        }
        else{
            fullName_EditText.isEnabled = true
            email_EditText.isEnabled = true
            experience_EditText.isEnabled = true
            cancel_Button.visibility = View.VISIBLE
            edit_Button.setText(R.string.save)
            Toast.makeText(context ,R.string.text_editable, Toast.LENGTH_SHORT).show()
        }
    }
    private fun cancelEvent(){
        cancel_Button.visibility = View.INVISIBLE
        updateEditTexts()
        fullName_EditText.isEnabled = false
        email_EditText.isEnabled = false
        experience_EditText.isEnabled = false
        edit_Button.setText(R.string.edit)
        Toast.makeText(context ,R.string.text_non_editable, Toast.LENGTH_SHORT).show()
    }
    private fun updateEditTexts(){
        val currentUser = auth.currentUser
        var uid = currentUser!!.uid
        db.collection(profile_string).document(uid).get().addOnSuccessListener { document ->
            if(document != null){
                val fullname = document.data?.get("fullName").toString()
                val email = document.data?.get("email").toString()
                val experience = document.data?.get("experience").toString()
                if(fullname.isNotEmpty() and fullname.isNotBlank() and !fullname.equals("null")){
                    fullName_EditText.setText(document.data?.get("fullName").toString())
                }
                if(email.isNotEmpty() and email.isNotBlank() and !email.equals("null")){
                    email_EditText.setText(document.data?.get("email").toString())
                }
                if(experience.isNotEmpty() and experience.isNotBlank() and !experience.equals("null")){
                    experience_EditText.setText(document.data?.get("experience").toString())
                }
            }
        }.addOnFailureListener{
            e -> Log.e(TAG, "Error")
        }
    }
}