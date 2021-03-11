package com.enesinky.demo.audiotranscription

import android.app.Activity
import android.view.View
import android.widget.ScrollView
import android.widget.TextView

class Console {

    private lateinit var scrollView: ScrollView
    private lateinit var textHolder: TextView
    private lateinit var activity: Activity

    fun build(scrollView: ScrollView, activity: Activity) : Console {
        this.scrollView = scrollView
        this.activity = activity
        this.textHolder = scrollView.getChildAt(0) as TextView
        return this
    }

     fun print(strText: String) {
        val print = textHolder.text.toString() + "\n\n" + strText
        activity.runOnUiThread {
            textHolder.text = print
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    fun clear() {
        activity.runOnUiThread {
            textHolder.text = activity.resources.getString(R.string.output)
        }
    }

}