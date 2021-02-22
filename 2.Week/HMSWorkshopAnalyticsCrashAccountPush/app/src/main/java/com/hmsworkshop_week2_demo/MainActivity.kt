package com.hmsworkshop_week2_demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var  signOutButton : Button
    private lateinit var  revokeAuth : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signOutButton = findViewById(R.id.buttonSignOut);
        revokeAuth = findViewById(R.id.buttonRevokeAuth);

        signOutButton.setOnClickListener {
            AccountHelper.instance.signOut(this)
            {
                if (it) redirectToLoginActivity()
            }
        }

        revokeAuth.setOnClickListener {
            AccountHelper.instance.revokeAuthorization(this)
            {
            }
        }

    }


    private fun redirectToLoginActivity()
    {
        val mainActivityIntent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }
}