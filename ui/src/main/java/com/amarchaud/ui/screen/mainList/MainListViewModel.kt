package com.amarchaud.ui.screen.mainList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.usecase.GetOffersUseCase
import com.amarchaud.ui.screen.mainList.mappers.toErrorUiModel
import com.amarchaud.ui.screen.mainList.mappers.toUiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import com.amarchaud.ui.screen.mainList.models.MainUiModel
import com.amarchaud.ui.screen.mainList.models.OfferUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface IMainListViewModel {
    val viewState: StateFlow<MainUiModel>
    fun onRefresh()
}

@HiltViewModel
class MainListViewModel @Inject constructor(
    private val getOffersUseCase: GetOffersUseCase
) : ViewModel(), IMainListViewModel {

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _error: MutableStateFlow<ErrorApiUiModel?> = MutableStateFlow(null)
    private val _offers: MutableStateFlow<ImmutableList<OfferUiModel>> =
        MutableStateFlow(persistentListOf())

    override val viewState = combine(
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

    override fun onRefresh() {
        viewModelScope.launch {
            refreshAll()
        }
    }

    private suspend fun refreshAll() {
        _isLoading.update { true }
        _error.update { null }

        delay(300) // simulate latency
        getOffersUseCase.run()
            .onSuccess { offers ->
                _offers.update { offers.map { it.toUiModel() }.toImmutableList() }
            }.onFailure { error ->
                _error.update { (error as ErrorApiModel?)?.toErrorUiModel() }
            }

        _isLoading.update { false }
    }
}