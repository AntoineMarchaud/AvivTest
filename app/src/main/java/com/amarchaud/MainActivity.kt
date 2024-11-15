package com.amarchaud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amarchaud.ui.screen.detail.UserDetailComposable
import com.amarchaud.ui.screen.mainList.MainListComposable
import com.amarchaud.ui.theme.AvivTestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Serializable
    object Home

    @Serializable
    data class Detail(
        val id: Int
    )

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            AvivTestTheme {

                val navController = rememberNavController()


                // SharedTransitionLayout wraps the content to enable shared element transitions
                SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Home,
                    ) {




                        composable<Home> {
                            MainListComposable(
                                onClick = {
                                    navController.navigate(Detail(
                                        id = it
                                    ))
                                },
                                sharedTransitionScope = this@SharedTransitionLayout, // SharedTransitionLayout provides the SharedTransitionScope
                                animatedVisibilityScope = this@composable // This composable provides the AnimatedVisibilityScope
                            )
                        }

                        composable<Detail> {
                            UserDetailComposable(
                                onBack = {
                                    navController.popBackStack()
                                },
                                sharedTransitionScope = this@SharedTransitionLayout, // SharedTransitionLayout provides the SharedTransitionScope
                                animatedVisibilityScope = this@composable // This composable provides the AnimatedVisibilityScope
                            )
                        }
                    }
                }

            }
        }
    }
}