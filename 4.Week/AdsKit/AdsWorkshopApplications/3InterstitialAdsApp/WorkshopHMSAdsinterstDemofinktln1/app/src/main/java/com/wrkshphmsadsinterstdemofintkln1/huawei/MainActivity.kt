package com.wrkshphmsadsinterstdemofintkln1.huawei

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.InterstitialAd

class MainActivity : AppCompatActivity() {
    private var displayRadioGroup: RadioGroup? = null
    private var loadAdButton: Button? = null
    private var interstitialAd: InterstitialAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Initialize HUAWEI Ads SDK
        HwAds.init(this)

        displayRadioGroup = findViewById(R.id.display_radio_group)

        loadAdButton = findViewById(R.id.load_ad)

        loadAdButton!!.setOnClickListener { loadInterstitialAd() }
    }

    private fun loadInterstitialAd() {
        interstitialAd = InterstitialAd(this)
        interstitialAd!!.adId = getAdId()
        interstitialAd!!.adListener = adListener
        val adParam = AdParam.Builder().build()
        interstitialAd!!.loadAd(adParam)
    }

    private fun getAdId(): String {
        if (displayRadioGroup!!.checkedRadioButtonId == R.id.display_image) {
            return getString(R.string.image_ad_id)
        } else {
            return getString(R.string.video_ad_id)
        }
    }

    private val adListener: AdListener = object: AdListener(){
        override fun onAdLoaded() {
           Toast.makeText(this@MainActivity,"Ad loaded",Toast.LENGTH_LONG).show()
            showInterstatial()
        }

        override fun onAdFailed(p0: Int) {
            Toast.makeText(this@MainActivity,"Ad failed to load",Toast.LENGTH_LONG).show()
        }

        override fun onAdClicked() {
            Toast.makeText(this@MainActivity,"Ad clicked",Toast.LENGTH_LONG).show()
        }
    }
    private fun showInterstatial(){
        if(interstitialAd != null && interstitialAd!!.isLoaded){
            interstitialAd!!.show()
        }else{
            Toast.makeText(this,"Ad failed to load,null", Toast.LENGTH_LONG).show()
        }
    }

}

