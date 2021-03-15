package com.enesinky.demo.audiotranscription

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.speechrtt.*
import java.util.*

class RealTimeActivity : AppCompatActivity() {

    private val logTag = "audio-transcription"
    private var rttEngine: MLSpeechRealTimeTranscription? = null
    private lateinit var rttConfig: MLSpeechRealTimeTranscriptionConfig
    private var isListening = false
    private lateinit var resultText: TextView
    private lateinit var textHolder: TextView
    private var isPermissionGranted: Boolean = false
    private lateinit var button: Button
    private lateinit var clearButton: TextView
    private lateinit var scrollView: ScrollView
    private val console = Console()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time)

        //set HMS ML Kit API KEY
        MLApplication.getInstance().apiKey = "CgB6e3x9CHqu5EMNL3apkna/Ob7gS6dxMLT926TDV2MAB77MJso4Qfx6v0sN5VGG3Lt+CcA1Csf7sJFzWebo6TQ3"

        // List of supported languages by Jan-2021
        val languages = arrayOf(
            MLSpeechRealTimeTranscriptionConstants.LAN_EN_US,
            MLSpeechRealTimeTranscriptionConstants.LAN_ZH_CN,
            MLSpeechRealTimeTranscriptionConstants.LAN_FR_FR
        )

        // Configure Engine
        rttConfig = MLSpeechRealTimeTranscriptionConfig.Factory()
                .setLanguage(languages[0])
                .enablePunctuation(true)
                .enableSentenceTimeOffset(true)
                .enableWordTimeOffset(false)
                .create()

        resultText = findViewById(R.id.result)
        textHolder = findViewById(R.id.textView)
        button = findViewById(R.id.button)
        clearButton = findViewById(R.id.clearBtn)
        scrollView = findViewById(R.id.scrollView)

        // Build Console
        console.build(scrollView, this)


        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    isPermissionGranted = true
                } else {
                    console.print("Microphone permission is not granted.")
                }
            }


        if(ContextCompat.checkSelfPermission(
                this,
                Constants.RTT_PERMISSION_LIST[0]
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Constants.RTT_PERMISSION_LIST[0])
        }
        else {
            isPermissionGranted = true
        }

        button.setOnClickListener {
            when(isListening) {
                false -> {
                    if(isPermissionGranted && isPhoneHuaweiBrand()) {
                        startRttEngine()
                        console.print("Engine start.")
                    }
                    else {
                        console.print("Engine cannot be started without record audio permission.")
                    }
                }
                true -> {
                    stopRttEngine()
                    console.print("Engine stop.")
                }
            }

        }

        clearButton.setOnClickListener {
            console.clear()
        }

    }

    private fun startRttEngine() {
        rttEngine = MLSpeechRealTimeTranscription.getInstance().apply {
            setRealTimeTranscriptionListener(rttListener)
            startRecognizing(rttConfig)
        }.also {
            button.isEnabled = false
            button.text = this.resources.getString(R.string.stop)
        }
    }

    private fun stopRttEngine() {
        rttEngine?.destroy()
        isListening = false
        resetButton()
    }

    private fun resetButton() {
        button.isEnabled = true
        button.text = this.resources.getString(R.string.start)
        isListening = false
    }


    private fun isPhoneHuaweiBrand() : Boolean {
        return (Constants.MANUFACTURER_BRAND == "HUAWEI" || Constants.MANUFACTURER_BRAND == "HONOR")
    }

    private val rttListener: MLSpeechRealTimeTranscriptionListener =
        object : MLSpeechRealTimeTranscriptionListener {

            override fun onStartListening() {
                // The recorder starts to receive speech.
                console.print("Listening... (${rttConfig.language})")
                Log.d(logTag, "onStartListening: (${rttConfig.language})")
                button.isEnabled = true
                isListening = true
            }

            override fun onStartingOfSpeech() {
                // The user starts to speak, that is, the speech recognizer detects that the user starts to speak.
            }

            override fun onVoiceDataReceived(data: ByteArray?, energy: Float, bundle: Bundle?) {
                // Return the original PCM stream and audio power to the user. This API is not running in the main thread, and the return result is processed in the sub-thread.
            }

            override fun onRecognizingResults(partialResults: Bundle?) {

                // get sentence results with time offsets
                val results: ArrayList<MLSpeechRealTimeTranscriptionResult> =
                    partialResults?.getSerializable(
                        "RESULTS_SENTENCE_OFFSET"
                    ) as ArrayList<MLSpeechRealTimeTranscriptionResult>

                // refresh dynamic text
                resultText.text = partialResults.getString("results_recognizing")!!

                results.forEach {
                    if (partialResults.getBoolean("RESULTS_PARTIALFINAL")) {
                        val sT = TimestampConverter()
                            .setDuration(it.endTime.toInt())
                            .setPosition(it.startTime.toInt())
                            .getString()

                        console.print("$sT: ${it.text}")
                        Log.d(
                            logTag,
                            "onResult: $sT: ${it.text}"
                        )

                        // clear dynamic text
                        resultText.text = ""
                    }
                }
            }

            override fun onError(error: Int, errorMessage: String?) {
                // Called when an error occurs in recognition.
                Log.d(logTag, "onError: message=$errorMessage")
                console.print("Error: $errorMessage ($error)")
                resetButton()
            }

            override fun onState(state: Int, params: Bundle?) {
                // Notify the app status change.
                Log.d(logTag, "onState: $state")
            }

        }

}