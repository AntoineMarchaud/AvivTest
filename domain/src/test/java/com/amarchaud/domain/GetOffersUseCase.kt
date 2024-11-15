package com.amarchaud.domain

import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.models.OfferModel
import com.amarchaud.domain.repository.SimpleListDemoRepository
import com.amarchaud.domain.usecase.GetOffersUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetOffersUseCase {

    private val repositoryMock: SimpleListDemoRepository = mock()
    private lateinit var useCase: GetOffersUseCase

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

    private val mockOfferList = List(3) { mockOfferModel }

    @Before
    fun setUp() {
        useCase = GetOffersUseCase(repository = repositoryMock)
    }

    @Test
    fun `GetOffersUseCase ok`() = runTest {
        whenever(repositoryMock.getOffers()).thenReturn(Result.success(mockOfferList))

        val res = useCase.run()
        Assert.assertTrue(res.isSuccess)
        Assert.assertTrue(res.getOrThrow() == mockOfferList)
    }

    @Test
    fun `GetOffersUseCase ko`() = runTest {
        whenever(repositoryMock.getOffers()).thenReturn(Result.failure(ErrorApiModel.NoInternetError()))

        val res = useCase.run()
        Assert.assertTrue(res.isFailure)
        try {
            res.getOrThrow()
        } catch (e: Throwable) {
            Assert.assertTrue(e is ErrorApiModel.NoInternetError)
        }
    }
}
