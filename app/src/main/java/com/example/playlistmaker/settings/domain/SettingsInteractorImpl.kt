package com.example.playlistmaker.settings.domain

class SettingsInteractorImpl(private var themeSettings: ThemeSettings) : SettingsInteractor {

    var isDarkTheme = true

    override fun isAppThemeDark(): Boolean {
        isDarkTheme = themeSettings.lookAtTheme()
        return isDarkTheme
    }

    override fun changeThemeSettings(): Boolean {
        isDarkTheme = themeSettings.appThemeSwitch()
        return isDarkTheme
    }
}