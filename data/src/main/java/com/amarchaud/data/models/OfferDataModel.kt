package com.amarchaud.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OffersDataModel(
    @SerialName("items")
    val items: List<OfferDataModel>? = null,
    @SerialName("totalCount")
    val totalCount: Int? = null,
)

@Serializable
data class OfferDataModel(
    @SerialName("id")
    val id: Int = -1,
    @SerialName("bedrooms")
    val bedrooms: Int? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("area")
    val area: Double? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("price")
    val price: Double? = null,
    @SerialName("professional")
    val professional: String? = null,
    @SerialName("propertyType")
    val propertyType: String? = null,
    @SerialName("offerType")
    val offerType: Int? = null,
    @SerialName("rooms")
    val rooms: Int? = null,
)
