package com.amarchaud.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AvivOffersDataModel(
    @field:Json(name = "items")
    val items: List<OfferDataModel> = emptyList(),
    @field:Json(name = "totalCount")
    val totalCount: Int? = null,
)

@JsonClass(generateAdapter = true)
data class OfferDataModel(
    @field:Json(name = "id")
    val id: Int = -1,
    @field:Json(name = "bedrooms")
    val bedrooms: Int? = null,
    @field:Json(name = "city")
    val city: String? = null,
    @field:Json(name = "area")
    val area: Double? = null,
    @field:Json(name = "url")
    val url: String? = null,
    @field:Json(name = "price")
    val price: Int? = null,
    @field:Json(name = "professional")
    val professional: String? = null,
    @field:Json(name = "propertyType")
    val propertyType: String? = null,
    @field:Json(name = "offerType")
    val offerType: Int? = null,
    @field:Json(name = "rooms")
    val rooms: Int? = null,
)
