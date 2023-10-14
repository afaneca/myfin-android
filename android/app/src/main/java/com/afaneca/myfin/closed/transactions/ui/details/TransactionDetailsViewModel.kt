package com.afaneca.myfin.closed.transactions.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.repository.TransactionsRepository
import com.afaneca.myfin.data.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), TransactionDetailsContract.ViewModel {

    private val _effect = MutableSharedFlow<TransactionDetailsContract.Effect>(
        replay = 0,
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val effect = _effect.asSharedFlow()

    private var trxId: Int
        get() = savedStateHandle.get<Int>(TRX_ID_SAVED_STATE_HANDLE_TAG) ?: -1
        set(value) = savedStateHandle.set(TRX_ID_SAVED_STATE_HANDLE_TAG, value)

    fun setTransactionId(transactionId: Int) {
        trxId = transactionId
    }

    override fun triggerEvent(event: TransactionDetailsContract.Event) {
        when (event) {
            is TransactionDetailsContract.Event.RemoveTransactionClicked -> {
                removeTransaction(trxId)
            }

            is TransactionDetailsContract.Event.EditTransactionClicked -> {
                _effect.tryEmit(TransactionDetailsContract.Effect.NavigateToEditTransaction)
            }
        }
    }

    private fun removeTransaction(trxId: Int) {
        viewModelScope.launch {
            when (val resource = transactionsRepository.removeTransaction(trxId)) {
                is Resource.Failure -> {
                    _effect.tryEmit(TransactionDetailsContract.Effect.ShowError(resource.errorMessage))
                }

                is Resource.Success -> {
                    _effect.tryEmit(TransactionDetailsContract.Effect.NavigateToTransactionsList)
                }

                else -> {}
            }
        }
    }

    companion object {
        private const val TRX_ID_SAVED_STATE_HANDLE_TAG = "TRX_ID_SAVED_STATE_HANDLE_TAG"
    }
}