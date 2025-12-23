package com.afaneca.myfin.ui.transactions.list

import androidx.annotation.StringRes
import com.afaneca.myfin.domain.model.Transaction
import com.afaneca.myfin.ui.dashboard.DashboardContract
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow

object TransactionListContract {
    const val DEFAULT_PAGE_SIZE = 15

    interface ViewModel {
        val mainState: StateFlow<MainState>
        val effect: SharedFlow<Effect>

        fun triggerEvent(event: Event)
    }

    /**
     * Persistent view state. Single object per logical section (might contain other objects inside it for better organization).
     */
    data class MainState(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val isLoadingAdditionalPage: Boolean = false,
        val page: Int = 0,
        val query: String = "",
        val canRequestMoreItems: Boolean = false,
        val items: List<Transaction>? = null,
        val groupedItems: Map<String, List<Transaction>>? = null,
    )

    /**
     * UI interactions, such as button clicks, data input, etc...
     */
    sealed interface Event {
        data object Init : Event
        data object NavigationIconClicked : Event
        data object ToolbarSettingsClicked : Event
        data object ClearQueryClicked : Event
        data class QueryInputUpdated(val query: String) : Event
        data object EndOfListReached : Event
        data class TransactionClicked(val transaction: Transaction) : Event
    }

    /**
     * Non-persistent changes to the UI, such as temporary error messages, snackbar/toast messages, navigation, etc...
     */
    sealed interface Effect {
        data object OpenNavigationDrawer : Effect
        data object GoToSettings : Effect
        data class ShowGenericError(@StringRes val errorMessage: Int? = null) : Effect
        data class GoToTransactionDetails(val transaction: Transaction) : Effect
    }
}
