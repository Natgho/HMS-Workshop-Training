package com.hmsworkshop_week2_demo

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.request.AccountAuthParams
import com.huawei.hms.support.account.request.AccountAuthParamsHelper

class AccountHelper private constructor(){


    private object HOLDER {
        val INSTANCE = AccountHelper()
    }

    companion object {
        val instance: AccountHelper by lazy { HOLDER.INSTANCE }
    }


    public fun authorizationCodeSignIn(activity: AppCompatActivity)
    {
        val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams()
        val service = AccountAuthManager.getService(activity, authParams)
        activity.startActivityForResult(service.signInIntent, 8888)
    }

    public fun idTokenSignIn(activity: AppCompatActivity)
    {
        val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams()
        val service = AccountAuthManager.getService(activity, authParams)
        activity.startActivityForResult(service.signInIntent, 9999)
    }


    public fun silentSignIn(activity: AppCompatActivity, result: (Boolean) -> Unit )
    {
        val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service = AccountAuthManager.getService(activity, authParams)

        val task = service.silentSignIn()

        task.addOnSuccessListener {
            Log.i("Account Kit", "Silent Sign In Successful")
            result(true)

        }

        task.addOnFailureListener {
            Log.i("Account Kit", "Silent Sign In Failed. Reason: "+ it.message)
            result(false)
        }
    }

    public fun signOut(activity: AppCompatActivity, result: (Boolean) -> Unit)
    {

        val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service = AccountAuthManager.getService(activity, authParams)

        val task = service.signOut()

        task.addOnCompleteListener {
            Log.i("Account Kit", "Sign Out Successful")
            result(true)
        }

        task.addOnFailureListener {
            Log.i("Account Kit", "Sign Out Failed. Reason: "+it.message)
            result(false)
        }

    }

    public fun revokeAuthorization(activity: AppCompatActivity, result: (Boolean) -> Unit)
    {

        val authParams = AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
        val service = AccountAuthManager.getService(activity, authParams)

        val task = service.cancelAuthorization()

        task.addOnCompleteListener {
            Log.i("Account Kit", "revokeAuthorization Successful")
            result(true)
        }

        task.addOnFailureListener {
            Log.i("Account Kit", "revokeAuthorization Failed. Reason: "+it.message)
            result(false)
        }

    }

}