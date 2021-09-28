package com.afaneca.myfin.open.login.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.afaneca.myfin.open.login.data.FakeLoginRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by me on 25/09/2021
 */
@ExperimentalCoroutinesApi
class LoginViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(FakeLoginRepository())
    }

    @Test
    fun testLoginWithEmptyUsername() {
        viewModel.onUsernameInputChanged("")
        viewModel.onPasswordInputChanged("123")
        viewModel.refreshLoginBtnState()
        assertThat(viewModel.shouldLoginButtonBeEnabled.value).isFalse()
    }

    @Test
    fun testLoginWithEmptyPassword() {
        viewModel.onUsernameInputChanged("123")
        viewModel.onPasswordInputChanged("")
        viewModel.refreshLoginBtnState()
        assertThat(viewModel.shouldLoginButtonBeEnabled.value).isFalse()
    }

    @Test
    fun testLoginWithEmptyUsernameAndPassword() {
        viewModel.onUsernameInputChanged("")
        viewModel.onPasswordInputChanged("")
        viewModel.refreshLoginBtnState()
        assertThat(viewModel.shouldLoginButtonBeEnabled.value).isFalse()
    }
}