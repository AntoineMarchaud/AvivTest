package com.amarchaud.domain

import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.models.OfferModel
import com.amarchaud.domain.repository.SimpleListDemoRepository
import com.amarchaud.domain.usecase.GetOfferByIdUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetOfferByIdUseCase {
    private val repositoryMock: SimpleListDemoRepository = mock()
    private lateinit var useCase: GetOfferByIdUseCase

    private val mockOfferModel = OfferModel(
        id = 0,
        bedrooms = 1,
        city = "Paris",
        area = 0.0,
        url = null,
        price = 0.0,
        offerType = 0,
        rooms = 0,
        professional = "",
        propertyType = ""
    )

    @Before
    fun setUp() {
        useCase = GetOfferByIdUseCase(repository = repositoryMock)
    }

    @Test
    fun `GetOfferByIdUseCase ok`() = runTest {
        whenever(repositoryMock.getOfferById(any())).thenReturn(Result.success(mockOfferModel))

        val res = useCase.run(0)
        Assert.assertTrue(res.isSuccess)
        Assert.assertTrue(res.getOrThrow() == mockOfferModel)
    }

    @Test
    fun `GetOfferByIdUseCase ko`() = runTest {
        whenever(repositoryMock.getOfferById(any())).thenReturn(Result.failure(ErrorApiModel.NoInternetError()))

        val res = useCase.run(0)
        Assert.assertTrue(res.isFailure)
        try {
            res.getOrThrow()
        } catch (e: Throwable) {
            Assert.assertTrue(e is ErrorApiModel.NoInternetError)
        }
    }
}