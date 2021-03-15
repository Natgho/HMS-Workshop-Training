package com.enesinky.demo.audiotranscription

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.translate.MLTranslateLanguage
import com.huawei.hms.mlsdk.translate.MLTranslatorFactory
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslateSetting
import com.huawei.hms.mlsdk.translate.cloud.MLRemoteTranslator


class TranslateActivity : AppCompatActivity() {

    private val logTag = "translation"
    private lateinit var context: Context
    private lateinit var setting: MLRemoteTranslateSetting
    private lateinit var mlRemoteTranslator: MLRemoteTranslator
    private lateinit var inputText: EditText
    private lateinit var textHolder: TextView
    private lateinit var sourceSpinner: Spinner
    private lateinit var targetSpinner: Spinner
    private lateinit var button: Button
    private lateinit var clearButton: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var languages: List<Language>
    private val console = Console()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)
        context = this.applicationContext
        //set HMS ML Kit API KEY
        MLApplication.getInstance().apiKey = "CgB6e3x9CHqu5EMNL3apkna/Ob7gS6dxMLT926TDV2MAB77MJso4Qfx6v0sN5VGG3Lt+CcA1Csf7sJFzWebo6TQ3"

        sourceSpinner = findViewById(R.id.sourceLang)
        targetSpinner = findViewById(R.id.targetLang)
        inputText = findViewById(R.id.input)
        textHolder = findViewById(R.id.textView)
        button = findViewById(R.id.button)
        clearButton = findViewById(R.id.clearBtn)
        scrollView = findViewById(R.id.scrollView)

        // Build Console
        console.build(scrollView, this)

        retrieveSupportedLanguages()

        button.setOnClickListener {

            // hide keyboard
            this.hideKeyboard()

            // sourceText: text to be translated, with up to 5000 characters.
            val sourceText: String = inputText.text.toString()

            // check source/target language are same - input text is not empty
            if(setting.sourceLangCode != setting.targetLangCode && sourceText != "") {

                val task = mlRemoteTranslator.asyncTranslate(sourceText)
                task.addOnSuccessListener { translatedText ->
                    // Processing logic for recognition success.
                    console.print(translatedText)
                }.addOnFailureListener { e ->
                    // Processing logic for recognition failure.
                    try {
                        val mlException = e as MLException
                        // Obtain the result code. You can process the result code and customize respective messages displayed to users.
                        val errorCode = mlException.errCode
                        // Obtain the error information. You can quickly locate the fault based on the result code.
                        val errorMessage = mlException.message
                        console.print("Error: $errorMessage ($errorCode)")
                    } catch (error: Exception) {
                        // Handle the conversion error.
                        console.print("cannot resolve exception: $error")
                    }
                }

            }

        }

        clearButton.setOnClickListener {
            console.clear()
        }

        // add listener to spinners
        // we want to update translation setting when another language is selected
        sourceSpinner.onItemSelectedListener = spinnerListener
        targetSpinner.onItemSelectedListener = spinnerListener

    }

    private val spinnerListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateTranslator()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    private fun updateTranslator() {
        // retrieve language codes
        val source: String = languages[sourceSpinner.selectedItemPosition].iso6391
        val target: String = languages[targetSpinner.selectedItemPosition].iso6391
        // Create language settings
        setting =
            MLRemoteTranslateSetting.Factory() // Set the source language code. The BCP-47 standard is used for Traditional Chinese, and the ISO 639-1 standard is used for other languages. This parameter is optional. If this parameter is not set, the system automatically detects the language.
                .setSourceLangCode(source) // Set the target language code. The BCP-47 standard is used for Traditional Chinese, and the ISO 639-1 standard is used for other languages.
                .setTargetLangCode(target)
                .create()
        // Create translator object
        mlRemoteTranslator = MLTranslatorFactory.getInstance().getRemoteTranslator(setting)
        Log.d(logTag, "settings: source=$source target=$target")
    }

    private fun retrieveSupportedLanguages() {
        MLTranslateLanguage.getCloudAllLanguages().addOnSuccessListener { languageCodes ->

            // Languages supported by real-time translation are successfully obtained as iso codes
            // lets save languages as Language objects
            languages = LanguagesFromJson(context).get(languageCodes)

            // Let's convert Language objects (includes iso code,name,native name) into string array
            val languagesArray = convertToArray(languages)

            val adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item, languagesArray
            )
            sourceSpinner.adapter = adapter
            targetSpinner.adapter = adapter
        }
    }

    private fun convertToArray(objects: List<Language>): Array<String?> {
        val array: Array<String?> = arrayOfNulls(objects.size)
        objects.forEachIndexed { i, el ->
            array[i] = el.name
        }
        return array
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}