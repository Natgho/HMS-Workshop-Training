package com.hmsworkshop_week2_demo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity  : AppCompatActivity() {

    private lateinit var  loginButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton = findViewById(R.id.buttonLogin);

        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)

        loginButton.setOnClickListener {
            startActivity(mainActivityIntent)
            finish()
        }

    }
}