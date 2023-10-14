package com.afaneca.myfin.closed.transactions.ui.addTransaction

import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.data.model.AccountResponse
import com.afaneca.myfin.data.model.AddTransactionStep0Response
import com.afaneca.myfin.data.model.CategoryResponse
import com.afaneca.myfin.data.model.EntityResponse
import kotlinx.coroutines.flow.StateFlow

sealed class AddTransactionContract {

    interface ViewModel {
        val state: StateFlow<State>
        val effect: StateFlow<Effect?>

        fun triggerEvent(event: Event)
    }

    data class State(
        val isLoading: Boolean = false,
        val formData: AddTransactionInitialFormData? = null,
        val trx: MyFinTransaction? = null,
        val isAddButtonEnabled: Boolean = false,
        val isAccountFromEnabled: Boolean = false,
        val isAccountToEnabled: Boolean = false,
        val isEssentialToggleVisible: Boolean = false,
    )

    sealed class Event {
        data class InitForm(val trx: MyFinTransaction? = null) : Event()
        data class AmountInserted(val amount: String) : Event()
        data class DateSelected(val timestamp: Long) : Event()
        data class AccountFromSelected(val accountId: String) : Event()
        data class AccountToSelected(val accountId: String) : Event()
        data class EntitySelected(val entityId: String) : Event()
        data class CategorySelected(val categoryId: String) : Event()
        data class TypeSelected(val typeId: Char) : Event()
        data class DescriptionChanged(val description: String) : Event()
        data class EssentialToggled(val selected: Boolean) : Event()
        object AddEditTransactionClick : Event()
    }

    sealed class Effect {
        data class NavigateToTransactionList(val isEditing: Boolean) : Effect()
        data class ShowError(val errorMessage: String? = null) : Effect()
    }

    data class AddTransactionInitialFormData(
        val entities: List<Entity>,
        val categories: List<Category>,
        val accounts: List<Account>
    )

    data class Entity(
        val entityId: String,
        val name: String,
    )

    data class Category(
        val categoryId: String,
        val name: String,
        val description: String,
    )

    data class Type(
        val letter: String,
        val name: String,
    )

    data class Account(
        val accountId: String,
        val name: String,
        val type: String,
    )
}

fun AddTransactionStep0Response.toUiModel() = AddTransactionContract.AddTransactionInitialFormData(
    entities = entities.map { it.toUiModel() },
    categories = categories.map { it.toUiModel() },
    accounts = accounts.map { it.toUiModel() },
)

fun EntityResponse.toUiModel() = AddTransactionContract.Entity(
    entityId = this.entityId,
    name = this.name,
)

fun CategoryResponse.toUiModel() = AddTransactionContract.Category(
    categoryId = this.categoryId,
    name = this.name,
    description = this.description
)

fun AccountResponse.toUiModel() = AddTransactionContract.Account(
    accountId = this.accountId,
    name = this.name,
    type = this.type,
)

enum class TrxType(val id: Char) {
    TRANSFER('T'), EXPENSE('E'), INCOME('I')
}