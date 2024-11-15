package com.amarchaud.data.repository

import com.amarchaud.data.api.AvivTestApi
import com.amarchaud.data.api.apiResult
import com.amarchaud.data.mappers.toDomain
import com.amarchaud.data.models.ErrorApiDataModel
import com.amarchaud.domain.models.OfferModel
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.repository.AvivTestRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AvivTestRepositoryImpl @Inject constructor(
    private val avivTestApi: AvivTestApi,
) : AvivTestRepository {
    override suspend fun getOffers(): Result<List<OfferModel>> {
        runCatching {
            delay(300)
            apiResult(avivTestApi.getOffers()).getOrThrow()
        }.fold(
            onSuccess = {
                return Result.success(it.items.map { element -> element.toDomain() })
            },
            onFailure = {
                return when (it) {
                    is ErrorApiDataModel -> Result.failure(it.toDomain())
                    is CancellationException -> Result.failure(ErrorApiModel.CancellationError())
                    else -> Result.failure(ErrorApiModel.GenericError())
                }
            }
        )
    }

    override suspend fun getOfferById(id: Int): Result<OfferModel> {
        runCatching {
            delay(300)
            apiResult(avivTestApi.getOfferById(id = id)).getOrThrow()
        }.fold(
            onSuccess = {
                return Result.success(it.toDomain())
            },
            onFailure = {
                return when (it) {
                    is ErrorApiDataModel -> Result.failure(it.toDomain())
                    is CancellationException -> Result.failure(ErrorApiModel.CancellationError())
                    else -> Result.failure(ErrorApiModel.GenericError())
                }
            }
        )
    }
}