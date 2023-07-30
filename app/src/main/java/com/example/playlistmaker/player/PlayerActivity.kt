package com.example.playlistmaker.player

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R

class PlayerActivity : AppCompatActivity() {

    private lateinit var albumCover: ImageView
    private lateinit var nameSong: TextView
    private lateinit var bandName: TextView
    private lateinit var duration: TextView
    private lateinit var albumName: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var backButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        albumCover = findViewById(R.id.albums_cover_player_activity)
        nameSong = findViewById(R.id.song_name_player_activity)
        bandName = findViewById(R.id.band_name_player_activity)
        duration = findViewById(R.id.duration_track_value_player_activity)
        albumName = findViewById(R.id.album_value_player_activity)
        year = findViewById(R.id.year_value_player_activity)
        genre = findViewById(R.id.genre_value_player_activity)
        country = findViewById(R.id.country_value_player_activity)
        backButton = findViewById(R.id.back_button_player_activity)

        transferDateFromSearchActivity()

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun transferDateFromSearchActivity() {
        val arguments: Bundle? = intent.extras

        Glide.with(applicationContext)
            .load(arguments?.getString("album cover"))
            .placeholder(R.drawable.song_cover)
            .centerCrop()
            .into(albumCover)
        nameSong.text = arguments?.getString("name song")
        bandName.text = arguments?.getString("band")
        duration.text = arguments?.getString("duration")
        albumName.text = arguments?.getString("album")
        year.text = arguments?.getString("year")
        genre.text = arguments?.getString("genre")
        country.text = arguments?.getString("country")
    }
}