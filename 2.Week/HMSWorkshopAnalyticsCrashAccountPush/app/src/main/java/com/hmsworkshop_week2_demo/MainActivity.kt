package com.hmsworkshop_week2_demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import com.huawei.hms.analytics.type.HAEventType
import com.huawei.hms.support.account.AccountAuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var  signOutButton : Button
    private lateinit var  revokeAuthButton : Button
    private lateinit var  displayNameTextView: TextView
    private lateinit var analyticsInstance: HiAnalyticsInstance
    private lateinit var displayName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        signOutButton = findViewById(R.id.buttonSignOut);
        revokeAuthButton = findViewById(R.id.buttonRevokeAuth);
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
            }
        }

    }

    //Report a Custom Event
    private fun createRevokeAuthEvent() {
        val bundle = Bundle()
        bundle.putString("UserName", displayName)
        // Report a predefined Event
        analyticsInstance.onEvent("revokeAuth", bundle)
    }



    //Report a Predefined Event
    private fun createCompleteTutorialEvent() {
        val bundle = Bundle()
        bundle.putString("TutorialName", "HMS_Workshop_Week2")
        analyticsInstance.onEvent(HAEventType.COMPLETETUTORIAL, bundle)
    }


    private fun redirectToLoginActivity()
    {
        val mainActivityIntent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }
}