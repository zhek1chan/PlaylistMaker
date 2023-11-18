package com.example.playlistmaker.media.di

import androidx.room.Room.databaseBuilder
import com.example.playlistmaker.media.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.converters.TrackDbConvertor
import com.example.playlistmaker.media.data.converters.TrackInPlaylistConvertor
import com.example.playlistmaker.media.data.db.AppDataBase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaDataModule = module {
    single {
        databaseBuilder(androidContext(), AppDataBase::class.java, "database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
    factory { TrackDbConvertor() }
    factory { PlaylistDbConvertor() }
    factory { TrackInPlaylistConvertor() }
}