package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.ui.viewmodel.CreatingPlaylistViewModel
import com.example.playlistmaker.media.ui.viewmodel.FavouriteTracksViewModel
import com.example.playlistmaker.media.ui.viewmodel.MediaActivityViewModel
import com.example.playlistmaker.media.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { FavouriteTracksViewModel(get(), get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { CreatingPlaylistViewModel(get()) }
    viewModel { MediaActivityViewModel() }
}