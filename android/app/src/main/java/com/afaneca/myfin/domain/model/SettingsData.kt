package com.afaneca.myfin.domain.model

import okhttp3.HttpUrl

data class SettingsData(
    val baseApiUrl: HttpUrl,
    val useDynamicTheme: Boolean,
)