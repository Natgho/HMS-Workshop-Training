package com.hmsworkshop_week2_demo

import android.content.Context
import android.util.Log
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.huawei.hms.push.HmsMessaging
import kotlin.concurrent.thread

class PushTokenHelper private constructor(){


    private object HOLDER {
        val INSTANCE = PushTokenHelper()
    }

    companion object {
        val instance: PushTokenHelper by lazy { HOLDER.INSTANCE }
    }

    private val TAG = "PushKit"

    public fun getToken(context: Context){
        thread {
            try {
                val appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id")
                val token = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
                Log.d(TAG,"HMS Token -> $token")

            } catch (e: Exception) {
                Log.i(TAG, "getToken failed, $e")
            }
        }.run()
    }

    public fun deleteToken(context: Context)
    {
        object : Thread() {
            override fun run() {
                try {
                    // read from agconnect-services.json
                    val appId =
                        AGConnectServicesConfig.fromContext(context).getString("client/app_id")
                    HmsInstanceId.getInstance(context).deleteToken(appId, "HCM")
                    Log.i(TAG, "deleteToken success.")
                } catch (e: ApiException) {
                    Log.e(TAG, "deleteToken failed.$e")
                }
            }
        }.start()

    }

    public fun togglePush(state: Boolean, context: Context): Boolean
    {
        var result: Boolean = true
        if (state)
        {
            HmsMessaging.getInstance(context).turnOnPush()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "turnOnPush Complete")
                        result = true
                    } else {
                        Log.e(TAG, "turnOnPush failed: ret=" + task.exception.message)
                        result = false
                    }
                }
        }else
        {
            HmsMessaging.getInstance(context).turnOffPush()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, "turnOffPush Complete")
                        result = true
                    } else {
                        Log.e(TAG, "turnOffPush failed: ret=" + task.exception.message)
                        result = false
                    }
                }

        }


        return result
    }




}