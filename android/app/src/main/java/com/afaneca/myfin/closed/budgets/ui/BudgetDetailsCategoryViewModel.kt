package com.afaneca.myfin.closed.budgets.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.repository.BudgetsRepository
import com.afaneca.myfin.data.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetDetailsCategoryViewModel @Inject constructor(
    private val repository: BudgetsRepository
) : ViewModel() {

    sealed class Action {
        data class OnSaveButtonClick(
            val budgetId: String,
            val catId: String,
            val plannedAmountDebit: String,
            val plannedAmountCredit: String
        ) : Action()
    }

    sealed class Event {
        data class ShowError(val message: String) : Event()
        data class GoToBudget(val budgetId: String) : Event()
    }

    sealed class State {
        object Loading : State()
        object NotLoading : State()
    }

    private val _viewEvent = MutableLiveData<Event>()
    val viewEvent: LiveData<Event> get() = _viewEvent

    private val _viewState = MutableLiveData<State>()
    val viewState: LiveData<State> get() = _viewState

    fun performAction(action: Action) {
        when (action) {
            is Action.OnSaveButtonClick -> onSaveButtonClick(
                action.budgetId,
                action.catId,
                action.plannedAmountDebit,
                action.plannedAmountCredit
            )
        }
    }

    private fun onSaveButtonClick(
        budgetId: String,
        catId: String,
        plannedAmountDebit: String,
        plannedAmountCredit: String
    ) {
        viewModelScope.launch {
            val resource = repository.updateBudgetCategoryAmounts(
                budgetId,
                catId,
                plannedAmountDebit,
                plannedAmountCredit
            )
            when (resource) {
                is Resource.Loading -> _viewState.postValue(State.Loading)
                is Resource.Failure -> _viewEvent.postValue(Event.ShowError(resource.errorMessage))
                is Resource.Success -> _viewEvent.postValue(Event.GoToBudget(budgetId))
            }
        }
    }

}