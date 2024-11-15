@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.amarchaud.ui.screen.mainList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.amarchaud.ui.R
import com.amarchaud.ui.screen.mainList.composables.ErrorComposable
import com.amarchaud.ui.screen.mainList.composables.OneElement
import com.amarchaud.ui.screen.mainList.composables.OneElementShimmer
import com.amarchaud.ui.screen.mainList.models.ErrorApiUiModel
import com.amarchaud.ui.screen.mainList.models.MainUiModel
import com.amarchaud.ui.screen.mainList.models.OfferUiModel
import com.amarchaud.ui.screen.mainList.models.toMessage
import com.amarchaud.ui.theme.SimpleListDemoTheme
import kotlinx.collections.immutable.persistentListOf
import kotlin.random.Random

internal val heightOneCell = 96.dp
private const val MAX_ELEMENTS_TO_ANIMATE = 16
private const val TRANSLATION_DURATION = 800
private const val INIT_OFFSET_VALUE = 200
private const val OFFSET_BY_INDEX = 60
private val animationCubic = CubicBezierEasing(0f, 0.56f, 0.46f, 1f)

const val nbShimmer = 12
const val TestTagShimmer = "testShimmer_"
const val TestTagError = "testError"
const val TestTagOffer = "testTagOffer_"
const val TestTagEmpty = "testTagEmpty"

@Composable
fun MainListComposable(
    viewModel: MainListViewModel = hiltViewModel(),
    onClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val viewState by viewModel.viewState.collectAsState()

    MainListScreen(
        viewState = viewState,
        onClick = onClick,
        onRefresh = {
            viewModel.onRefresh()
        },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainListScreen(
    viewState: MainUiModel,
    onClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val context = LocalContext.current

    var canDisplayEnterAnimation by rememberSaveable { mutableStateOf(false) }
    var canDisplayPullToRefresh by rememberSaveable { mutableStateOf(false) }

    var translationY: List<State<Dp>> = remember { mutableStateListOf() }


    if (translationY.isEmpty() && LocalInspectionMode.current.not()) {
        translationY = buildList {
            for (i in 0 until MAX_ELEMENTS_TO_ANIMATE) {
                add(
                    i,
                    animateDpAsState(
                        targetValue = if (canDisplayEnterAnimation) {
                            0.dp
                        } else {
                            (INIT_OFFSET_VALUE + i * OFFSET_BY_INDEX).dp // start from bottom
                        },
                        animationSpec = tween(
                            durationMillis = TRANSLATION_DURATION,
                            easing = animationCubic,
                        ),
                        label = "animate item $i",
                    ),
                )
            }
        }
    }

    val state = rememberPullRefreshState(
        refreshing = canDisplayPullToRefresh && viewState.isLoading,
        onRefresh = {
            canDisplayEnterAnimation = false
            onRefresh()
        }
    )

    LaunchedEffect(key1 = viewState.isLoading) {
        if (!viewState.isLoading) {
            canDisplayPullToRefresh = true
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state = state),
    ) {
        AnimatedVisibility(
            visible = viewState.isLoading,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            canDisplayEnterAnimation = false

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2)
            ) {
                items(count = nbShimmer) { index ->
                    OneElementShimmer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .testTag(tag = "${TestTagShimmer}${index}"),
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = viewState.error != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(alignment = Alignment.Center),
        ) {
            ErrorComposable(
                modifier = Modifier.testTag(tag = TestTagError),
                errorMessage = stringResource(
                    id = R.string.error_refresh,
                    viewState.error?.toMessage(context).orEmpty()
                ),
                onRefresh = onRefresh
            )
        }

        AnimatedVisibility(
            visible = !viewState.isLoading && viewState.error == null && viewState.offersUiModel.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(alignment = Alignment.Center),
        ) {
            Text(
                modifier = Modifier
                    .testTag(TestTagEmpty)
                    .align(Alignment.Center),
                text = "Nothing !"
            )
        }

        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.matchParentSize(),
            visible = !viewState.isLoading && viewState.error == null
        ) {
            canDisplayEnterAnimation = true

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
            ) {
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.statusBarsPadding())
                }

                items(
                    count = viewState.offersUiModel.size,
                    key = { index -> viewState.offersUiModel[index].id }
                ) { index ->
                    viewState.offersUiModel[index].let {
                        OneElement(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .testTag(
                                    tag = "${TestTagOffer}$index"
                                )
                                .offset(
                                    y = if (index < MAX_ELEMENTS_TO_ANIMATE && translationY.isNotEmpty()) {
                                        translationY[index].value
                                    } else {
                                        0.dp
                                    },
                                ),
                            element = it,
                            onClick = {
                                onClick(it.id)
                            },
                            animatedVisibilityScope = animatedVisibilityScope,
                            sharedTransitionScope = sharedTransitionScope
                        )
                    }
                }
            }

        }


        PullRefreshIndicator(
            refreshing = canDisplayPullToRefresh && viewState.isLoading,
            state = state,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
internal fun SharedTransitionScope.MainListScreenHackForPreview(
    viewState: MainUiModel,
    onClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    MainListScreen(
        viewState = viewState,
        onClick = onClick,
        onRefresh = onRefresh,
        sharedTransitionScope = this,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@Preview
@Composable
private fun MainListScreenPreview() {
    SimpleListDemoTheme {
        val viewState = MainUiModel(
            isLoading = false,
            error = null,
            offersUiModel = persistentListOf(*List(10) {
                mockOfferElement.copy(
                    id = Random.nextInt()
                )
            }.toTypedArray())
        )

        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                MainListScreenHackForPreview(
                    viewState = viewState,
                    onClick = {},
                    onRefresh = {},
                    animatedVisibilityScope = this
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainListScreenLoadingPreview() {
    SimpleListDemoTheme {

        val viewState = MainUiModel(
            isLoading = true,
            error = null,
            offersUiModel = persistentListOf(*List(10) {
                mockOfferElement.copy(
                    id = Random.nextInt()
                )
            }.toTypedArray())
        )

        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                MainListScreenHackForPreview(
                    viewState = viewState,
                    onClick = {},
                    onRefresh = {},
                    animatedVisibilityScope = this
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainListScreenErrorPreview() {
    SimpleListDemoTheme {
        val viewState = MainUiModel(
            isLoading = false,
            error = ErrorApiUiModel.NoInternetError,
            offersUiModel = persistentListOf(*List(10) {
                mockOfferElement.copy(
                    id = Random.nextInt()
                )
            }.toTypedArray())
        )

        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                MainListScreenHackForPreview(
                    viewState = viewState,
                    onClick = {},
                    onRefresh = {},
                    animatedVisibilityScope = this
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainListScreenEmptyPreview() {
    SimpleListDemoTheme {
        val viewState = MainUiModel(
            isLoading = false,
            error = null,
            offersUiModel = persistentListOf()
        )

        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                MainListScreenHackForPreview(
                    viewState = viewState,
                    onClick = {},
                    onRefresh = {},
                    animatedVisibilityScope = this
                )
            }
        }
    }
}

internal val mockOfferElement = OfferUiModel(
    id = 0,
    city = "Paris",
    area = 250.0,
    url = "https://v.seloger.com/s/crop/590x330/visuels/1/7/t/3/17t3fitclms3bzwv8qshbyzh9dw32e9l0p0udr80k.jpg".toUri(),
    price = 185000.0,
    professional = "GSL EXPLORE",
    propertyType = "Maison - Villa",
    offerType = 1,
    rooms = 7,
    bedrooms = 4
)
