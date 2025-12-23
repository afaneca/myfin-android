package com.afaneca.myfin.ui.dashboard


import androidx.annotation.StringRes
import com.afaneca.myfin.ui.transactions.list.TransactionListContract
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow

object DashboardContract {
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
    )

    /**
     * UI interactions, such as button clicks, data input, etc...
     */
    sealed interface Event {
        data object Init : Event
        data object NavigationIconClicked : Event
        data object ToolbarSettingsClicked : Event
        data object TransactionsShortcutClicked : Event
        data object AccountsShortcutClicked : Event
        data object BudgetsShortcutClicked : Event
    }

    /**
     * Non-persistent changes to the UI, such as temporary error messages, snackbar/toast messages, navigation, etc...
     */
    sealed interface Effect {
        data object OpenNavigationDrawer : Effect
        data object GoToSettings : Effect
        data object GoToTransactions : Effect
        data object GoToAccounts : Effect
        data object GoToBudgets : Effect
        data class ShowGenericError(@StringRes val errorMessage: Int? = null) : Effect
    }
}
