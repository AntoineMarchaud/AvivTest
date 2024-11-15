package com.amarchaud.domain.usecase

import com.amarchaud.domain.repository.AvivTestRepository
import javax.inject.Inject

class GetOffersUseCase @Inject constructor(
    private val repository: AvivTestRepository
) {
    suspend fun run() = repository.getOffers()
}