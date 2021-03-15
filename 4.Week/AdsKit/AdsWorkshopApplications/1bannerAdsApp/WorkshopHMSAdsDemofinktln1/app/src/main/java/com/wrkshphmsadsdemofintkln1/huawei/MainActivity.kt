package com.wrkshphmsadsdemofintkln1.huawei

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.banner.BannerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HwAds.init(this)

        var bannerView: BannerView? = findViewById(R.id.hw_banner_view)
        //bottom
        //bannerView!!.adId = "testw6vs28auh3"
        //bannerView!!.bannerAdSize = BannerAdSize.BANNER_SIZE_360_57
        bannerView!!.setBackgroundColor(Color.BLUE)
        bannerView!!.setBannerRefresh(40)

        val adParam = AdParam.Builder().build()
        bannerView!!.loadAd(adParam)
        //top
        var bannerView2: BannerView? = BannerView(this)
        bannerView2!!.adId = "testw6vs28auh3"
        bannerView2!!.bannerAdSize = BannerAdSize.BANNER_SIZE_SMART
        bannerView2!!.setBackgroundColor(Color.RED)
        bannerView2!!.setBannerRefresh(30)
        bannerView2!!.adListener = adListener
        val adParam2 = AdParam.Builder().build()
        bannerView2!!.loadAd(adParam2)
        var rootView: RelativeLayout  = findViewById(R.id.root_view)
        rootView.addView(bannerView2)


    }
    private val adListener: AdListener = object : AdListener(){
        override fun onAdLoaded() {
            Toast.makeText(applicationContext,"Ad Loaded! ",Toast.LENGTH_LONG).show()
        }

        override fun onAdFailed(p0: Int) {
            Toast.makeText(applicationContext,"Ad Failed! errorcode: $p0",Toast.LENGTH_LONG).show()
        }

        override fun onAdOpened() {
            // Called when an ad is clicked.
            Toast.makeText(applicationContext,"Ad Opened! ",Toast.LENGTH_LONG).show()
        }
        override fun onAdLeave() {
            // Called when an ad leaves an app.
            Toast.makeText(applicationContext,"Ad Leaved! ",Toast.LENGTH_LONG).show()
        }
        override fun onAdClicked() {
            // Called when an ad is clicked.
            Toast.makeText(applicationContext,"Ad Clicked! ",Toast.LENGTH_LONG).show()
        }
        override fun onAdClosed() {
            // Called when an ad is closed.
            Toast.makeText(applicationContext,"Ad Closed! ",Toast.LENGTH_LONG).show()
        }
    }
}