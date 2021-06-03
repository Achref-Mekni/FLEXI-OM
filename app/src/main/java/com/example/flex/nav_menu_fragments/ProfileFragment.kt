package com.example.flex.nav_menu_fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.flex.CircleTransform
import com.example.flex.LoginActivity
import com.example.flex.R
import com.example.flex.Users.MedicalRepresentative
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*



class ProfileFragment : Fragment() {

    companion object {
        const val TAG = "TAG_FRAGMENT_PROFILE"
    }

    private var fullname : String? = null
    private var databaseReference: DatabaseReference? = null
    private var profileNameTextView: TextView? =null
    private  var profilePhonenoTextView: TextView? = null
    private var profileAddressTextView: TextView? = null
    private var profileBirthTextView: TextView? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var profilePicImageView: ImageView? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var textViewEmail: TextView? = null
    private var editTextName: EditText? = null
    private var editNameButton: EditText? = null
    private var editSurNameButton: EditText? = null
    private var editPhoneButton: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        //editTextName = rootView.et_username;
        databaseReference = FirebaseDatabase.getInstance().reference
        //editTextName = findViewById<View>(R.id.et_username) as EditText
        profilePicImageView = rootView.profile_pic_imageView;
        profileNameTextView = rootView.profile_name_textView;
        textViewEmail = rootView.textViewEmailAddress;
        profilePhonenoTextView = rootView.profile_phoneno_textView;
        profileAddressTextView = rootView.profile_address_textView;
        profileBirthTextView = rootView.profile_birth_textView;
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        fullname = profileNameTextView.toString()
        /*rootView.buttonEditName.setOnClickListener {
            buttonClickedEditName()
        }

        rootView.buttonEditSurname.setOnClickListener {
            buttonClickedEditSurname()
        }*/

        rootView.buttonEditPhoneNo.setOnClickListener {
            buttonClickedEditPhoneNo()
        }

        /*rootView.btn_log_out.setOnClickListener {
            navigateLogOut()
        }*/
        // profile management here through rootview.button/.text etc ...
        val storageReference = firebaseStorage!!.reference
        // to change back to Profile Pic
        storageReference.child(firebaseAuth!!.uid!!).child("Images").child("Profile Pic.jpg")
            .downloadUrl.addOnSuccessListener { uri -> // Using "Picasso" (http://square.github.io/picasso/) after adding the dependency in the Gradle.
                // ".fit().centerInside()" fits the entire image into the specified area.
                // Finally, add "READ" and "WRITE" external storage permissions in the Manifest.
                Picasso.get().load(uri).fit().placeholder(R.drawable.profile_bg).centerInside().transform(CircleTransform()).into(profilePicImageView)

            }
        if (firebaseAuth!!.currentUser == null) {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        val user = firebaseAuth!!.currentUser
        val databaseReference = firebaseDatabase!!.reference.child("Users").child(user.uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("firebase", "rani fil baaase heeeey")
                val userProfile: MedicalRepresentative? =
                    dataSnapshot.getValue(MedicalRepresentative::class.java)
                profileNameTextView!!.text = userProfile!!.getUserName() + " " + userProfile.getUserSurname()
                //profileSurnameTextView!!.text = userProfile.getUserSurname()
                profilePhonenoTextView!!.text = userProfile.getUserPhoneno()
                profileAddressTextView!!.text = userProfile.getUserAddress()
                profileBirthTextView!!.text = userProfile.getUserBirth()
                textViewEmail!!.text = user.email
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, databaseError.code, Toast.LENGTH_SHORT)
                    .show()
            }
        })

        return rootView
    }

    /*private fun buttonClickedEditName() {
        val inflater = layoutInflater
        val alertLayout: View =
            inflater.inflate(R.layout.edit_name, null)
        val etUsername = alertLayout.findViewById<EditText>(R.id.et_username)
        val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
        alert.setTitle("Name Edit")
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout)
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false)
        alert.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { _, _ -> })
        alert.setPositiveButton("OK",
            DialogInterface.OnClickListener { _, _ ->
                val name = etUsername.text.toString()
                val surname = profileSurnameTextView!!.text.toString()
                val phoneno = profilePhonenoTextView!!.text.toString()
                val address = profileAddressTextView!!.text.toString()
                val birthdate = profileBirthTextView!!.text.toString()
                val userinformation = MedicalRepresentative(name, surname, phoneno,address,birthdate)
                val user = firebaseAuth!!.currentUser
                databaseReference!!.child("Users").child(user.uid).setValue(userinformation)
                //databaseReference!!.child(user.uid).setValue(userinformation)
                etUsername.onEditorAction(EditorInfo.IME_ACTION_DONE)
            })
        val dialog: android.app.AlertDialog? = alert!!.create()
        dialog!!.show()
    }

    private fun buttonClickedEditSurname() {
        val inflater = layoutInflater
        val alertLayout: View =
            inflater.inflate(R.layout.edit_surname, null)
        val etUserSurname = alertLayout.findViewById<EditText>(R.id.et_userSurname)
        val alert =
            AlertDialog.Builder(requireContext())
        alert.setTitle("Surname Edit")
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout)
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false)
        alert.setNegativeButton(
            "Cancel"
        ) { _, _ -> }
        alert.setPositiveButton(
            "OK"
        ) { _, _ ->
            val name = profileNameTextView!!.text.toString()
            val surname = etUserSurname.text.toString()
            val phoneno = profilePhonenoTextView!!.text.toString()
            val address = profileAddressTextView!!.text.toString()
            val birthdate = profileBirthTextView!!.text.toString()
            val userinformation = MedicalRepresentative(name, surname, phoneno,address,birthdate)
            val user = firebaseAuth!!.currentUser
            databaseReference!!.child("Users").child(user.uid).setValue(userinformation)
            //databaseReference!!.child(user.uid).setValue(userinformation)
            etUserSurname.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }
        val dialog = alert.create()
        dialog.show()
    }*/


    private fun buttonClickedEditPhoneNo() {
        val inflater = layoutInflater
        val alertLayout: View =
            inflater.inflate(R.layout.edit_phone, null)
        val etUserPhoneno = alertLayout.findViewById<EditText>(R.id.et_userPhone)
        val alert =
            AlertDialog.Builder(requireContext())
        alert.setTitle("Phone No Edit")
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout)
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false)
        alert.setNegativeButton(
            "Cancel"
        ) { _, _ -> }
        alert.setPositiveButton(
            "OK"
        ) { _, _ ->
            val phoneno = etUserPhoneno.text.toString()
            val user = firebaseAuth!!.currentUser
            databaseReference!!.child("Users").child(user.uid).child("phoneno").setValue(phoneno)
            //databaseReference!!.child(user.uid).setValue(userinformation)
            etUserPhoneno.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }
        val dialog = alert.create()
        dialog.show()
    }

    private fun navigateLogOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        //finish()
    }

}