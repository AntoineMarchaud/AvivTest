package com.amarchaud.ui.screen.mainList.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MainUiModel(
    val isLoading: Boolean = false,
    val error: ErrorApiUiModel? = null,
    val offersUiModel: ImmutableList<OfferUiModel> = persistentListOf()
)
