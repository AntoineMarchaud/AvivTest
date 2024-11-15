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
import com.amarchaud.ui.theme.SimpleListDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

sealed class MyNavigation {
    @Serializable
    data object Home : MyNavigation()

    @Serializable
    data class Detail(
        val id: Int
    ) : MyNavigation()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            SimpleListDemoTheme {

                val navController = rememberNavController()

                // SharedTransitionLayout wraps the content to enable shared element transitions
                SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = MyNavigation.Home,
                    ) {

                        composable<MyNavigation.Home> {
                            MainListComposable(
                                onClick = {
                                    navController.navigate(
                                        MyNavigation.Detail(
                                            id = it
                                        )
                                    )
                                },
                                sharedTransitionScope = this@SharedTransitionLayout, // SharedTransitionLayout provides the SharedTransitionScope
                                animatedVisibilityScope = this@composable // This composable provides the AnimatedVisibilityScope
                            )
                        }

                        composable<MyNavigation.Detail> {
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