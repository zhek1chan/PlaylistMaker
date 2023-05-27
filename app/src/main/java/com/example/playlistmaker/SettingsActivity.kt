package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val nightmodeButton:Switch = findViewById<Switch>(R.id.switch_nightmode)
        nightmodeButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back)
        arrowBackButton.setOnClickListener {
            finish()
        }
        val shareButton = findViewById<LinearLayout>(R.id.button_share)
        shareButton.setOnClickListener {
            var shareIntent = Intent().apply{
                this.action=Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT,getString(R.string.settings_url))
                this.type="text/plain"
            }
            startActivity(shareIntent)
        }
        val helpButton = findViewById<LinearLayout>(R.id.button_help)
        helpButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_email)))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_title))
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_message))
            startActivity(shareIntent)
        }
        val urlButton = findViewById<LinearLayout>(R.id.button_user_agreements)
        urlButton.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(browserIntent)
        }
    }
}