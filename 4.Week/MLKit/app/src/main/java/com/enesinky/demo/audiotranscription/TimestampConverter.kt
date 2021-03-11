package com.enesinky.demo.audiotranscription

import kotlin.math.ceil
import kotlin.math.floor

class TimestampConverter {

    private var ts = 0
    private var ds = 0

    fun setPosition(position: Int): TimestampConverter {
        this.ts = position
        return this
    }

    fun setDuration(duration: Int): TimestampConverter {
        this.ds = duration
        return this
    }

    fun getString(): String {
        val active = ceil(ts / 1000.0).toInt()  // position in seconds
        val hours = floor(active / 60.0 / 60.0).toInt()
        val minutes = floor(active / 60.0).toInt()
        val seconds = active % 60

        var str = "00:00"
        when {
            ds in 0 until 1000 * 60 * 60 -> {
                str = addZero(minutes) + ":" + addZero(seconds)
            }
            ds >= 1000 * 60 * 60 -> {
                str = addZero(hours) + ":" + addZero((minutes % 60)) + ":" + addZero(seconds)
            }
        }
        return str
    }

    private fun addZero(i: Int): String {
        var str = i.toString()
        if (str.length < 2) str = "0$str"
        return str
    }

}