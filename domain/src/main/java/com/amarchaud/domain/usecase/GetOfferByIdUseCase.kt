package com.amarchaud.domain.usecase

import com.amarchaud.domain.repository.SimpleListDemoRepository
import javax.inject.Inject

class GetOfferByIdUseCase @Inject constructor(
    private val repository: SimpleListDemoRepository
) {
    suspend fun run(id : Int) = repository.getOfferById(id = id)
}