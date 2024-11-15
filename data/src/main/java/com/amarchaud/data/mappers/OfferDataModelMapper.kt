package com.amarchaud.data.mappers

import com.amarchaud.data.models.OfferDataModel
import com.amarchaud.domain.models.OfferModel

internal fun OfferDataModel.toDomain() = OfferModel(
    id = this.id,
    bedrooms = this.bedrooms,
    city = this.city.orEmpty(),
    area = this.area,
    url = this.url,
    price = this.price,
    professional = this.professional.orEmpty(),
    propertyType = this.propertyType.orEmpty(),
    offerType = this.offerType,
    rooms = this.rooms,
)
