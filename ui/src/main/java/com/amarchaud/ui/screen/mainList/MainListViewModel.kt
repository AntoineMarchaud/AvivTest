package com.amarchaud.ui.screen.mainList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.usecase.GetOffersUseCase
import com.amarchaud.ui.screen.mainList.mappers.toErrorUiModel
import com.amarchaud.ui.screen.mainList.mappers.toUiModel
import com.amarchaud.ui.screen.mainList.models.OfferUiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import com.amarchaud.ui.screen.mainList.models.MainUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainListViewModel @Inject constructor(
    private val getOffersUseCase: GetOffersUseCase
) : ViewModel() {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _error: MutableStateFlow<ErrorApiUiModel?> = MutableStateFlow(null)
    private val _offers: MutableStateFlow<List<OfferUiModel>> = MutableStateFlow(emptyList())

    val viewState = combine(
        _isLoading,
        _error,
        _offers
    ) { isLoading, error, data ->
        MainUiModel(
            isLoading = isLoading,
            error = error,
            offersUiModel = data
        )
    }.stateIn(
        viewModelScope, SharingStarted.Lazily, MainUiModel(
            isLoading = true
        )
    )

    init {
        viewModelScope.launch {
            refreshAll()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            refreshAll()
        }
    }

    private suspend fun refreshAll() {
        _isLoading.update { true }
        _error.update { null }

        getOffersUseCase.run()
            .onSuccess { offers ->
                _offers.update { offers.map { it.toUiModel() } }
            }.onFailure { error ->
                _error.update { (error as ErrorApiModel?)?.toErrorUiModel() }
            }

        _isLoading.update { false }
    }
}