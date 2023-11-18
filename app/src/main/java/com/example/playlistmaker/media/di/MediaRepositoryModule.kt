package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.data.LikedTracksRepositoryImpl
import com.example.playlistmaker.media.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.media.domain.db.LikedTracksInteractor
import com.example.playlistmaker.media.domain.db.LikedTracksInteractorImpl
import com.example.playlistmaker.media.domain.db.LikedTracksRepository
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.db.PlaylistsInteractorImpl
import com.example.playlistmaker.media.domain.db.PlaylistsRepository
import org.koin.dsl.module

val mediaRepositoryModule = module {
    single<LikedTracksRepository> {
        LikedTracksRepositoryImpl(get(), get())
    }
    single<LikedTracksInteractor> {
        LikedTracksInteractorImpl(get())
    }
    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get(), get(), get())
    }
    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
}