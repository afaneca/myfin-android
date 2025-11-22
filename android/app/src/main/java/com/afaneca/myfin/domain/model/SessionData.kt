package com.afaneca.myfin.domain.model

data class SessionData(
    val username: String,
    val sessionKey: String,
    val currency: String,
)
