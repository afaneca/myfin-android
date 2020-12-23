package com.afaneca.myfin.open.login.ui

import androidx.lifecycle.*
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.open.login.data.AttemptLoginResponse
import kotlinx.coroutines.launch

/**
 * Created by me on 21/12/2020
 */
class LoginViewModel(
    private val repository: LoginRepository
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


    fun attemptLogin(
        username: String,
        password: String
    ) = viewModelScope.launch {
        shouldLoginButtonBeEnabled.value = false
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.attemptLogin(username, password)
        shouldLoginButtonBeEnabled.value = true
    }

    fun saveSessionToken(token: String) = viewModelScope.launch {
        repository.saveSessionKeyToken(token)
    }
}