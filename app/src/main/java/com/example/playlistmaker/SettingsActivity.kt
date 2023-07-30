package com.example.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val nightModeSwitch = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        nightModeSwitch.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        nightModeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

            with(getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE).edit()) {
                putBoolean(MainActivity.THEME_PREF, checked)
                apply()
            }
        }
        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back)
        arrowBackButton.setOnClickListener {
            finish()
        }
        val shareButton = findViewById<LinearLayout>(R.id.button_share)
        shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_url))
                this.type = "text/plain"
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
        urlButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(browserIntent)
        }
    }
}