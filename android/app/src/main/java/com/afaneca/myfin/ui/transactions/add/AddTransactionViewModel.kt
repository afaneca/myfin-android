package com.afaneca.myfin.ui.transactions.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.model.TransactionType
import com.afaneca.myfin.domain.model.UserAccount
import com.afaneca.myfin.domain.usecase.AddTransactionUseCase
import com.afaneca.myfin.domain.usecase.AutoCategorizeTransactionUseCase
import com.afaneca.myfin.domain.usecase.GetAddTransactionFormDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val getFormDataUseCase: GetAddTransactionFormDataUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val autoCatUseCase: AutoCategorizeTransactionUseCase,
) : ViewModel(), AddTransactionContract.ViewModel {

    private val _mainState = MutableStateFlow(AddTransactionContract.MainState())
    override val mainState = _mainState.asStateFlow()

    private val _effect = MutableSharedFlow<AddTransactionContract.Effect>(extraBufferCapacity = 1)
    override val effect: SharedFlow<AddTransactionContract.Effect> = _effect.asSharedFlow()

    override fun triggerEvent(event: AddTransactionContract.Event) {
        when (event) {
            AddTransactionContract.Event.Init -> init()

            is AddTransactionContract.Event.AccountFromUpdated -> {
                _mainState.update {
                    it.copy(
                        accountFrom = event.account,
                        isSubmitButtonEnabled = isInputValid(accountFrom = event.account),
                    )
                }
            }

            is AddTransactionContract.Event.AccountToUpdated -> {
                _mainState.update {
                    it.copy(
                        accountTo = event.account,
                        isSubmitButtonEnabled = isInputValid(accountTo = event.account),
                    )
                }
            }

            is AddTransactionContract.Event.AmountUpdated -> {
                _mainState.update {
                    it.copy(
                        amount = event.amount,
                        isSubmitButtonEnabled = isInputValid(amount = event.amount),
                    )
                }
            }

            AddTransactionContract.Event.AutoCategorizeClicked -> {
                val state = mainState.value
                val description = state.description ?: return
                val amount = state.amount ?: return
                viewModelScope.launch {
                    _mainState.update { it.copy(isLoadingAutoCategorization = true, isSubmitButtonEnabled = false) }
                    when (val result = autoCatUseCase(
                        AutoCategorizeTransactionUseCase.Params(
                            description = description,
                            amount = amount,
                            accountFromId = state.accountFrom?.id,
                            accountToId = state.accountTo?.id,
                        )
                    )) {
                        is Result.Success -> {
                            val suggestion = result.data
                            val matched = mainState.value
                            val category = matched.availableCategories.find { it.categoryId == suggestion.categoryId }
                            val entity = matched.availableEntities.find { it.entityId == suggestion.entityId }
                            _mainState.update {
                                it.copy(
                                    isLoadingAutoCategorization = false,
                                    category = category ?: it.category,
                                    entity = entity ?: it.entity,
                                    isSubmitButtonEnabled = isInputValid(isLoadingAutoCategorization = false),
                                )
                            }
                        }
                        is Result.Error -> {
                            _mainState.update {
                                it.copy(
                                    isLoadingAutoCategorization = false,
                                    isSubmitButtonEnabled = isInputValid(isLoadingAutoCategorization = false),
                                )
                            }
                            _effect.tryEmit(AddTransactionContract.Effect.ShowGenericError())
                        }
                    }
                }
            }

            is AddTransactionContract.Event.CategoryUpdated -> {
                _mainState.update {
                    it.copy(category = event.category, isSubmitButtonEnabled = isInputValid())
                }
            }

            is AddTransactionContract.Event.DateUpdated -> {
                _mainState.update {
                    it.copy(
                        date = event.date,
                        isSubmitButtonEnabled = isInputValid(date = event.date),
                    )
                }
            }

            is AddTransactionContract.Event.DescriptionUpdated -> {
                _mainState.update {
                    it.copy(description = event.description, isSubmitButtonEnabled = isInputValid())
                }
            }

            is AddTransactionContract.Event.EntityUpdated -> {
                _mainState.update {
                    it.copy(entity = event.entity, isSubmitButtonEnabled = isInputValid())
                }
            }

            is AddTransactionContract.Event.IsEssentialUpdated -> {
                _mainState.update {
                    it.copy(isEssential = event.isEssential, isSubmitButtonEnabled = isInputValid())
                }
            }

            is AddTransactionContract.Event.TypeUpdated -> {
                _mainState.update {
                    val isAccountFromVisible = event.type != TransactionType.INCOME
                    val isAccountToVisible = event.type != TransactionType.EXPENSE
                    val accountFrom = it.accountFrom.takeIf { isAccountFromVisible }
                    val accountTo = it.accountTo.takeIf { isAccountToVisible }
                    it.copy(
                        type = event.type,
                        accountFrom = accountFrom,
                        accountTo = accountTo,
                        isAccountFromVisible = isAccountFromVisible,
                        isAccountToVisible = isAccountToVisible,
                        isSubmitButtonEnabled = isInputValid(
                            type = event.type,
                            accountFrom = accountFrom,
                            accountTo = accountTo,
                        ),
                    )
                }
            }

            AddTransactionContract.Event.SaveClicked -> {
                val state = mainState.value
                val type = state.type ?: return
                val amount = state.amount ?: return
                val date = state.date ?: return
                viewModelScope.launch {
                    _mainState.update { it.copy(isLoading = true, isSubmitButtonEnabled = false) }
                    when (addTransactionUseCase(
                        AddTransactionUseCase.Params(
                            type = type,
                            amount = amount,
                            description = state.description,
                            entityId = state.entity?.entityId,
                            accountFromId = state.accountFrom?.id,
                            accountToId = state.accountTo?.id,
                            categoryId = state.category?.categoryId,
                            dateTimestamp = date,
                            isEssential = state.isEssential,
                        )
                    )) {
                        is Result.Success -> {
                            _mainState.update { it.copy(isLoading = false) }
                            _effect.tryEmit(AddTransactionContract.Effect.GoBackToList)
                        }
                        is Result.Error -> {
                            _mainState.update {
                                it.copy(isLoading = false, isSubmitButtonEnabled = isInputValid())
                            }
                            _effect.tryEmit(AddTransactionContract.Effect.ShowGenericError())
                        }
                    }
                }
            }
        }
    }

    private fun init() {
        if (mainState.value.isInitialized) return

        val defaultDate = System.currentTimeMillis() / 1000
        _mainState.update {
            it.copy(
                isInitialized = true,
                date = defaultDate,
                isSubmitButtonEnabled = isInputValid(date = defaultDate),
            )
        }

        viewModelScope.launch {
            _mainState.update { it.copy(isLoadingFormData = true) }
            when (val result = getFormDataUseCase(Unit)) {
                is Result.Success -> {
                    _mainState.update {
                        it.copy(
                            isLoadingFormData = false,
                            availableAccounts = result.data.accounts,
                            availableCategories = result.data.categories,
                            availableEntities = result.data.entities,
                        )
                    }
                }
                is Result.Error -> {
                    _mainState.update { it.copy(isLoadingFormData = false) }
                    _effect.tryEmit(AddTransactionContract.Effect.ShowGenericError())
                }
            }
        }
    }

    private fun isInputValid(
        type: TransactionType? = mainState.value.type,
        date: Long? = mainState.value.date,
        amount: BigDecimal? = mainState.value.amount,
        accountFrom: UserAccount? = mainState.value.accountFrom,
        accountTo: UserAccount? = mainState.value.accountTo,
        isLoadingAutoCategorization: Boolean = mainState.value.isLoadingAutoCategorization,
    ): Boolean {
        return type != null
            && date != null
            && amount != null
            && amount > BigDecimal.ZERO
            && (type == TransactionType.INCOME || accountFrom != null)
            && (type == TransactionType.EXPENSE || accountTo != null)
            && !isLoadingAutoCategorization
    }
}
