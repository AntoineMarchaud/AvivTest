package com.amarchaud.data.api

import com.amarchaud.data.models.OfferDataModel
import com.amarchaud.data.models.AvivOffersDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AvivTestApi {
    @GET("/listings.json")
    suspend fun getOffers(): Response<AvivOffersDataModel>

    @GET("/listings/{id}.json")
    suspend fun getOfferById(@Path("id") id: Int): Response<OfferDataModel>
}