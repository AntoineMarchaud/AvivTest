package com.amarchaud.ui

import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.models.OfferModel
import com.amarchaud.domain.usecase.GetOffersUseCase
import com.amarchaud.ui.screen.mainList.IMainListViewModel
import com.amarchaud.ui.screen.mainList.MainListViewModel
import com.amarchaud.ui.screen.mainList.mappers.toUiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import com.amarchaud.ui.screen.mainList.models.MainUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainListViewModelTest {
    private val getOffersUseCase: GetOffersUseCase = mock()
    private lateinit var viewModel: IMainListViewModel


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

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getUsers ok`() = runTest {
        whenever(getOffersUseCase.run()).thenReturn(
            Result.success(mockOfferList)
        )
        val offersUiModelOut =  mockOfferList.map { it.toUiModel() }

        viewModel =
            MainListViewModel(getOffersUseCase = getOffersUseCase)

        val values = mutableListOf(MainUiModel())
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect {
                values.add(it)
            }
        }

        advanceUntilIdle()

        with(values.last()) {
            Assert.assertFalse(isLoading)
            Assert.assertNull(error)
            Assert.assertTrue(offersUiModel == offersUiModelOut)
        }

        job.cancel()
    }

    @Test
    fun `getUsers ko`() = runTest {
        whenever(getOffersUseCase.run()).thenReturn(
            Result.failure(ErrorApiModel.NoInternetError())
        )

        viewModel =
            MainListViewModel(getOffersUseCase = getOffersUseCase)

        val values = mutableListOf(MainUiModel())
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect {
                values.add(it)
            }
        }

        advanceUntilIdle()

        with(values.last()) {
            Assert.assertFalse(isLoading)
            Assert.assertTrue(error is ErrorApiUiModel.NoInternetError)
            Assert.assertTrue(offersUiModel.isEmpty())
        }

        job.cancel()
    }
}