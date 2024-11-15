@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.amarchaud.ui.screen.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.amarchaud.ui.R
import com.amarchaud.ui.composables.ImageLoaderSubCompose
import com.amarchaud.ui.composables.ShimmerAnimationItem
import com.amarchaud.ui.screen.detail.models.DetailUiModel
import com.amarchaud.ui.screen.mainList.composables.ErrorComposable
import com.amarchaud.ui.screen.mainList.composables.SharedCity
import com.amarchaud.ui.screen.mainList.composables.SharedImage
import com.amarchaud.ui.screen.mainList.mockOfferElement
import com.amarchaud.ui.screen.mainList.models.OfferUiModel
import com.amarchaud.ui.screen.mainList.models.toMessage
import com.amarchaud.ui.theme.SimpleListDemoTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun UserDetailComposable(
    viewModel: DetailViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()

    BackHandler {
        onBack()
    }

    UserDetailScreen(
        viewState = viewState,
        onBack = onBack,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onRefresh = viewModel::onRefresh
    )
}

@Composable
private fun UserDetailScreen(
    viewState: DetailUiModel,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(visible = viewState.isLoading) {
            ShimmerAnimationItem(modifier = Modifier.fillMaxSize())
        }

        AnimatedVisibility(
            visible = viewState.error != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ErrorComposable(
                modifier = Modifier.align(alignment = Alignment.Center),
                errorMessage = stringResource(
                    id = R.string.error_refresh,
                    viewState.error?.toMessage(context).orEmpty()
                ),
                onRefresh = onRefresh
            )
        }

        val offsetDetail by animateDpAsState(
            targetValue = if (viewState.isLoading.not() && viewState.error == null) {
                0.dp
            } else {
                200.dp
            },
            animationSpec  = tween(500),
        )

        Column(modifier = Modifier.fillMaxSize()) {
            if (viewState.isLoading.not() && viewState.error == null) {
                OfferMainDetail(
                    modifier = Modifier.fillMaxWidth(),
                    offerDetail = viewState.offerUiModel,
                    animatedVisibilityScope = animatedVisibilityScope,
                    sharedTransitionScope = sharedTransitionScope
                )

                OfferBottomDetail(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetDetail)
                        .padding(all = 16.dp),
                    offerDetail = viewState.offerUiModel,
                )
            }
        }


        OutlinedButton(
            onClick = onBack,
            shape = CircleShape,
            border = BorderStroke(
                2.dp,
                Color.White,
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 16.dp)
                .size(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
            contentPadding = PaddingValues(8.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "back",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun OfferMainDetail(
    modifier: Modifier = Modifier,
    offerDetail: OfferUiModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageLoaderSubCompose(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
                    .sharedElement(
                        state = rememberSharedContentState(key = "$SharedImage-${offerDetail.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
                contentScale = ContentScale.Crop,
                data = offerDetail.url,
                loading = {
                    ShimmerAnimationItem()
                },
                failure = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_error_24dp),
                        contentDescription = "error loading"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = "$SharedCity-${offerDetail.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
                text = offerDetail.city
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OfferBottomDetail(
    modifier: Modifier = Modifier,
    offerDetail: OfferUiModel,
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(size = 16.dp))
            .background(color = Color.DarkGray)
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Row {
            Text(text = "professional")
            Text(text = offerDetail.professional)
        }
        Row {
            Text(text = "bedrooms")
            Text(text = offerDetail.bedrooms.toString())
        }
        Row {
            Text(text = "city")
            Text(text = offerDetail.city)
        }
        Row {
            Text(text = "propertyType")
            Text(text = offerDetail.propertyType)
        }
        Row {
            Text(text = "offerType")
            Text(text = offerDetail.offerType.toString())
        }
        Row {
            Text(text = "rooms")
            Text(text = offerDetail.rooms.toString())
        }
        Row {
            Text(text = "price")
            Text(text = offerDetail.price.toString())
        }
    }
}

@Composable
private fun SharedTransitionScope.UserDetailScreenHackForPreview(
    viewState: DetailUiModel,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    UserDetailScreen(
        viewState = viewState,
        onBack = onBack,
        sharedTransitionScope = this,
        animatedVisibilityScope = animatedVisibilityScope,
        onRefresh = {}
    )
}

@Preview
@Composable
private fun UserDetailScreenPreview() {
    SimpleListDemoTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                UserDetailScreenHackForPreview(
                    viewState = DetailUiModel(
                        offerUiModel = mockOfferElement,
                    ),
                    onBack = {},
                    animatedVisibilityScope = this
                )
            }
        }
    }
}