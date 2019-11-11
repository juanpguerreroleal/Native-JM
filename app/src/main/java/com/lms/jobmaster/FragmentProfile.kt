package com.lms.jobmaster

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.net.URL

private const val READ_REQUEST_CODE: Int = 42
class FragmentProfile: Fragment(){
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var profile_string: String
    private lateinit var empty_string: String

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    override fun onStart() {
        super.onStart()
        edit_Button.setOnClickListener{editOrSaveEvent()}
        cancel_Button.setOnClickListener{cancelEvent()}
        uploadPhoto_Button.setOnClickListener{uploadPhoto()}
        uploadPhoto_Button.isClickable = false
        ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.userTypes_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            userTypes_Spinner.adapter = adapter
            userTypes_Spinner.isEnabled = false
        }
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
                    val occupancy = occupancy_EditText.text.toString()
                    val phone = phone_EditText.text.toString()
                    val userTypePosition = userTypes_Spinner.selectedItemPosition
                    if(document.exists()){
                        val profileObject = hashMapOf<String, Any>(
                                "occupancy" to occupancy,
                                "fullName" to fullname,
                                "email" to email,
                                "experience" to experience,
                                "phone" to phone,
                                "userType" to userTypePosition
                        )
                        db.collection(profile_string).document(currentUser.uid).update(profileObject)
                                .addOnSuccessListener { Log.d(TAG, "Se actualizo el perfil correctamente") }
                                .addOnFailureListener { e -> Log.w(TAG, "Ocurrio un error en la actualizacion del perfil", e) }
                        Log.d(TAG, "")
                    }
                    else{
                        val profileObject = hashMapOf(
                                "uid" to currentUser.uid,
                                "occupancy" to occupancy,
                                "fullName" to fullname,
                                "email" to email,
                                "experience" to experience,
                                "phone" to phone,
                                "userType" to userTypePosition
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
            occupancy_EditText.isEnabled = false
            phone_EditText.isEnabled = false
            userTypes_Spinner.isEnabled = false
            uploadPhoto_Button.isClickable = false
            edit_Button.setText(R.string.edit)
            Toast.makeText(context ,R.string.text_changes_saved, Toast.LENGTH_SHORT).show()
        }
        else{
            userTypes_Spinner.isEnabled = true
            fullName_EditText.isEnabled = true
            email_EditText.isEnabled = true
            experience_EditText.isEnabled = true
            occupancy_EditText.isEnabled = true
            phone_EditText.isEnabled = true
            uploadPhoto_Button.isClickable = true
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
        occupancy_EditText.isEnabled = false
        phone_EditText.isEnabled = false
        userTypes_Spinner.isEnabled = false
        uploadPhoto_Button.isClickable = false
        edit_Button.setText(R.string.edit)
        Toast.makeText(context ,R.string.text_non_editable, Toast.LENGTH_SHORT).show()
    }
    private fun updateEditTexts(){
        val currentUser = auth.currentUser
        var uid = currentUser!!.uid
        empty_string = getString(R.string.empty)
        db.collection(profile_string).document(uid).get().addOnSuccessListener { document ->
            if(document != null){
                val fullname = document.data?.get("fullName").toString()
                val email = document.data?.get("email").toString()
                val experience = document.data?.get("experience").toString()
                val occupancy = document.data?.get("occupancy").toString()
                val phone = document.data?.get("phone").toString()
                val userTypePosition = document.data?.get("userType").toString()
                val photo = document.data?.get("photoUrl").toString()
                if(fullname.isNotEmpty() and fullname.isNotBlank() and !fullname.equals("null")){
                    fullName_EditText.setText(document.data?.get("fullName").toString())
                }
                else{
                    fullName_EditText.setText(empty_string)
                }
                if(email.isNotEmpty() and email.isNotBlank() and !email.equals("null")){
                    email_EditText.setText(document.data?.get("email").toString())
                }
                else{
                    email_EditText.setText(empty_string)
                }
                if(experience.isNotEmpty() and experience.isNotBlank() and !experience.equals("null")){
                    experience_EditText.setText(document.data?.get("experience").toString())
                }
                else{
                    experience_EditText.setText(empty_string)
                }
                if(occupancy.isNotEmpty() and occupancy.isNotBlank() and !occupancy.equals("null")){
                    occupancy_EditText.setText(document.data?.get("occupancy").toString())
                }
                else{
                    occupancy_EditText.setText(empty_string)
                }
                if(phone.isNotEmpty() and phone.isNotBlank() and !phone.equals("null")){
                    phone_EditText.setText(document.data?.get("phone").toString())
                }
                else{
                    phone_EditText.setText(empty_string)
                }
                if(photo.isNotEmpty() and photo.isNotBlank() and !photo.equals("null")){
                    Picasso.get().load(Uri.parse(photo)).into(userPhoto_ImageView)
                }
                if(userTypePosition.isNotEmpty() and userTypePosition.isNotBlank() and !userTypePosition.equals("null")){
                    userTypes_Spinner.setSelection(userTypePosition.toInt())
                }
                if(fullname.equals("null") and
                        phone.equals("null") and
                        email.equals("null") and
                        occupancy.equals("null")){
                    val dialogBuilder = AlertDialog.Builder(requireActivity())
                    dialogBuilder.setMessage(R.string.nonexistent_profile)
                            .setCancelable(false)
                            .setPositiveButton(R.string.accept, DialogInterface.OnClickListener {
                                dialog, id -> editOrSaveEvent()
                            })
                    val alert = dialogBuilder.create()
                    alert.setTitle("Update your profile")
                    alert.show()
                }
            }
        }.addOnFailureListener{
            e -> Log.e(TAG, "Error")
        }
    }
    private fun uploadPhoto(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        val currentUser = auth.currentUser
        if (currentUser != null){
            val uid = currentUser.uid
            if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                resultData?.data?.also { uri ->
                    userPhoto_ImageView.setImageURI(uri)
                    val storageRef = storage.reference
                    var file = uri
                    val imageRef = storageRef.child("UsersPhotos/${uid}.jpg")
                    var uploadTask = imageRef.putFile(file)
                    val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation imageRef.downloadUrl
                    }).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val downloadUri = task.result.toString()
                            uploadPhotoUrl(downloadUri)
                        } else {

                        }
                    }
                }
            }
        }
    }
    private fun uploadPhotoUrl(url: String){
        if(url.isNotEmpty() and !url.equals("null") and url.isNotBlank()){
            val currentUser = auth.currentUser
            if(currentUser != null){
                db.collection(profile_string).document(currentUser.uid).get().addOnSuccessListener { document ->
                    if(document != null){
                        if(document.exists()){
                            val profileObject = hashMapOf<String, Any>(
                                    "photoUrl" to url
                            )
                            db.collection(profile_string).document(currentUser.uid).update(profileObject)
                                    .addOnSuccessListener { Log.d(TAG, "Se actualizo el perfil correctamente") }
                                    .addOnFailureListener { e -> Log.w(TAG, "Ocurrio un error en la actualizacion del perfil", e) }
                            Log.d(TAG, "")
                        }
                        else{
                            val profileObject = hashMapOf(
                                    "uid" to currentUser.uid,
                                    "photoUrl" to url
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
    }
}