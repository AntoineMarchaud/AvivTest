package com.amarchaud.domain.models

import android.net.Uri

data class OfferModel(
    val id: Int,
    val bedrooms: Int?,
    val city: String?,
    val area: Double?,
    val url: Uri?,
    val price: Int?,
    val professional: String?,
    val propertyType: String?,
    val offerType: Int?,
    val rooms: Int?,
)
