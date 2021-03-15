package com.hmsworkshop_week2_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.agconnect.crash.AGConnectCrash
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import com.huawei.hms.analytics.type.HAEventType
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager


class MainActivity : AppCompatActivity() {

    private lateinit var  signOutButton : Button
    private lateinit var  revokeAuthButton : Button
    private lateinit var  crashButton : Button
    private lateinit var  displayNameTextView: TextView
    private lateinit var analyticsInstance: HiAnalyticsInstance
    private lateinit var displayName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        signOutButton = findViewById(R.id.buttonSignOut);
        revokeAuthButton = findViewById(R.id.buttonRevokeAuth);
        crashButton = findViewById(R.id.buttonCrash);
        displayNameTextView = findViewById(R.id.displayNameTV);

        //Enabling Analytics
        HiAnalyticsTools.enableLog();
        // Generate the Analytics Instance
        analyticsInstance = HiAnalytics.getInstance(this);



        displayName = AccountAuthManager.getAuthResult().displayName
        displayNameTextView.text = displayName


        signOutButton.setOnClickListener {
            AccountHelper.instance.signOut(this)
            {
                if (it) redirectToLoginActivity()
            }
        }

        revokeAuthButton.setOnClickListener {
            AccountHelper.instance.revokeAuthorization(this)
            {
                createCompleteTutorialEvent()
                createRevokeAuthEvent()
                setUserProfile()
            }
        }

        crashButton.setOnClickListener {
            //divideByZeroCrash()
            crashServiceTestCrash()
        }

    }

    //Report a Custom Event
    private fun createRevokeAuthEvent() {
        val bundle = Bundle()
        bundle.putString("UserName", displayName)
        // Report a predefined Event
        analyticsInstance.onEvent("revokeAuth", bundle)
    }

    private fun setUserProfile()
    {
        analyticsInstance.setUserProfile("FavoriteCategory", "Electronics");
    }


    //Report a Predefined Event
    private fun createCompleteTutorialEvent() {
        val bundle = Bundle()
        bundle.putString("TutorialName", "HMS_Workshop_Week2")
        analyticsInstance.onEvent(HAEventType.COMPLETETUTORIAL, bundle)
    }

    private fun divideByZeroCrash(): Int
    {
       var x : Int = 13/0

        return x
    }



    private fun crashServiceTestCrash()
    {
        AGConnectCrash.getInstance().testIt();
    }

    private fun redirectToLoginActivity()
    {
        val mainActivityIntent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }
}