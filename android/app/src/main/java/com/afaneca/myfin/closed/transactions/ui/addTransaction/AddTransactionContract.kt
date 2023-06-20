package com.afaneca.myfin.closed.transactions.ui.addTransaction

import androidx.lifecycle.LiveData
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.closed.transactions.data.AccountResponse
import com.afaneca.myfin.closed.transactions.data.AddTransactionStep0Response
import com.afaneca.myfin.closed.transactions.data.CategoryResponse
import com.afaneca.myfin.closed.transactions.data.EntityResponse
import com.afaneca.myfin.closed.transactions.data.TypeResponse

sealed class AddTransactionContract {

    interface ViewModel {
        val state: LiveData<State>

        fun triggerEvent(event: Event)
    }

    sealed class State {
        object Loading : State()
        object Failure : State()
        data class InitForm(
            val formData: AddTransactionInitialFormData,
            val trx: MyFinTransaction? = null
        ) : State()

        data class ToggleAddButton(val isToEnable: Boolean) : State()
        data class ToggleAccountFrom(val isToEnable: Boolean) : State()
        data class ToggleAccountTo(val isToEnable: Boolean) : State()
        data class ToggleEssential(val isToShow: Boolean) : State()
        object AddTransactionSuccess : State()
    }

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

    data class AddTransactionInitialFormData(
        val entities: List<Entity>,
        val categories: List<Category>,
        val types: List<Type>,
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
    types = types.map { it.toUiModel() },
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

fun TypeResponse.toUiModel() = AddTransactionContract.Type(
    letter = this.letter,
    name = this.name,
)

fun AccountResponse.toUiModel() = AddTransactionContract.Account(
    accountId = this.accountId,
    name = this.name,
    type = this.type,
)

enum class TrxType(val id: Char) {
    TRANSFER('T'), EXPENSE('E'), INCOME('I')
}