package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme =
            sharedPref.getBoolean(THEME_PREF, currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        (applicationContext as App).switchTheme(isDarkTheme)

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

    companion object {
        const val PREFS = "my_prefs"
        const val THEME_PREF = "isDarkTheme"
    }
}