package com.afaneca.myfin.closed.transactions.ui.details

import kotlinx.coroutines.flow.SharedFlow

sealed class TransactionDetailsContract {

    interface ViewModel {
        val effect: SharedFlow<Effect>

        fun triggerEvent(event: Event)
    }

    sealed class Event {
        object EditTransactionClicked : Event()
        object RemoveTransactionClicked : Event()
    }

    sealed class Effect {
        object NavigateToTransactionsList : Effect()
        object NavigateToEditTransaction : Effect()
        data class ShowError(val message: String? = null) : Effect()
    }
}