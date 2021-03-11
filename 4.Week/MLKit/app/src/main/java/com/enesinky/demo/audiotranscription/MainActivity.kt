package com.enesinky.demo.audiotranscription

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val card1 = findViewById<CardView>(R.id.cardView)
        val card2 = findViewById<CardView>(R.id.cardView2)
        val card3 = findViewById<CardView>(R.id.cardView3)

        card1.setOnClickListener {
            val intent = Intent(this, AudioFileActivity::class.java)
            startActivity(intent)
        }

        card2.setOnClickListener {
            val intent = Intent(this, RealTimeActivity::class.java)
            startActivity(intent)
        }

        card3.setOnClickListener {
            val intent = Intent(this, TranslateActivity::class.java)
            startActivity(intent)
        }

    }
}