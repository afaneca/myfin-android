package com.afaneca.myfin.data

import androidx.lifecycle.LiveData
import com.afaneca.myfin.data.db.accounts.UserAccountEntity

interface PrivateRepository {
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
