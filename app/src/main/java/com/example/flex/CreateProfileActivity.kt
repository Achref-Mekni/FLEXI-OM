package com.example.flex

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.flex.Users.MedicalRepresentative
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.create_profile_activity.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreateProfileActivity: AppCompatActivity() {
    private val TAG = CreateProfileActivity::class.java!!.simpleName
    private lateinit var btnSave: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var textViewemailname: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var editTextName: EditText
    private lateinit var editTextSurname:EditText
    private lateinit var editTextPhoneNo:EditText
    private lateinit var editTextAddress:EditText
    private lateinit var editTextBirth:TextView
    private lateinit var profileImageView: ImageView
    private lateinit var firebaseStorage:FirebaseStorage

    private val PICK_IMAGE = 123
    private  var imagePath: Uri? = null
    private lateinit var storageReference:StorageReference
    private var textview_date: TextView? = null
    var cal: Calendar = Calendar.getInstance()
    var button_date: Button? = null

    fun EditProfileActivity() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data!!.data != null)
        {
            imagePath = data.data!!
            try
            {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
                profileImageView.setImageBitmap(bitmap)
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_profile_activity)
        // get the references from layout file
        textview_date = this.text_view_date_1
        button_date = this.button_date_1

        textview_date!!.text = "--/--/----"

        // create an OnDateSetListener
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener {
            DatePickerDialog(this@CreateProfileActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null)
        {
            finish()
            startActivity(Intent(applicationContext, RegisterActivity::class.java))

        }
        databaseReference = Firebase.database.reference
        editTextName = findViewById<EditText>(R.id.EditTextName)
        editTextSurname = findViewById<EditText>(R.id.EditTextSurname)
        editTextPhoneNo = findViewById<EditText>(R.id.EditTextPhoneNo)
        editTextAddress = findViewById<EditText>(R.id.EditTextAddress)
        editTextBirth = findViewById<TextView>(R.id.text_view_date_1)
        btnSave = findViewById<Button>(R.id.btnSaveButton)
        val user = firebaseAuth.currentUser
        btnSave.setOnClickListener{
            if (imagePath == null) {
                //val drawable = this.resources.getDrawable(R.drawable.defavatar)
                //val bitmap = BitmapFactory.decodeResource(resources, R.drawable.defavatar)
                // openSelectProfilePictureDialog();
                userInformation()
                // sendUserData()
                startActivity(Intent(this@CreateProfileActivity, HomeActivity::class.java))
                //finish()
            } else {
                userInformation()
                sendUserData()
                startActivity(Intent(this@CreateProfileActivity, HomeActivity::class.java))
                //finish()
            }
        }
        textViewemailname = findViewById<TextView>(R.id.textViewEmailAdress)
        textViewemailname.text = user.email
        profileImageView = findViewById(R.id.update_imageView)
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference

        profileImageView.setOnClickListener {
            val profileIntent = Intent()
            profileIntent.type = "image/*"
            profileIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(profileIntent, "Select Image."), PICK_IMAGE)
        }
    }

    private fun userInformation() {
        val name = editTextName.text.toString().trim()
        val surname = editTextSurname.text.toString().trim()
        val phoneno = editTextPhoneNo.text.toString().trim()
        val address = editTextAddress.text.toString().trim()
        val birthdate = editTextBirth.text.toString().trim()
        val delegate = MedicalRepresentative(name, surname, phoneno,address,birthdate)
        val user = firebaseAuth.currentUser
        //print(user.uid)
        databaseReference.child("Users").child(user.uid).setValue(delegate)
        Toast.makeText(applicationContext, "User information updated", Toast.LENGTH_LONG).show()
    }

    /*override fun onClick(view: View) {
        if (view === btnSave) {
            if (imagePath == null) {
                val drawable = this.resources.getDrawable(R.drawable.defavatar)
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.defavatar)
                // openSelectProfilePictureDialog();
                userInformation()
                // sendUserData()
                startActivity(Intent(this@CreateProfileActivity, HomeActivity::class.java))
                finish()
            } else {
                userInformation()
                sendUserData()
                startActivity(Intent(this@CreateProfileActivity, HomeActivity::class.java))
                finish()
            }
        }
    }*/

    private fun sendUserData() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        // Get "User UID" from Firebase > Authentification > Users.
        //val databaseReference = firebaseDatabase.getReference(firebaseAuth.uid!!)
        val imageReference = storageReference.child(firebaseAuth.uid!!).child("Images").child("Profile Pic.jpg") //User id/Images/Profile Pic.jpg
        val uploadTask = imageReference.putFile(imagePath!!)
        uploadTask.addOnFailureListener { Toast.makeText(this@CreateProfileActivity, "Error: Uploading profile picture", Toast.LENGTH_SHORT).show() }.addOnSuccessListener { Toast.makeText(this@CreateProfileActivity, "Profile picture uploaded", Toast.LENGTH_SHORT).show() }
    }

    fun openSelectProfilePictureDialog() {
        val alertDialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(this).create()
        val title = TextView(this)
        title.text = "Profile Picture"
        title.setPadding(10, 10, 10, 10) // Set Position
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)
        title.textSize = 20f
        alertDialog!!.setCustomTitle(title)
        val msg = TextView(this)
        msg.text = "Please select a profile picture \n Tap the sample avatar logo"
        msg.gravity = Gravity.CENTER_HORIZONTAL
        msg.setTextColor(Color.BLACK)
        alertDialog.setView(msg)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", DialogInterface.OnClickListener { _, _ ->
            // Perform Action on Button
        })
        Dialog(applicationContext)
        alertDialog.show()
        val okBT: Button = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
        val neutralBtnLP = okBT.layoutParams as LinearLayout.LayoutParams
        neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL
        okBT.setPadding(50, 10, 10, 10) // Set Position
        okBT.setTextColor(Color.BLUE)
        okBT.layoutParams = neutralBtnLP
    }
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }

}