package com.amarchaud.data

import com.amarchaud.data.api.SimpleListDemoApi
import com.amarchaud.data.mappers.toDomain
import com.amarchaud.data.models.OffersDataModel
import com.amarchaud.data.models.OfferDataModel
import com.amarchaud.data.repository.SimpleListDemoRepositoryImpl
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.repository.SimpleListDemoRepository
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class RepositoryTest {

    private val apiMock: SimpleListDemoApi = mock()

    private lateinit var repository: SimpleListDemoRepository

    private val mockOfferDataModel = OfferDataModel(
        id = 0,
        city = "Paris",
        area = 250.0,
    )

    @Before
    fun setUp() {
        repository = SimpleListDemoRepositoryImpl(
            simpleListDemoApi = apiMock,
        )
    }

    @Test
    fun `check getOffers and map it to domain`() = runTest {
        whenever(apiMock.getOffers()).thenReturn(
            Response.success(
                OffersDataModel(
                    items = List(3) { mockOfferDataModel },
                    totalCount = 3
                )
            )
        )

        val res = repository.getOffers()
        Assert.assertTrue(res.isSuccess)

        val offers = res.getOrThrow()
        Assert.assertTrue(offers.count() == 3)
        Assert.assertTrue(offers == List(3) { mockOfferDataModel.toDomain() })
    }

    @Test
    fun `check getOfferById and map it to domain`() = runTest {
        whenever(apiMock.getOfferById(any())).thenReturn(
            Response.success(
                mockOfferDataModel
            )
        )

        val res = repository.getOfferById(any())
        Assert.assertTrue(res.isSuccess)

        val offer = res.getOrThrow()
        Assert.assertTrue(offer == mockOfferDataModel.toDomain())
    }

    @Test
    fun `check getOffers KO`() = runTest {
        whenever(apiMock.getOffers()).thenReturn(
            Response.error(
                400,
                "{\"error\":[\"error\"]}".toResponseBody()
            )
        )

        val res = repository.getOffers()
        Assert.assertTrue(res.isFailure)

        try {
            res.getOrThrow()
        } catch (e: Throwable) {
            Assert.assertTrue(e is ErrorApiModel.ApiServerErrorWithCode)
        }
    }

    @Test
    fun `check getOfferById KO`() = runTest {
        whenever(apiMock.getOfferById(any())).thenReturn(
            Response.error(
                400,
                "{\"error\":[\"error\"]}".toResponseBody()
            )
        )

        val res = repository.getOfferById(any())
        Assert.assertTrue(res.isFailure)

        try {
            res.getOrThrow()
        } catch (e: Throwable) {
            Assert.assertTrue(e is ErrorApiModel.ApiServerErrorWithCode)
        }
    }
}