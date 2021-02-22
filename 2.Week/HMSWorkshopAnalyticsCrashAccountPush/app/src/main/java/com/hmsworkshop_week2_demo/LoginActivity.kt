package com.hmsworkshop_week2_demo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import com.huawei.hms.analytics.type.HAEventType
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton

class LoginActivity  : AppCompatActivity() {

    private lateinit var  loginButton : HuaweiIdAuthButton
    private lateinit var analyticsInstance: HiAnalyticsInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton = findViewById(R.id.buttonLogin);

        //Enabling Analytics
        HiAnalyticsTools.enableLog();
        // Generate the Analytics Instance
        analyticsInstance = HiAnalytics.getInstance(this);
        analyticsInstance.setAnalyticsEnabled(true)

        //Fire a start tutorial event
        createStartTutorialEvent()


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
                Log.e("AccountKit","Sign in failed. Exception Message: "+authAccountTask.exception.message )
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
                Log.e("AccountKit","Sign in failed. Exception Message: "+authAccountTask.exception.message )
            }
        }
    }


    //Report a Predefined Event
    private fun createStartTutorialEvent() {
        val bundle = Bundle()
        bundle.putString("TutorialName", "HMS_Workshop_Week2")
        analyticsInstance.onEvent(HAEventType.STARTTUTORIAL, bundle)
    }


    private fun redirectToMainActivity()
    {
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }
}