package com.amarchaud.domain.usecase

import com.amarchaud.domain.repository.SimpleListDemoRepository
import javax.inject.Inject

class GetOffersUseCase @Inject constructor(
    private val repository: SimpleListDemoRepository
) {
    suspend fun run() = repository.getOffers()
}