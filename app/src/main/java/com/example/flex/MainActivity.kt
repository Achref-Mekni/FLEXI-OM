package com.example.flex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity





class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)

    }
}