package com.hmsworkshop_week2_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.support.account.AccountAuthManager

class LoginActivity  : AppCompatActivity() {

    private lateinit var  loginButton : com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener {
            //authorizationCodeSignIn()
            AccountHelper.instance.idTokenSignIn(this)
        }

        AccountHelper.instance.silentSignIn(activity = this,result =
        { result-> Boolean

            if(result)
            {
                redirectToMainActivity()
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 8888)
        {
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful)
            {

                //it is safe to go to the MainActivity
                redirectToMainActivity()
            }
            else
            {
                // Sign in failed
                Log.e("Account Kit","Sign in failed. Exception Message: "+authAccountTask.exception.message )
            }
        }
        else if(requestCode == 9999)
        {
            val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data)
            if (authAccountTask.isSuccessful)
            {

                //it is safe to go to the MainActivity
                redirectToMainActivity()
            }
            else
            {
                // Sign in failed
                Log.e("Account Kit","Sign in failed. Exception Message: "+authAccountTask.exception.message )
            }
        }
    }





    private fun redirectToMainActivity()
    {
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }
}