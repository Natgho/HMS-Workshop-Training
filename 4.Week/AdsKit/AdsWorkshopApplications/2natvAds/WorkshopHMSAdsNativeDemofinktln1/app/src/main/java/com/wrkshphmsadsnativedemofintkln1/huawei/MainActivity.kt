package com.wrkshphmsadsnativedemofintkln1.huawei

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.VideoOperator
import com.huawei.hms.ads.nativead.*

class MainActivity : AppCompatActivity() {
    private var small: RadioButton? = null
    private var video: RadioButton? = null
    private var loadBtn: Button? = null
    private var adScrollView: ScrollView? = null
    private var layoutId = 0
    private var globalNativeAd: NativeAd? = null
    private val videoLifecycleListener: VideoOperator.VideoLifecycleListener = object : VideoOperator.VideoLifecycleListener() {
        override fun onVideoStart() {
            //updateStatus(getString(com.huawei.hms.ads.nativead.R.string.status_play_start), false)
            Toast.makeText(this@MainActivity,getString(R.string.status_play_start),Toast.LENGTH_LONG).show()
        }

        override fun onVideoPlay() {
            //updateStatus(getString(com.huawei.hms.ads.nativead.R.string.status_playing), false)
            Toast.makeText(this@MainActivity,getString(R.string.status_playing),Toast.LENGTH_LONG).show()
        }

        override fun onVideoEnd() {
            // If there is a video, load a new native ad only after video playback is complete.
            //updateStatus(getString(com.huawei.hms.ads.nativead.R.string.status_play_end), true)
            Toast.makeText(this@MainActivity,getString(R.string.status_play_end),Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HwAds.init(this)
        small = findViewById(R.id.radio_button_small)
        video = findViewById(R.id.radio_button_video)
        loadBtn = findViewById(R.id.btn_load)
        adScrollView = findViewById(R.id.scroll_view_ad)
        loadBtn!!.setOnClickListener(View.OnClickListener { loadAd(adId) })
        loadAd(adId)
    }

    private val adId: String
        private get() {
            val adId: String
            layoutId = R.layout.native_video_template
            if (small!!.isChecked) {
                adId = getString(R.string.ad_id_native_small)
                layoutId = R.layout.native_small_template
            } else if (video!!.isChecked) {
                adId = getString(R.string.ad_id_native_video)
            } else {
                adId = getString(R.string.ad_id_native)
            }
            return adId
        }

    private fun loadAd(adId: String){
        val builder = NativeAdLoader.Builder(this,adId)
        builder.setNativeAdLoadedListener {nativeAd ->
            Toast.makeText(this@MainActivity,"Ad loaded succesfully",Toast.LENGTH_LONG).show()
            showNativeAd(nativeAd)
        }.setAdListener(object: AdListener(){
            override fun onAdFailed(p0: Int) {
                Toast.makeText(this@MainActivity,"Ad failed with errorcode: $p0",Toast.LENGTH_LONG).show()
            }
        })
        val adConfiguration = NativeAdConfiguration.Builder().setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT)
            .build()
        val nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build()
        nativeAdLoader.loadAd(AdParam.Builder().build())
        Toast.makeText(this@MainActivity, R.string.status_ad_loading,Toast.LENGTH_LONG).show()
    }
    private fun showNativeAd(nativeAd: NativeAd){
        // Destroy the original native ad.
        if (null != globalNativeAd) {
            globalNativeAd!!.destroy()
        }
        globalNativeAd = nativeAd
        val nativeView = layoutInflater.inflate(layoutId, null) as NativeView
        nativeView.titleView = nativeView.findViewById(R.id.ad_title)
        nativeView.mediaView = nativeView.findViewById<View>(R.id.ad_media) as MediaView
        nativeView.adSourceView = nativeView.findViewById(R.id.ad_source)
        nativeView.callToActionView = nativeView.findViewById(R.id.ad_call_to_action)

        (nativeView.titleView as TextView).text = globalNativeAd!!.title
        nativeView.mediaView.setMediaContent(globalNativeAd?.mediaContent)
        if (null != globalNativeAd?.adSource) {
            (nativeView.adSourceView as TextView).text = globalNativeAd?.adSource
        }
        nativeView.adSourceView.visibility = if (null != globalNativeAd?.adSource) View.VISIBLE else View.INVISIBLE
        if (null != globalNativeAd?.callToAction) {
            (nativeView.callToActionView as Button).text = globalNativeAd?.callToAction
        }
        nativeView.callToActionView.visibility = if (null != globalNativeAd?.callToAction) View.VISIBLE else View.INVISIBLE
        // Obtain a video controller.
        val videoOperator = globalNativeAd?.videoOperator

        // Check whether a native ad contains video materials.
        if (videoOperator!!.hasVideo()) {
            // Add a video lifecycle event listener.
            videoOperator.videoLifecycleListener = videoLifecycleListener
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd)
        // Add NativeView to the app UI.
        adScrollView!!.removeAllViews()
        adScrollView!!.addView(nativeView)
    }
    override fun onDestroy() {
        if (null != globalNativeAd) {
            globalNativeAd!!.destroy()
        }
        super.onDestroy()
    }


}