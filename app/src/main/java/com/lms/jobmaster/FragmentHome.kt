package com.lms.jobmaster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*

class FragmentHome : Fragment(){
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var profile_string: String

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentUser = auth.currentUser
        db = FirebaseFirestore.getInstance()
        if(currentUser != null){  retrieveData() }
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    private fun retrieveData(){
        val currentUser = auth.currentUser
        var uid = currentUser!!.uid
        profile_string = getString(R.string.profile_collection)
        if (!currentUser.equals(null)){
            db.collection(profile_string).document(uid).get().addOnSuccessListener { document ->
                if (!document.equals(null)){
                    val fullname = document.data?.get("fullName").toString()
                    val welcomeString = "Welcome ".plus(fullname).plus("!")
                    val photo = document.data?.get("photoUrl").toString()
                    if (fullname.isNotEmpty() && photo.isNotEmpty()){
                        Name.text = welcomeString
                        Picasso.get().load(photo).into(profile_picture)
                    }
                }
            }
        }
    }

}