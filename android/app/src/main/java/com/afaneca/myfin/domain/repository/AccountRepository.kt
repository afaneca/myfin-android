package com.afaneca.myfin.domain.repository

import androidx.lifecycle.LiveData
import com.afaneca.myfin.data.db.accounts.UserAccountEntity

interface AccountRepository {
    fun insertAccount(
        userAccountObj: UserAccountEntity
    )

    fun insertAccounts(
        userAccountObj: List<UserAccountEntity>
    )

    fun getAllUserAccounts(): LiveData<List<UserAccountEntity>>
    fun deleteUserAccount(
        userAccountObj: UserAccountEntity
    )

    fun deleteAllUserAccounts()
    fun clearUserSessionData()
}
