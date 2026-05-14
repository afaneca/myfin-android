package com.afaneca.myfin.domain.model

data class AddTransactionFormData(
    val accounts: List<UserAccount>,
    val categories: List<Category>,
    val entities: List<Entity>,
)

