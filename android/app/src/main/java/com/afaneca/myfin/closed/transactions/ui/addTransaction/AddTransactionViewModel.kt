package com.afaneca.myfin.closed.transact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.closed.transactions.data.TransactionsRepository
import com.afaneca.myfin.closed.transactions.ui.addTransaction.AddTransactionContract
import com.afaneca.myfin.closed.transactions.ui.addTransaction.TrxType
import com.afaneca.myfin.closed.transactions.ui.addTransaction.toUiModel
import com.afaneca.myfin.data.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), AddTransactionContract.ViewModel {

    private val _state =
        MutableStateFlow(AddTransactionContract.State())
    override val state = _state.asStateFlow()

    private var isEditing: Boolean
        get() = savedStateHandle.get<Boolean>(IS_EDITING_SAVED_STATE_HANDLE_TAG) ?: false
        set(value) = savedStateHandle.set(IS_EDITING_SAVED_STATE_HANDLE_TAG, value)

    private var dateSelectedInput: Long? = null
    private var accountFromSelectedInput: String? = null
    private var accountToSelectedInput: String? = null
    private var categorySelectedInput: String? = null
    private var entitySelectedInput: String? = null
    private var amountInput: String? = null
    private var descriptionInput: String? = null
    private var typeSelectedInput: Char? = TrxType.EXPENSE.id
    private var isEssentialInput: Boolean = false

    private fun initForm(trx: MyFinTransaction? = null) {
        viewModelScope.launch {
            when (val resource = transactionsRepository.addTransactionStep0()) {
                is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                is Resource.Failure -> _state.update {
                    it.copy(
                        isLoading = false,
                        error = resource.errorMessage
                    )
                }

                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            formData = resource.data.toUiModel(),
                            trx = trx,
                        )
                    }

                }
            }
        }
    }

    override fun triggerEvent(event: AddTransactionContract.Event) {
        when (event) {
            is AddTransactionContract.Event.InitForm -> {
                initForm(event.trx)
                isEditing = event.trx != null
            }

            is AddTransactionContract.Event.DateSelected -> {
                dateSelectedInput = event.timestamp
                _state.update { it.copy(isAddButtonEnabled = isFormValid()) }
            }

            is AddTransactionContract.Event.AccountFromSelected -> {
                accountFromSelectedInput = event.accountId
                _state.update { it.copy(isAddButtonEnabled = isFormValid()) }
            }

            is AddTransactionContract.Event.AccountToSelected -> {
                accountToSelectedInput = event.accountId
                _state.update { it.copy(isAddButtonEnabled = isFormValid()) }
            }

            is AddTransactionContract.Event.CategorySelected -> {
                categorySelectedInput = event.categoryId
                _state.update { it.copy(isAddButtonEnabled = isFormValid()) }
            }

            is AddTransactionContract.Event.EntitySelected -> {
                entitySelectedInput = event.entityId
                _state.update { it.copy(isAddButtonEnabled = isFormValid()) }
            }

            is AddTransactionContract.Event.AmountInserted -> {
                amountInput = event.amount
                _state.update { it.copy(isAddButtonEnabled = isFormValid()) }
            }

            is AddTransactionContract.Event.DescriptionChanged -> {
                descriptionInput = event.description
                _state.update { it.copy(isAddButtonEnabled = isFormValid()) }

            }

            is AddTransactionContract.Event.TypeSelected -> {
                onTypeSelected(event.typeId)
            }

            is AddTransactionContract.Event.EssentialToggled -> {
                isEssentialInput = event.selected
                _state.update { it.copy(isAddButtonEnabled = isFormValid()) }
            }

            is AddTransactionContract.Event.AddEditTransactionClick -> onAddTransactionClick()
        }
    }

    private fun onTypeSelected(typeId: Char) {
        typeSelectedInput = typeId
        _state.update { it.copy(isAddButtonEnabled = isFormValid()) }
        _state.update { it.copy(isAccountFromEnabled = typeId == TrxType.TRANSFER.id || typeId == TrxType.EXPENSE.id) }
        _state.update { it.copy(isAccountToEnabled = typeId == TrxType.TRANSFER.id || typeId == TrxType.INCOME.id) }
        _state.update { it.copy(isEssentialToggleVisible = typeId == TrxType.EXPENSE.id) }
    }

    private fun isFormValid() =
        dateSelectedInput != null
                && !amountInput.isNullOrBlank()
                && (typeSelectedInput == TrxType.EXPENSE.id && !accountFromSelectedInput.isNullOrBlank()
                || typeSelectedInput == TrxType.INCOME.id && !accountToSelectedInput.isNullOrBlank()
                || typeSelectedInput == TrxType.TRANSFER.id
                && !accountToSelectedInput.isNullOrBlank()
                && !accountFromSelectedInput.isNullOrBlank())

    private fun onAddTransactionClick() {
        if (!isEditing) {
            // at this point, we already now all the form input fields are valid
            addTransaction(
                dateSelectedInput?.div(1000L),
                if (typeSelectedInput != TrxType.INCOME.id) accountFromSelectedInput else null,
                if (typeSelectedInput != TrxType.EXPENSE.id) accountToSelectedInput else null,
                categorySelectedInput,
                entitySelectedInput,
                amountInput,
                typeSelectedInput,
                descriptionInput,
                if (typeSelectedInput == TrxType.EXPENSE.id) isEssentialInput else false,
            )
        }
    }

    private fun addTransaction(
        dateSelectedInput: Long?,
        accountFromSelectedInput: String?,
        accountToSelectedInput: String?,
        categorySelectedInput: String?,
        entitySelectedInput: String?,
        amountInput: String?,
        type: Char?,
        description: String?,
        isEssential: Boolean
    ) {
        viewModelScope.launch {
            when (val response = transactionsRepository.addTransactionStep1(
                dateSelectedInput ?: 0L,
                amountInput ?: "",
                type ?: ' ',
                accountFromSelectedInput,
                accountToSelectedInput,
                description ?: "",
                entitySelectedInput,
                categorySelectedInput,
                isEssential
            )) {
                is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                is Resource.Failure -> _state.update {
                    it.copy(
                        isLoading = false,
                        error = response.errorMessage
                    )
                }

                is Resource.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true
                    )
                }
            }
        }
    }

    companion object {
        private const val IS_EDITING_SAVED_STATE_HANDLE_TAG = "IS_EDITING_SAVED_STATE_HANDLE_TAG"
    }
}

