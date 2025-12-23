package com.afaneca.myfin.ui.transactions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afaneca.myfin.domain.common.Result
import com.afaneca.myfin.domain.usecase.GetTransactionListByPageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import com.afaneca.myfin.ui.common.EffectMutableSharedFlow
import com.afaneca.myfin.ui.transactions.list.TransactionListContract.Effect.*
import com.afaneca.myfin.ui.utils.toDisplayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionListByPageUseCase: GetTransactionListByPageUseCase,
) : ViewModel(), TransactionListContract.ViewModel {
    private val _mainState = MutableStateFlow(TransactionListContract.MainState())

    // Derived state with grouped items
    override val mainState = _mainState
        .map { state ->
            state.copy(
                groupedItems = state.items?.groupBy { transaction ->
                    transaction.dateTimestamp.toDisplayDate("dd MMM yyyy")
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionListContract.MainState()
        )

    private val _effect = EffectMutableSharedFlow<TransactionListContract.Effect>()
    override val effect = _effect.asSharedFlow()

    private val _queryDebouncedFlow = MutableStateFlow("")

    init {
        setupQueryDebouncing()
    }

    override fun triggerEvent(event: TransactionListContract.Event) {
        when (event) {
            TransactionListContract.Event.Init -> init()
            TransactionListContract.Event.ClearQueryClicked -> {
                _mainState.update { it.copy(query = "") }
                _queryDebouncedFlow.value = ""
            }

            TransactionListContract.Event.EndOfListReached -> {
                if (!mainState.value.canRequestMoreItems) return
                val page = mainState.value.page + 1
                getTransactions(page = page)
            }

            is TransactionListContract.Event.QueryInputUpdated -> {
                _mainState.update { it.copy(query = event.query) }
                _queryDebouncedFlow.value = event.query
            }

            is TransactionListContract.Event.TransactionClicked -> {
                _effect.tryEmit(GoToTransactionDetails(event.transaction))
            }

            TransactionListContract.Event.NavigationIconClicked -> {
                _effect.tryEmit(OpenNavigationDrawer)
            }

            TransactionListContract.Event.ToolbarSettingsClicked -> {
                _effect.tryEmit(GoToSettings)
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupQueryDebouncing() {
        _queryDebouncedFlow
            .debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                getTransactions(page = 0, query = query)
            }
            .launchIn(viewModelScope)
    }

    private fun init() {
        /*if (mainState.value.isInitialized) return
        _mainState.update { it.copy(isInitialized = true) }*/
        getTransactions()
    }

    private fun getTransactions(
        page: Int = 0,
        pageSize: Int = TransactionListContract.DEFAULT_PAGE_SIZE,
        query: String? = mainState.value.query,
    ) = viewModelScope.launch {
        _mainState.update { it.copy(isLoading = page == 0, isLoadingAdditionalPage = page != 0) }
        val params = GetTransactionListByPageUseCase.Params(
            page = page, pageSize = pageSize, query = query,
        )
        val result = getTransactionListByPageUseCase(params)
        _mainState.update {
            it.copy(
                isLoading = false,
                isLoadingAdditionalPage = false,
                // clear list if on first page
                items = if (page == 0) emptyList() else it.items,
            )
        }
        when (result) {
            is Result.Error -> handleError(error = result.error)
            is Result.Success -> {
                _mainState.update {
                    val updatedItems =
                        if (page == 0 || it.items.isNullOrEmpty())
                            result.data.results
                        else
                            it.items + result.data.results
                    it.copy(
                        page = page,
                        items = updatedItems,
                        canRequestMoreItems = result.data.filteredCount != updatedItems.size,
                    )
                }
            }
        }
    }

    private fun handleError(error: GetTransactionListByPageUseCase.FeatureError) {
        _effect.tryEmit(ShowGenericError())
    }
}
