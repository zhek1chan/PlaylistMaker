package com.example.playlistmaker
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MediaActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (getSupportActionBar() != null)
            getSupportActionBar()?.hide();
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mediateka)
    }
}