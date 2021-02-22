package com.hmsworkshop_week2_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper

class LoginActivity  : AppCompatActivity() {

    private lateinit var  loginButton : com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton = findViewById(R.id.buttonLogin);


        loginButton.setOnClickListener {

            //authorizationCodeSignIn()
            idTokenSignIn()

        }

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


    private fun authorizationCodeSignIn()
    {
        val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams()
        val service = AccountAuthManager.getService(this, authParams)

        startActivityForResult(service.signInIntent, 8888)

    }

    private fun idTokenSignIn()
    {
        val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams()
        val service = AccountAuthManager.getService(this, authParams)
        startActivityForResult(service.signInIntent, 9999)
    }


    private fun redirectToMainActivity()
    {
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }
}