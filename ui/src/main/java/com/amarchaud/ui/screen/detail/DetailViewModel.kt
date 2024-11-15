package com.amarchaud.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.usecase.GetOfferByIdUseCase
import com.amarchaud.ui.screen.detail.models.DetailUiModel
import com.amarchaud.ui.screen.mainList.mappers.toErrorUiModel
import com.amarchaud.ui.screen.mainList.mappers.toUiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import com.amarchaud.ui.screen.mainList.models.OfferUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface IDetailViewModel {
    val viewState: StateFlow<DetailUiModel>
    fun onRefresh()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val getOfferByIdUseCase: GetOfferByIdUseCase
) : ViewModel(), IDetailViewModel {

    private val id =
        stateHandle.get<Int>("id") ?: throw NullPointerException("id can't be null")

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _error: MutableStateFlow<ErrorApiUiModel?> = MutableStateFlow(null)
    private val _detail: MutableStateFlow<OfferUiModel> = MutableStateFlow(OfferUiModel())

    override val viewState = combine(
        _isLoading,
        _error,
        _detail
    ) { isLoading, error, detail ->
        DetailUiModel(
            isLoading = isLoading,
            error = error,
            offerUiModel = detail
        )
    }.stateIn(
        viewModelScope, SharingStarted.Lazily, DetailUiModel(
            isLoading = true
        )
    )

    init {
        viewModelScope.launch {
            // different kinds of implementation of a list / detail screens

            //  1 : ROOM
            // save the list in a Database
            // with the "id" you get the element to display directly,
            // refresh specific part with the "details" api method with id
            // 2 : MEMORY
            // give common elements with navigation Compose to the detail screen (id, url, city)
            // refresh specific part with the "details" api method with id

            // For deeplinks navigation :
            // 1 : ROOM
            // if id exists in db, display common part + refresh, otherwise refresh everything with id
            // 2 : MEMORY
            // refresh everything with id

            // for the test, it is ok to call the api directly to display the result
            // so the SharedTransitionLayout can bug because normally you need ZERO latency to display common parts
            refreshDetail()
        }
    }

    override fun onRefresh() {
        viewModelScope.launch {
            refreshDetail()
        }
    }

    private suspend fun refreshDetail() {
        _isLoading.update { true }
        _error.update { null }

        getOfferByIdUseCase.run(id = id)
            .onSuccess { detail ->
                _detail.update { detail.toUiModel() }
            }
            .onFailure { error ->
                _error.update { (error as ErrorApiModel?)?.toErrorUiModel() }
            }

        _isLoading.update { false }
    }
}