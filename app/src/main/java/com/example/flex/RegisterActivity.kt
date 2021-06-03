package com.example.flex

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbar = findViewById(R.id.toolbar_reg)
        setSupportActionBar(toolbar)

        register_button.setOnClickListener {
            Toast.makeText(this@RegisterActivity, "You clicked Sign Up", Toast.LENGTH_SHORT).show()
            RegisterUser()

        }


        //set email/user/pass field icons
        email_reg.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0)
        password_reg.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pass_icon, 0, 0, 0)

        //pass the function that lights up the button when everything is written to them
        email_reg.addTextChangedListener(RegisterTextWatcher)
        password_reg.addTextChangedListener(RegisterTextWatcher)


        email_reg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
            {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {

            }


            override fun afterTextChanged(s: Editable)
            {
                if (s.length != 0)
                {
                    var drawable = resources.getDrawable(R.drawable.ic_email) //Your drawable image
                    drawable = DrawableCompat.wrap(drawable!!)
                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colordarkblue)) // Set whatever color you want
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                    email_reg.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    email_reg.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_email),
                        null, resources.getDrawable(R.drawable.ic_cancel_icon), null)
                }
                else if (s.length == 0)
                {
                    email_reg.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_email,
                        0, 0, 0)
                    var drawable = resources.getDrawable(R.drawable.ic_email) //Your drawable image
                    drawable = DrawableCompat.wrap(drawable!!)
                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colorDefault)) // Set whatever color you want
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                    email_reg.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    email_reg.setCompoundDrawablesWithIntrinsicBounds(
                        resources.getDrawable(R.drawable.ic_email),
                        null, null, null
                    )
                }

            }
        })

        password_reg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {

            }

            override fun afterTextChanged(s: Editable)
            {
                if (s.length != 0)
                {
                    var drawable = resources.getDrawable(R.drawable.ic_pass_icon) //Your drawable image
                    drawable = DrawableCompat.wrap(drawable!!)
                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colordarkblue)) // Set whatever color you want
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                    password_reg.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    password_reg.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_pass_icon),
                        null, resources.getDrawable(R.drawable.ic_cancel_icon), null)
                }
                else if (s.length == 0)
                {
                    password_reg.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pass_icon,
                        0, 0, 0)
                    var drawable = resources.getDrawable(R.drawable.ic_pass_icon) //Your drawable image
                    drawable = DrawableCompat.wrap(drawable!!)
                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colorDefault)) // Set whatever color you want
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                    password_reg.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    password_reg.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_pass_icon),
                        null, null, null
                    )
                }

            }
        })



        email_reg.setOnTouchListener(View.OnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN)
            {
                if (email_reg.getCompoundDrawables().get(2) != null)
                {
                    if (event.x >= email_reg.getRight() - email_reg.getLeft() -
                        email_reg.getCompoundDrawables().get(2).getBounds().width())
                    {
                        if (email_reg.getText().toString() != "")
                        {
                            email_reg.setText("")
                        }
                    }
                }
            }
            false
        })

        password_reg.setOnTouchListener(View.OnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN)
            {
                if (password_reg.getCompoundDrawables().get(2) != null)
                {
                    if (event.x >= password_reg.getRight() - password_reg.getLeft() -
                        password_reg.getCompoundDrawables().get(2).getBounds().width()
                    )
                    {
                        if (password_reg.getText().toString() != "")
                        {
                            password_reg.setText("")
                        }
                    }
                }
            }
            false
        })

    }

    private val RegisterTextWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
        {


        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
        {


        }

        override fun afterTextChanged(s: Editable)
        {
            val mEmail: String = email_reg.getText().toString().trim()
            val mPassword: String = password_reg.getText().toString().trim()
            val t = !mPassword.isEmpty() && !mEmail.isEmpty()
            if (t)
            {
                register_button.setBackgroundResource(R.color.colordarkblue)
                register_button.isEnabled = true
            }
            else
            {
                register_button.setBackgroundResource(R.color.colorwhiteblueshade)
            }

        }
    }
    override fun onStart()
    {
        super.onStart()
        val mEmail: String = email_reg.getText().toString().trim()
        val mPassword: String = password_reg.getText().toString().trim()
        val t = !mPassword.isEmpty() && !mEmail.isEmpty()
        if (t)
        {
            register_button.setBackgroundResource(R.color.colordarkblue)
            register_button.isEnabled = true

        }
        else
        {
            register_button.setBackgroundResource(R.color.colorwhiteblueshade)
        }
    }

    private fun RegisterUser() {
        auth = FirebaseAuth.getInstance()
        val email_txt = email_reg.text.toString()
        val password_txt = password_reg.text.toString()

        if (TextUtils.isEmpty(email_txt) && TextUtils.isEmpty(password_txt)) {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()

        } else {
            auth.createUserWithEmailAndPassword(
                email_reg.text.toString(), password_reg.text.toString()
            ).addOnSuccessListener {
                Toast.makeText(this@RegisterActivity,
                    "Registration OK",
                    Toast.LENGTH_LONG).show()
                updateUI()
            }.addOnFailureListener{
                Toast.makeText(this@RegisterActivity,
                    "Error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
            /*auth!!.createUserWithEmailAndPassword(email_txt, password_txt)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser

                            Toast.makeText(this@RegisterActivity, "Register success",
                                    Toast.LENGTH_SHORT).show()


                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            val e = task.exception as FirebaseAuthException?
                            Toast.makeText(this@RegisterActivity, "Failed Registration: " + e!!.message, Toast.LENGTH_SHORT).show()

                        }
                    }*/
        }
    }

    fun updateUI() {
        val intent = Intent(this@RegisterActivity, CreateProfileActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}