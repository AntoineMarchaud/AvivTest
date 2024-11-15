package com.amarchaud.domain.repository

import com.amarchaud.domain.models.OfferModel

interface SimpleListDemoRepository {
    suspend fun getOffers(): Result<List<OfferModel>>
    suspend fun getOfferById(id: Int): Result<OfferModel>
}