package com.amarchaud.ui

import androidx.lifecycle.SavedStateHandle
import com.amarchaud.domain.models.ErrorApiModel
import com.amarchaud.domain.models.OfferModel
import com.amarchaud.domain.usecase.GetOfferByIdUseCase
import com.amarchaud.ui.screen.detail.DetailViewModel
import com.amarchaud.ui.screen.detail.IDetailViewModel
import com.amarchaud.ui.screen.detail.models.DetailUiModel
import com.amarchaud.ui.screen.mainList.mappers.toUiModel
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val getOfferByIdUseCase: GetOfferByIdUseCase = mock()
    private val stateHandle: SavedStateHandle = mock()

    private lateinit var viewModel: IDetailViewModel


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
        whenever(stateHandle.get<Int>("id")).thenReturn(0)
        whenever(getOfferByIdUseCase.run(any())).thenReturn(
            Result.success(mockOfferModel)
        )
        val offersUiModelOut =  mockOfferModel.toUiModel()

        viewModel =
            DetailViewModel(
                stateHandle = stateHandle,
                getOfferByIdUseCase = getOfferByIdUseCase
            )

        val values = mutableListOf(DetailUiModel())
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect {
                values.add(it)
            }
        }

        advanceUntilIdle()

        with(values.last()) {
            Assert.assertFalse(isLoading)
            Assert.assertNull(error)
            Assert.assertTrue(offerUiModel == offersUiModelOut)
        }

        job.cancel()
    }

    @Test
    fun `getUsers ko`() = runTest {
        whenever(stateHandle.get<Int>("id")).thenReturn(0)
        whenever(getOfferByIdUseCase.run(any())).thenReturn(
            Result.failure(ErrorApiModel.NoInternetError())
        )

        viewModel =
            DetailViewModel(
                stateHandle = stateHandle,
                getOfferByIdUseCase = getOfferByIdUseCase
            )

        val values = mutableListOf(DetailUiModel())
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect {
                values.add(it)
            }
        }

        advanceUntilIdle()

        with(values.last()) {
            Assert.assertFalse(isLoading)
            Assert.assertTrue(error is ErrorApiUiModel.NoInternetError)
        }

        job.cancel()
    }
}