package com.amarchaud.ui.screen.detail.models

import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import com.amarchaud.ui.screen.mainList.models.OfferUiModel

data class DetailUiModel(
    val isLoading: Boolean = false,
    val error: ErrorApiUiModel? = null,
    val offerUiModel: OfferUiModel = OfferUiModel()
)
