package com.example.flex

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import com.example.flex.Tasks.Task
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

    companion object{
        var counter_TODO: Int = 0
        var counter_MISSED: Int = 0
    }
    private lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth
    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)


        email.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0)
        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pass_icon, 0, 0, 0)


        email.addTextChangedListener(loginTextWatcher)
        password.addTextChangedListener(loginTextWatcher)

        login_button.setOnClickListener {
            Toast.makeText(this@LoginActivity, "You clicked login.", Toast.LENGTH_SHORT).show()
            loginUser()
            countTasks()
        }

        register_button.setOnClickListener {
            Toast.makeText(this@LoginActivity, "You clicked Register.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }


        email.addTextChangedListener(object : TextWatcher {
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
                    email.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    email.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_email),
                        null, resources.getDrawable(R.drawable.ic_cancel_icon), null)
                }
                else if (s.length == 0)
                {
                    email.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_email,
                        0, 0, 0)
                    var drawable = resources.getDrawable(R.drawable.ic_email) //Your drawable image
                    drawable = DrawableCompat.wrap(drawable!!)
                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colorDefault)) // Set whatever color you want
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                    email.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    email.setCompoundDrawablesWithIntrinsicBounds(
                        resources.getDrawable(R.drawable.ic_email),
                        null, null, null
                    )
                }
            }
        })

        password.addTextChangedListener(object : TextWatcher {
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
                    password.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    password.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_pass_icon),
                        null, resources.getDrawable(R.drawable.ic_cancel_icon), null)
                }
                else if (s.length == 0)
                {
                    password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pass_icon,
                        0, 0, 0)
                    var drawable = resources.getDrawable(R.drawable.ic_pass_icon) //Your drawable image
                    drawable = DrawableCompat.wrap(drawable!!)
                    DrawableCompat.setTint(drawable, resources.getColor(R.color.colorDefault)) // Set whatever color you want
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                    password.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    password.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.drawable.ic_pass_icon),
                        null, null, null
                    )
                }
            }
        })

        email.setOnTouchListener(View.OnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN)
            {
                if (email.getCompoundDrawables().get(2) != null)
                {
                    if (event.x >= email.getRight() - email.getLeft() -
                        email.getCompoundDrawables().get(2).getBounds().width())
                    {
                        if (email.getText().toString() != "")
                        {
                            email.setText("")
                        }
                    }
                }
            }
            false
        })

        password.setOnTouchListener(View.OnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN)
            {
                if (password.getCompoundDrawables().get(2) != null)
                {
                    if (event.x >= password.getRight() - password.getLeft() -
                        password.getCompoundDrawables().get(2).getBounds().width()
                    )
                    {
                        if (password.getText().toString() != "")
                        {
                            password.setText("")
                        }
                    }
                }
            }
            false
        })

        remember_password.setOnClickListener(View.OnClickListener {

            if (!(remember_password.isSelected)) {
                remember_password.isChecked = true
                remember_password.isSelected = true
            } else {
                remember_password.isChecked = false
                remember_password.isSelected = false
            }
        })
    }

    private val loginTextWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int)
        {


        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
        {


        }

        override fun afterTextChanged(s: Editable)
        {
            val mUsername: String = email.getText().toString().trim()
            val mPassword: String = password.getText().toString().trim()
            val t = !mUsername.isEmpty() && !mPassword.isEmpty()
            if (t)
            {
                login_button.setBackgroundResource(R.color.colordarkblue)
                login_button.isEnabled = true
            }
            else
            {
                login_button.setBackgroundResource(R.color.colorwhiteblueshade)
            }

        }
    }

    override fun onStart()
    {
        super.onStart()
        val mUsername: String = email.getText().toString().trim()
        val mPassword: String = password.getText().toString().trim()
        val t = !mUsername.isEmpty() && !mPassword.isEmpty()
        if (t)
        {
            login_button.setBackgroundResource(R.color.colordarkblue)
            login_button.isEnabled = true

        }
        else
        {
            login_button.setBackgroundResource(R.color.colorwhiteblueshade)
        }
    }
    private fun loginUser() {
        auth = FirebaseAuth.getInstance()
        val email_txt = email.text.toString()
        val password_txt = password.text.toString()
        if (TextUtils.isEmpty(email_txt) && TextUtils.isEmpty(password_txt)) {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()

        } else {
            auth.signInWithEmailAndPassword(
                email.text.toString(), password.text.toString()
            ).addOnSuccessListener {
                //firebaseDatabase = FirebaseDatabase.getInstance()
                val user = auth.currentUser
                databaseReference = Firebase.database.reference
                print("yes")
                databaseReference!!.child("Users").child(user.uid).get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value.toString()}")
                    var isAdmin = it.value.toString().contains("isAdmin")
                    if (isAdmin)
                    {
                        startActivity(Intent(this@LoginActivity, AdminActivity::class.java))
                        //finish()
                    }
                    else
                    {
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        //finish()
                    }
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }

            }.addOnFailureListener{
                Toast.makeText(this@LoginActivity,
                    "Login error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun countTasks()
    {
        val firebaseAuth = FirebaseAuth.getInstance()
        val databaseReference = Firebase.database.reference
        //val user = firebaseAuth!!.currentUser
        //Log.i("firebase", "User is"+user.uid)
        databaseReference.child("Tasks").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var taskList : MutableList<Task> = mutableListOf()

                for (ds in dataSnapshot.children) {
                    val task: Task? =
                        ds.getValue(Task::class.java)

                    Log.i("firebase", "Got value"+task!!.getTaskDescription())
                    if (task != null ) { //&& task.getTaskuid() == user.uid)
                        taskList.add(task)
                        //*** related to notfication part
                        //i changed deadline to tryout so i dont create new tasks
                        //+ Date( java.util.concurrent.TimeUnit.DAYS.toMillis(2)).time
                        var deadline = task.getTaskDeadline()!!.time
                        val before_deadline = deadline - Date( TimeUnit.DAYS.toMillis(2)).time
                        var currTime =  Date(System.currentTimeMillis()).time
                        if(currTime in before_deadline..deadline)
                        {
                            counter_TODO += 1
                        }
                        else if(currTime> deadline)
                        {
                            counter_MISSED += 1
                        }
                        //***
                    }



                }



            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity, databaseError.code, Toast.LENGTH_SHORT)
                    .show()
            }

        })


    }


}