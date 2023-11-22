package com.example.pmf_engine_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val pmfEngine = PMFEngineManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.pmf_engine_android_example.R.layout.activity_main)
        pmfEngine.configure(this)
        pmfEngine.showPopupIfNeeded()
    }
}