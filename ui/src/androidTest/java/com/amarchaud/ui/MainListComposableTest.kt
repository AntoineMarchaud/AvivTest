import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.amarchaud.ui.R
import com.amarchaud.ui.screen.mainList.MainListScreenHackForPreview
import com.amarchaud.ui.screen.mainList.TestTagEmpty
import com.amarchaud.ui.screen.mainList.TestTagError
import com.amarchaud.ui.screen.mainList.TestTagOffer
import com.amarchaud.ui.screen.mainList.TestTagShimmer
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import com.amarchaud.ui.screen.mainList.models.MainUiModel
import com.amarchaud.ui.screen.mainList.models.OfferUiModel
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalSharedTransitionApi::class)
class MainListComposableTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadingState_showsShimmerItems() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    MainListScreenHackForPreview(
                        viewState = MainUiModel(
                            isLoading = true,
                            error = null,
                            offersUiModel = persistentListOf()
                        ),
                        onClick = {},
                        onRefresh = {},
                        animatedVisibilityScope = this
                    )
                }
            }
        }

        // at least one shimmer
        composeTestRule.onNodeWithTag("${TestTagShimmer}0").assertExists()
    }

    @Test
    fun testErrorState_displaysErrorMessage() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    MainListScreenHackForPreview(
                        viewState = MainUiModel(
                            isLoading = false,
                            error = ErrorApiUiModel.NoInternetError,
                            offersUiModel = persistentListOf()
                        ),
                        onClick = {},
                        onRefresh = {},
                        animatedVisibilityScope = this
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(TestTagError).assertExists()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val errorString = context.getString(R.string.error_refresh, context.getString(R.string.error_NoInternetError))
        composeTestRule.onNodeWithText(errorString).assertExists()
    }

    @Test
    fun testItemsDisplayedInGrid() {
        val items = persistentListOf(
            OfferUiModel(id = 1, city = "Paris"),
            OfferUiModel(id = 2, city = "Chartres")
        )

        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    MainListScreenHackForPreview(
                        viewState = MainUiModel(
                            isLoading = false,
                            error = null,
                            offersUiModel = items
                        ),
                        onClick = {},
                        onRefresh = {},
                        animatedVisibilityScope = this
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("${TestTagOffer}${0}").assertExists()
        composeTestRule.onNodeWithText("Chartres").assertExists()
    }

    @Test
    fun testClickOnItem_callsOnClick() {
        var clickedItemId: Int? = null
        val items = persistentListOf(
            OfferUiModel(id = 1),
            OfferUiModel(id = 2)
        )

        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    MainListScreenHackForPreview(
                        viewState = MainUiModel(
                            isLoading = false,
                            error = null,
                            offersUiModel = items
                        ),
                        onClick = { clickedItemId = it },
                        onRefresh = {},
                        animatedVisibilityScope = this
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("${TestTagOffer}${0}").performClick()
        assert(clickedItemId == 1)
    }

    @Test
    fun testEmptyState_showsNoItemsMessage() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    MainListScreenHackForPreview(
                        viewState = MainUiModel(
                            isLoading = false,
                            error = null,
                            offersUiModel = persistentListOf()
                        ),
                        onClick = {},
                        onRefresh = {},
                        animatedVisibilityScope = this
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(TestTagEmpty).assertExists()
    }
}
