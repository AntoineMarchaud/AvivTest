package com.amarchaud.ui.screen.mainList.models

import kotlinx.serialization.Serializable

@Serializable
data class OfferUiModel(
    val id: Int = 0,
    val bedrooms: Int = 0,
    val city: String = String(),
    val area: Double = 0.0,
    val url: String = String(),
    val price: Int = -1,
    val professional: String = String(),
    val propertyType: String = String(),
    val offerType: Int = -1,
    val rooms: Int = -1,
)