package com.afaneca.myfin.domain.model

data class LoginResult(
    val sessionKey: String,
    val username: String,
    val trustLimit: Int,
    val accounts: List<UserAccount>,
    val currency: String,
)
