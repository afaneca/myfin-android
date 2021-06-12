package com.afaneca.myfin.open.login.ui

import androidx.lifecycle.*
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.open.login.data.AttemptLoginResponse
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.java.KoinJavaComponent.inject

/**
 * Created by me on 21/12/2020
 */
class LoginViewModel(
    private val repository: LoginRepository,
    private val userDataManager: UserDataManager
) : ViewModel() {
    private var _usernameInput: MutableLiveData<String> = MutableLiveData("")
    val usernameInput: LiveData<String>
        get() = _usernameInput

    private var _passwordInput: MutableLiveData<String> = MutableLiveData("")
    val passwordInput: LiveData<String>
        get() = _passwordInput

    var shouldLoginButtonBeEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    /*val shouldLoginButtonBeEnabled: LiveData<Boolean>
        get() = _shouldLoginButtonBeEnabled*/


    private val _loginResponse: MutableLiveData<Resource<AttemptLoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<AttemptLoginResponse>>
        get() = _loginResponse


    init {
        val lastUsername = userDataManager.getLastUsername()
        if (!lastUsername.isNullOrBlank()) {
            _usernameInput.value = (lastUsername)
        }
    }

    fun attemptLogin(
        username: String,
        password: String
    ) = viewModelScope.launch {
        shouldLoginButtonBeEnabled.postValue(false)
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.attemptLogin(username, password)
        if (_loginResponse.value is Resource.Success<AttemptLoginResponse>) {
            val sessionToken = (_loginResponse.value as Resource.Success<AttemptLoginResponse>)
                .data.sessionkey ?: ""
            val username = (_loginResponse.value as Resource.Success<AttemptLoginResponse>)
                .data.username ?: ""

            saveSessionToken(sessionToken)
            saveUsername(username)
        } else {
            shouldLoginButtonBeEnabled.postValue(true)
        }


    }

    private fun saveUsername(username: String) {
        repository.saveUsername(username)
    }

    private fun saveSessionToken(token: String) = viewModelScope.launch {
        repository.saveSessionKeyToken(token)
    }
}