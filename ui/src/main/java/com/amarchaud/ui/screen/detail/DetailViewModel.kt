package com.amarchaud.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.usecase.GetOfferByIdUseCase
import com.amarchaud.ui.screen.detail.models.DetailUiModel
import com.amarchaud.ui.screen.mainList.mappers.toErrorUiModel
import com.amarchaud.ui.screen.mainList.mappers.toUiModel
import com.amarchaud.ui.screen.mainList.models.OfferUiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val getOfferByIdUseCase: GetOfferByIdUseCase
) : ViewModel() {

    private val id =
        stateHandle.get<Int>("id") ?: throw NullPointerException("detail can't be null")

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _error: MutableStateFlow<ErrorApiUiModel?> = MutableStateFlow(null)
    private val _detail: MutableStateFlow<OfferUiModel> = MutableStateFlow(OfferUiModel())

    val viewState = combine(
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
            // in a "normal" implementation, we should save the list in a Room Database
            // like that we can display "common" elements between the list and the detail directly
            // and just refresh in background other details
            refreshDetail()
            // for this test, I have simulate a 300 ms api call, to show that the detail is directly displayed
            // when the screen appears
        }
    }

    fun onRefresh() {
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