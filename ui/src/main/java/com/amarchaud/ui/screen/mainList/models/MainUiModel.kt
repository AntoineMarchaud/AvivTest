package com.amarchaud.ui.screen.mainList.models

data class MainUiModel(
    val isLoading: Boolean = false,
    val error: ErrorApiUiModel? = null,
    val offersUiModel: List<OfferUiModel> = emptyList()
)
