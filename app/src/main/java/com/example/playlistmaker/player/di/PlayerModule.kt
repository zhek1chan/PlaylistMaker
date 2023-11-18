package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.ui.view_model.FragmentPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl(MediaPlayer())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    viewModel { FragmentPlayerViewModel(get(), get(), get()) }
}