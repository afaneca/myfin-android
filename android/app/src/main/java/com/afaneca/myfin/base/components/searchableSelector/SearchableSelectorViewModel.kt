package com.afaneca.myfin.base.components.searchableSelector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

const val SAVED_STATE_INSTANCE_ID_TAG = "SAVED_STATE_INSTANCE_ID_TAG"
const val SAVED_STATE_DATASET_TAG = "SAVED_STATE_DATASET_TAG"

@HiltViewModel
class SearchableSelectorViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), SearchableSelectorContract.ViewModel {

    private var instanceId: String
        get() = savedStateHandle.get<String>(SAVED_STATE_INSTANCE_ID_TAG) ?: ""
        set(value) = savedStateHandle.set(SAVED_STATE_INSTANCE_ID_TAG, value)

    private var dataset: Array<SearchableListItem>
        get() = savedStateHandle.get<Array<SearchableListItem>>(SAVED_STATE_DATASET_TAG)
            ?: emptyArray()
        set(value) = savedStateHandle.set(SAVED_STATE_DATASET_TAG, value)

    private val internalState: MutableSharedFlow<SearchableSelectorContract.State?> =
        MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 5,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override fun subscribeToState() = internalState.asSharedFlow()
    override fun triggerEvent(event: SearchableSelectorContract.Event) {
        when (event) {
            is SearchableSelectorContract.Event.InitList -> init(
                instanceId = event.instanceId,
                dataset = event.dataset
            )
            is SearchableSelectorContract.Event.Itemselected -> onItemSelected(event.item)
        }
    }

    private fun onItemSelected(item: SearchableListItem) {
        internalState.tryEmit(SearchableSelectorContract.State.ItemSelected(item, instanceId))
    }

    private fun init(instanceId: String, dataset: List<SearchableListItem>) {
        this.instanceId = instanceId
        this.dataset = dataset.toTypedArray()
        internalState.tryEmit(SearchableSelectorContract.State.Init(dataset = dataset))
    }
}