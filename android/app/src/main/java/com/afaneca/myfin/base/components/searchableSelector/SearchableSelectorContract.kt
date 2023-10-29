package com.afaneca.myfin.base.components.searchableSelector

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

sealed class SearchableSelectorContract {
    interface ViewModel {
        fun subscribeToState(): SharedFlow<State?>
        fun triggerEvent(event: Event)
    }

    sealed class State {
        data class Init(val dataset: List<SearchableListItem>) : State()
        data class ItemSelected(val item: SearchableListItem, val instanceId: String) : State()
    }

    sealed class Event {
        data class InitList(val instanceId: String, val dataset: List<SearchableListItem>) : Event()
        data class Itemselected(val item: SearchableListItem) : Event()
    }
}
