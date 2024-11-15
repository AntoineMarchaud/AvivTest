package com.amarchaud.domain.usecase

import com.amarchaud.domain.repository.AvivTestRepository
import javax.inject.Inject

class GetOfferByIdUseCase @Inject constructor(
    private val repository: AvivTestRepository
) {
    suspend fun run(id : Int) = repository.getOfferById(id = id)
}