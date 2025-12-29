package com.afaneca.myfin.data.source

import com.afaneca.myfin.domain.model.BalanceDigest
import com.afaneca.myfin.domain.model.SessionData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory data source for temporary, non-persistent data.
 * Data is lost when the application is terminated.
 */
interface TransientDataSource {
    fun setBalanceDigest(balanceDigest: BalanceDigest)
    fun observeBalanceDigest(): Flow<BalanceDigest>
    fun setSessionData(data: SessionData)
    fun getSessionData(): SessionData?
    fun observeIsAuthenticated(): Flow<Boolean>
    fun setUnauthenticated()
    fun setAuthenticated()
    fun clearUserData()
}

@Singleton
class InMemoryDataSource @Inject constructor() : TransientDataSource {
    private val _balanceDigest = MutableStateFlow(BalanceDigest())
    private val _isAuthenticated = MutableStateFlow(false)
    private var sessionData: SessionData? = null

    override fun setBalanceDigest(balanceDigest: BalanceDigest) {
        _balanceDigest.value = balanceDigest
    }

    override fun observeBalanceDigest(): Flow<BalanceDigest> {
        return _balanceDigest.asStateFlow()
    }

    override fun setSessionData(data: SessionData) {
        sessionData = data
    }

    override fun getSessionData(): SessionData? {
        return sessionData
    }

    override fun observeIsAuthenticated(): Flow<Boolean> {
        return _isAuthenticated.asStateFlow()
    }

    override fun setUnauthenticated() {
        _isAuthenticated.value = false
    }

    override fun setAuthenticated() {
        _isAuthenticated.value = true
    }

    override fun clearUserData() {
        _balanceDigest.value = BalanceDigest()
        _isAuthenticated.value = false
        sessionData = null
    }
}

