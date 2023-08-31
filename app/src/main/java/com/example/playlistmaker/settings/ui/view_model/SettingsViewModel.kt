package com.example.playlistmaker.settings.ui.view_model

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private var sharingInteractor: SharingInteractor,
    private var settingsInteractor: SettingsInteractor
) : ViewModel() {
    init {
        sharingInteractor = Creator.provideSharingIneractor()
        settingsInteractor = Creator.provideSettingsIneractor()
    }

    private var onBackLiveData = MutableLiveData(false)
    fun onBackClick() {
        onBackLiveData.value = true
    }

    fun getOnBackLiveData(): LiveData<Boolean> = onBackLiveData

    private var themeLiveData = MutableLiveData(settingsInteractor.isAppThemeDark())
    fun getThemeLiveData(): LiveData<Boolean> {
        val getting = if (themeLiveData.value!!) "day" else "night"
        Log.d("Тема", "Theme is $getting")
        return themeLiveData
    }

    fun themeSwitch() {
        themeLiveData.value = settingsInteractor.changeThemeSettings()
        val getting = if (themeLiveData.value!!) "day" else "night"
        Log.d("Тема", "Swithed theme to $getting")
        makeTheme(themeLiveData.value!!)
    }

    private fun makeTheme(theme: Boolean) {
        if (theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun writeSupport() {
        sharingInteractor.openSupport()
    }

    fun readAgreement() {
        sharingInteractor.openTerms()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = App()
                    return SettingsViewModel(
                        Creator.provideSharingIneractor(),
                        Creator.provideSettingsIneractor()
                    ) as T
                }
            }
    }
}