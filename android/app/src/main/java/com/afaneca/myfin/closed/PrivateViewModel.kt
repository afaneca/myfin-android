package com.afaneca.myfin.closed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 * Created by me on 14/06/2021
 */

@KoinApiExtension
class PrivateViewModel : ViewModel(), KoinComponent {
    private val repository: PrivateRepository by lazy {
        PrivateRepository(get(), get())
    }

    fun getUserAccounts(): LiveData<List<UserAccountEntity>> {
        return repository.getAllUserAccounts()
    }


}