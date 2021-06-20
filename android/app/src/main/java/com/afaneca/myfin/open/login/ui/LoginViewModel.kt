package com.afaneca.myfin.open.login.ui

import android.content.Context
import androidx.lifecycle.*
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.open.login.data.AttemptLoginResponse
import kotlinx.coroutines.launch

/**
 * Created by me on 21/12/2020
 */
class LoginViewModel(
    private val repository: LoginRepository,
    private val userDataManager: UserDataManager
) : ViewModel() {
    private val isToKeepSession by lazy { repository.getIsToKeepSession() }
    private val lastUsername by lazy { repository.getLastUsername() }

    private var _usernameInput: MutableLiveData<String> = MutableLiveData("")
    val usernameInput: LiveData<String>
        get() = _usernameInput

    private var _passwordInput: MutableLiveData<String> = MutableLiveData("")
    val passwordInput: LiveData<String>
        get() = _passwordInput

    var shouldLoginButtonBeEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    /*val shouldLoginButtonBeEnabled: LiveData<Boolean>
        get() = _shouldLoginButtonBeEnabled*/

    private var _triggerBiometricAuth: MutableLiveData<Boolean> = MutableLiveData(false)
    val triggerBiometricAuth: LiveData<Boolean>
        get() = _triggerBiometricAuth


    private val _loginResponse: MutableLiveData<Resource<AttemptLoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<AttemptLoginResponse>>
        get() = _loginResponse


    init {
        if (isToKeepSession && hasPasswordStored()) {
            _triggerBiometricAuth.postValue(true)
        }

        if (!lastUsername.isNullOrBlank()) {
            _usernameInput.value = (lastUsername)
        }
    }

    private fun hasPasswordStored(): Boolean {
        // TODO - unmock this
        return true
    }

    fun attemptLogin(
        username: String,
        password: String,
        keepSession: Boolean,
        context: Context
    ) = viewModelScope.launch {
        shouldLoginButtonBeEnabled.postValue(false)
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.attemptLogin(username, password)
        if (_loginResponse.value is Resource.Success<AttemptLoginResponse>) {
            val sessionToken = (_loginResponse.value as Resource.Success<AttemptLoginResponse>)
                .data.sessionkey ?: ""
            val username = (_loginResponse.value as Resource.Success<AttemptLoginResponse>)
                .data.username ?: ""
            val userAccounts = (_loginResponse.value as Resource.Success<AttemptLoginResponse>)
                .data.accounts ?: listOf<UserAccountEntity>()

            saveUsername(username)
            saveSessionToken(sessionToken)
            saveUserAccounts(userAccounts as List<UserAccountEntity>)
            repository.saveIsToKeepSession(keepSession)

            if (keepSession) {
                savePassword(password, context)
            }
        } else {
            shouldLoginButtonBeEnabled.postValue(true)
        }
    }

    fun attemptBiometricLogin(context: Context) {
        val username = lastUsername
        val password = getPassword(context)
        attemptLogin(username ?: "", password, true, context)
    }


    private fun saveUserAccounts(userAccounts: List<UserAccountEntity>) {
        repository.saveUserAccounts(userAccounts)
    }

    private fun saveUsername(username: String) {
        repository.saveUsername(username)
    }

    private fun savePassword(password: String, context: Context) {
        repository.saveEncryptedPassword(context, password)
    }

    private fun getPassword(context: Context): String {
        return repository.getPassword(context)
    }

    private fun saveSessionToken(token: String) = viewModelScope.launch {
        repository.saveSessionKeyToken(token)
    }
}