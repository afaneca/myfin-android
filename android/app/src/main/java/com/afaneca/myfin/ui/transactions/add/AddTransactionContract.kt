package com.afaneca.myfin.ui.transactions.add

import androidx.annotation.StringRes
import com.afaneca.myfin.domain.model.Category
import com.afaneca.myfin.domain.model.Entity
import com.afaneca.myfin.domain.model.TransactionType
import com.afaneca.myfin.domain.model.UserAccount
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

object AddTransactionContract {
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
        val isLoadingFormData: Boolean = false,
        val type: TransactionType? = null,
        val date: Long? = null,
        val amount: BigDecimal? = null,
        val accountFrom: UserAccount? = null,
        val accountTo: UserAccount? = null,
        val isAccountFromVisible: Boolean = false,
        val isAccountToVisible: Boolean = false,
        val category: Category? = null,
        val entity: Entity? = null,
        val description: String? = null,
        val isEssential: Boolean = false,
        val isLoadingAutoCategorization: Boolean = false,
        val isSubmitButtonEnabled: Boolean = false,
        val availableAccounts: List<UserAccount> = emptyList(),
        val availableCategories: List<Category> = emptyList(),
        val availableEntities: List<Entity> = emptyList(),
    )

    /**
     * UI interactions, such as button clicks, data input, etc...
     */
    sealed interface Event {
        data object Init : Event
        data class TypeUpdated(val type: TransactionType) : Event
        data class DateUpdated(val date: Long) : Event
        data class AmountUpdated(val amount: BigDecimal?) : Event
        data class AccountFromUpdated(val account: UserAccount?) : Event
        data class AccountToUpdated(val account: UserAccount?) : Event
        data class CategoryUpdated(val category: Category?) : Event
        data class EntityUpdated(val entity: Entity?) : Event
        data class DescriptionUpdated(val description: String) : Event
        data class IsEssentialUpdated(val isEssential: Boolean) : Event
        data object AutoCategorizeClicked : Event
        data object SaveClicked : Event
    }

    /**
     * Non-persistent changes to the UI, such as temporary error messages, snackbar/toast messages, navigation, etc...
     */
    sealed interface Effect {
        data class ShowGenericError(@param:StringRes val errorMessage: Int? = null) : Effect
        data object GoBackToList : Effect
    }
}
