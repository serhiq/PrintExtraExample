package com.example.playgroundevotor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Вызов сервиса PrintExtra"
        prefs = Prefs(this)
        textView = findViewById<TextView>(R.id.textView)
    }

    override fun onResume() {
        super.onResume()
        textView.text = prefs.logs
    }
}