package com.amarchaud.ui.screen.mainList.mappers

import com.amarchaud.domain.models.OfferModel
import com.amarchaud.ui.screen.mainList.models.OfferUiModel

internal fun OfferModel.toUiModel() = OfferUiModel(
    id = this.id,
    bedrooms = this.bedrooms ?: -1,
    city = this.city.orEmpty(),
    area = this.area ?: 0.0,
    url = this.url?.toString().orEmpty(),
    price = this.price ?: -1,
    professional = this.professional.orEmpty(),
    propertyType = this.propertyType.orEmpty(),
    offerType = this.offerType ?: -1,
    rooms = this.rooms ?: -1,
)