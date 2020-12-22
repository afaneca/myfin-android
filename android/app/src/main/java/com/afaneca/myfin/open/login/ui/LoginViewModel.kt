package com.afaneca.myfin.open.login.ui

import androidx.lifecycle.*
import com.afaneca.myfin.network.Resource
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

    private var _isLoginButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isLoginButtonEnabled: LiveData<Boolean>
        get() = _isLoginButtonEnabled


    private val _loginResponse: MutableLiveData<Resource<AttemptLoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<AttemptLoginResponse>>
        get() = _loginResponse


    fun attemptLogin(
        username: String,
        password: String
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.attemptLogin(username, password)
    }
}