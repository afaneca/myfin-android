package com.afaneca.myfin.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.open.login.ui.LoginViewModel

/**
 * Created by me on 22/12/2020
 */
class BaseViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository as LoginRepository) as T
            // Add More ViewModels here
            else -> throw IllegalArgumentException("ViewModel class not found")
        }
    }
}