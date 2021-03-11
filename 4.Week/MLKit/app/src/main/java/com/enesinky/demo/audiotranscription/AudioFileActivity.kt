package com.enesinky.demo.audiotranscription

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.huawei.hms.mlsdk.aft.MLAftConstants
import com.huawei.hms.mlsdk.aft.MLAftEvents
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftEngine
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftListener
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftResult
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftSetting
import com.huawei.hms.mlsdk.common.MLApplication
import java.util.*

class AudioFileActivity : AppCompatActivity() {

    private val logTag = "audio-transcription"
    private lateinit var aftConfig: MLRemoteAftSetting
    private lateinit var aftEngine: MLRemoteAftEngine
    private var waitResponseTimer: Timer? = null
    private var waitResponseTimerTask: TimerTask? = null
    private var retrieveResultTimer: Timer? = null
    private lateinit var textHolder: TextView
    private var isPermissionGranted: Boolean = false
    private lateinit var chooseFileButton: Button
    private lateinit var clearButton: TextView
    private lateinit var scrollView: ScrollView
    private var fileDuration = 0
    private val console = Console()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_file)

        //set HMS ML Kit API KEY
        MLApplication.getInstance().apiKey = "CgB6e3x9CHqu5EMNL3apkna/Ob7gS6dxMLT926TDV2MAB77MJso4Qfx6v0sN5VGG3Lt+CcA1Csf7sJFzWebo6TQ3"

        textHolder = findViewById(R.id.textView)
        chooseFileButton = findViewById(R.id.button)
        clearButton = findViewById(R.id.clearBtn)
        scrollView = findViewById(R.id.scrollView)

        // Build console
        console.build(scrollView, this)

        val aftLanguages = arrayOf(
                MLAftConstants.LANGUAGE_EN_US,
                MLAftConstants.LANGUAGE_ZH
        )

        aftConfig = MLRemoteAftSetting.Factory()
                .setLanguageCode(aftLanguages[0])
                .enableSentenceTimeOffset(true)
                .enableWordTimeOffset(false)
                .enablePunctuation(true)
                .create()

        aftEngine = MLRemoteAftEngine.getInstance().apply {
            init(this@AudioFileActivity) // application context
            setAftListener(longAftListener) //set listener
        }


        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    isPermissionGranted = true
                } else {
                    console.print("Read Storage permission is not granted.")
                }
            }


        if(ContextCompat.checkSelfPermission(
                this,
                Constants.AT_PERMISSION_LIST[0]
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Constants.AT_PERMISSION_LIST[0])
        }
        else {
            isPermissionGranted = true
        }

        chooseFileButton.setOnClickListener {
            if(isPermissionGranted) {
                fileSelectorIntent()
            }
            else {
                console.print("File Explorer cannot be opened without permission.")
            }
        }

        clearButton.setOnClickListener {
            console.clear()
        }

    }


    private val fileSelectorForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            processSelectedFile(result.data)
        }
    }

    private val allowedMimeTypes = arrayOf(
            "audio/mpeg",
            "audio/wav",
            "audio/amr",
            "audio/m4a",
            "audio/x-m4a",
            "audio/x-wav",
            "audio/rnd.wav"
    )

    private fun fileSelectorIntent() {
        try {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "audio/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_MIME_TYPES, allowedMimeTypes)
            }
            fileSelectorForResult.launch(Intent.createChooser(intent, "Import a file"))
        } catch (ex: ActivityNotFoundException) {
            console.print("Install a file manager to perform this action.")
        }
    }

    @SuppressLint("Recycle")
    private fun processSelectedFile(data: Intent?) {

        // Get the Uri of the selected file
        val uri: Uri = data!!.data!!
        val mimeType = this.contentResolver!!.getType(uri)
        val cursor = this.contentResolver!!
                .query(uri, arrayOf(MediaStore.Audio.AudioColumns.SIZE), null, null, null)!!
                .also {
                    it.moveToFirst()
                }
        val fileSize = cursor.getLong(0) //in bytes
        cursor.close()
        val retriever = MediaMetadataRetriever().also {
            it.setDataSource(this, uri)
        }
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()

        console.print("selected file uri=$uri, mimeType=$mimeType, fileSize=$fileSize, duration=$duration")
        Log.d(logTag, "processFile: selected file uri=$uri")
        Log.d(logTag, "processFile: selected file mimeType=$mimeType, fileSize=$fileSize, duration=$duration")

        // Start engine
        if (duration != null) {
            fileDuration = duration
            if(duration <= 1000 * 60) {
                startShortAftEngine(uri)
            } else {
                startLongAftEngine(uri)
            }
        }


    }

    private fun startLongAftEngine(uri: Uri) {
        val aftTaskId = aftEngine.longRecognize(uri, aftConfig).also {
            Log.d(logTag, "startLongAftEngine: engine start command given. (TaskID=$it)")
            console.print("Long Engine start command given. (TaskID=$it)")
        }
    }

    private fun startShortAftEngine(uri: Uri) {
        aftEngine.shortRecognize(uri, aftConfig).also {
            Log.d(logTag, "startShortAftEngine: engine start command given.")
            console.print("Short Engine start command given.")
        }
    }

    private fun clearTimer() {
        waitResponseTimer?.cancel()
        waitResponseTimerTask?.cancel()
        waitResponseTimer = null
        waitResponseTimerTask = null
    }

    private val longAftListener: MLRemoteAftListener = object : MLRemoteAftListener {
        var queryNum: Int = 0

        override fun onInitComplete(taskId: String, ext: Any?) {
            // Callback function called when the on-device initialization of audio file transcription is complete.
            // After the initialization is complete, call the startTask method to start audio file upload and processing.
            aftEngine.startTask(taskId)
            Log.d(logTag, "onInitComplete: task is started with (TaskID=$taskId)")
            console.print("task is started with (TaskID=$taskId)")
        }

        override fun onUploadProgress(taskId: String, progress: Double, ext: Any?) {
            // reserved
        }

        override fun onEvent(taskId: String, eventId: Int, ext: Any?) {
            // Callback function of special events, including the stop, resume, and uploaded events of the transcription engine.
            // For long audio file transcription, the result is returned by segment. You can create a thread in this method and call the MLRemoteAftEngine.getLongAftResult() method to periodically obtain the audio file transcription result.
            if (eventId == MLAftEvents.UPLOADED_EVENT) {

                console.print("file is uploaded. (TaskID=$taskId)")
                Log.d(logTag, "onEvent: file is uploaded. (TaskID=$taskId)")

                // Periodically obtain the audio file transcription result using this method.
                queryNum = 0
                getResult(taskId)


                // set a timer when upload is finished
                // this timer will be triggered as "no response"
                waitResponseTimer = Timer()
                waitResponseTimerTask = object : TimerTask() {
                    override fun run() {
                        Log.d(logTag, "Service has no response since 60 seconds!")
                        console.print("Service has no response since 60 seconds!")
                    }
                }
                waitResponseTimer!!.schedule(waitResponseTimerTask, 60000)
            }
        }

        override fun onResult(taskId: String, result: MLRemoteAftResult, ext: Any?) {
            clearTimer()
            // Obtain the transcription result notification.
            if (result.isComplete) {
                if(result.text != null) {
                    Log.d(logTag, "onResult: LongAft: full result obtained. (TaskID=$taskId)")
                    console.print("LongAft: full result obtained. (TaskID=$taskId)")
                    retrieveResultTimer?.cancel()
                    retrieveResultTimer = null


                    // loop each sentence and print log
                    result.sentences.forEach {

                        val sT = TimestampConverter()
                            .setDuration(fileDuration)
                            .setPosition(it.startTime)
                            .getString()


                        Log.d(
                                logTag,
                                "onResult: $sT: ${it.text}"
                        )
                        console.print("$sT: ${it.text}")
                    }

                } else {
                    Log.d(
                            logTag,
                            "onResult: LongAft: result is completed but text is null. (TaskID=$taskId)"
                    )
                    console.print("result is completed but text is null. (TaskID=$taskId)")
                }
            } else {
                queryNum++
                val currentSentenceSize = if(result.sentences!=null) result.sentences.size else 0
                Log.d(
                        logTag,
                        "onResult: LongAft: Querying results (query num: $queryNum - current sentence count: $currentSentenceSize) (TaskID=$taskId)"
                )
                console.print("Querying results (query num: $queryNum - current sentence count: $currentSentenceSize) (TaskID=$taskId)")
            }
        }


        // Periodically obtain the long audio file transcription result.
        private fun getResult(taskId: String) {
            if (retrieveResultTimer == null) {
                retrieveResultTimer = Timer()
            }
            val timerTask: TimerTask = object : TimerTask() {
                override fun run() {
                    // Query the long audio file transcription result by taskId.
                    aftEngine.getLongAftResult(taskId)
                }
            }
            // start query 5000 millis later, then query every 10000 millis
            retrieveResultTimer!!.schedule(timerTask, 5000, 10000)
        }

        override fun onError(taskId: String, errorCode: Int, message: String) {
            // Transcription error callback function.
            Log.d(logTag, "longAft: onError: $message ($errorCode) (TaskID=$taskId)")
            console.print("Error: $message ($errorCode) (TaskID=$taskId)")
            clearTimer()
            retrieveResultTimer?.cancel()
            retrieveResultTimer = null
        }
    }

}