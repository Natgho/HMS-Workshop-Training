package com.enesinky.demo.audiotranscription

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

data class Language (var iso6391: String, var name: String, var native: String)
class LanguagesFromJson(val context: Context) {

    private val assetFileName = "global_languages.json"

    private fun getJsonDataFromAsset(): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    fun get(languageCodes: Set<String>): List<Language> {
        val jsonFileString = getJsonDataFromAsset()
        val gson = Gson()
        val listLanguageType = object : TypeToken<List<Language>>() {}.type
        return (gson.fromJson(jsonFileString, listLanguageType) as List<Language>)
            .filter { language ->    // match with languageCodes from HMS ML Kit - only get this languages
            languageCodes.contains(language.iso6391)
            }
            .sortedBy { it.name }.toMutableList() // sort languages and convert to List of Language class;
    }


}