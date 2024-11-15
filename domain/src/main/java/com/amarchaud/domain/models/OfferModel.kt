package com.amarchaud.domain.models

data class OfferModel(
    val id: Int,
    val bedrooms: Int?,
    val city: String?,
    val area: Double?,
    val url: String?,
    val price: Double?,
    val professional: String?,
    val propertyType: String?,
    val offerType: Int?,
    val rooms: Int?,
)
