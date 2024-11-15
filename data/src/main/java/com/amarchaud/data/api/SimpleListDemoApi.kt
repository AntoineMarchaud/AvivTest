package com.amarchaud.data.api

import com.amarchaud.data.models.OffersDataModel
import com.amarchaud.data.models.OfferDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SimpleListDemoApi {
    @GET("/listings.json")
    suspend fun getOffers(): Response<OffersDataModel>

    @GET("/listings/{id}.json")
    suspend fun getOfferById(@Path("id") id: Int): Response<OfferDataModel>
}