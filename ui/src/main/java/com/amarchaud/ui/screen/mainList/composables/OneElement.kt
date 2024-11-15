@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.amarchaud.ui.screen.mainList.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amarchaud.ui.R
import com.amarchaud.ui.composables.ImageLoaderSubCompose
import com.amarchaud.ui.composables.ShimmerAnimationItem
import com.amarchaud.ui.screen.mainList.heightOneCell
import com.amarchaud.ui.screen.mainList.mockOfferElement
import com.amarchaud.ui.screen.mainList.models.OfferUiModel
import com.amarchaud.ui.theme.SimpleListDemoTheme

const val SharedImage = "SharedImage"
const val SharedCity = "SharedCity"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun OneElement(
    modifier: Modifier = Modifier,
    element: OfferUiModel,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    with(sharedTransitionScope) {
        Column(
            modifier = modifier
                .clickable {
                    onClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ImageLoaderSubCompose(
                modifier = Modifier
                    .height(heightOneCell)
                    .aspectRatio(ratio = 1f)
                    .sharedElement(
                        state = rememberSharedContentState(key = "${SharedImage}-${element.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .clip(shape = CircleShape)
                    .border(width = 2.dp, color = Color.Blue, shape = CircleShape),
                data = element.url,
                loading = {
                    ShimmerAnimationItem()
                },
                failure = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_error_24dp),
                        contentDescription = "error loading image"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = "${SharedCity}-${element.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ), text = element.city
            )

            Divider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(start = 32.dp)
            )
        }
    }
}

@Composable
private fun SharedTransitionScope.OneElementHack(
    modifier: Modifier = Modifier,
    element: OfferUiModel,
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    OneElement(
        modifier = modifier,
        element = element,
        onClick =  onClick,
        sharedTransitionScope = this,
        animatedVisibilityScope = animatedVisibilityScope
    )
}

@Preview
@Composable
private fun OneUserPreview() {
    SimpleListDemoTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                OneElementHack(
                    modifier = Modifier,
                    element = mockOfferElement,
                    onClick = {},
                    animatedVisibilityScope = this
                )
            }
        }
    }
}