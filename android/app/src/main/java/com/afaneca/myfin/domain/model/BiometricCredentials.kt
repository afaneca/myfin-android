package com.afaneca.myfin.domain.model

data class BiometricCredentials(
    val username: String,
    val encryptedPassword: String,
    val initializationVector: String,
)
