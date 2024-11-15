package com.amarchaud.data.repository

import com.amarchaud.data.api.SimpleListDemoApi
import com.amarchaud.data.api.apiResult
import com.amarchaud.data.mappers.toDomain
import com.amarchaud.data.models.ErrorApiDataModel
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.models.OfferModel
import com.amarchaud.domain.repository.SimpleListDemoRepository
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class SimpleListDemoRepositoryImpl @Inject constructor(
    private val simpleListDemoApi: SimpleListDemoApi,
) : SimpleListDemoRepository {
    override suspend fun getOffers(): Result<List<OfferModel>> {
        runCatching {
            apiResult(simpleListDemoApi.getOffers()).getOrThrow()
        }.fold(
            onSuccess = {
                return Result.success(it.items?.map { element -> element.toDomain() }.orEmpty())
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
            apiResult(simpleListDemoApi.getOfferById(id = id)).getOrThrow()
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