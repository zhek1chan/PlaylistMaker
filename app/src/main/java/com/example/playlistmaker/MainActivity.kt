package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchButton = findViewById<Button>(R.id.button_search)
        val mediaButton = findViewById<Button>(R.id.button_media)
        val settingsButton = findViewById<Button>(R.id.button_settings)
        fun navigateTo(clazz: Class<out AppCompatActivity>) {
            val intent = Intent(this, clazz)
            startActivity(intent)
        }
        searchButton.setOnClickListener {
            navigateTo(SearchActivity::class.java)
        }

        mediaButton.setOnClickListener {
            navigateTo(MediaActivity::class.java)
        }

        settingsButton.setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }

    }
}