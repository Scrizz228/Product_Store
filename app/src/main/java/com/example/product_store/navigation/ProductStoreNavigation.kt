package com.example.product_store.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import com.example.product_store.ui.screens.CartScreen
import com.example.product_store.ui.screens.ProductCatalogScreen
import com.example.product_store.ui.screens.ProductDetailScreen
import com.example.product_store.ui.screens.FavoritesScreen
import com.example.product_store.ui.screens.SettingsScreen
import com.example.product_store.ui.screens.DealsScreen
import com.example.product_store.viewmodel.ProductStoreViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProductStoreNavigation(
    navController: NavHostController,
    viewModel: ProductStoreViewModel
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = "catalog",
        enterTransition = {
            // Material FadeThrough-like with smoother spring animations
            fadeIn(animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f)) + 
                    scaleIn(initialScale = 0.95f, animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f))
        },
        exitTransition = {
            fadeOut(animationSpec = spring(dampingRatio = 0.9f, stiffness = 400f))
        },
        popEnterTransition = {
            fadeIn(animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f))
        },
        popExitTransition = {
            fadeOut(animationSpec = spring(dampingRatio = 0.9f, stiffness = 400f)) + 
                    scaleOut(targetScale = 0.95f, animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f))
        }
    ) {
        composable("catalog") {
            ProductCatalogScreen(
                viewModel = viewModel,
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                onFavoritesClick = { navController.navigate("favorites") },
                onSettingsClick = { navController.navigate("settings") },
                onDealsClick = { navController.navigate("deals") },
                onNavigateHome = { /* уже дома, ничего не делаем */ }
            )
        }
        
        composable(
            route = "product_detail/{productId}",
            enterTransition = {
                // Slide from right + fade for forward navigation with smoother spring
                slideInHorizontally(
                    initialOffsetX = { it / 3 }, 
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 250f)
                ) + fadeIn(animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f))
            },
            exitTransition = {
                fadeOut(animationSpec = spring(dampingRatio = 0.9f, stiffness = 400f))
            },
            popEnterTransition = {
                fadeIn(animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f))
            },
            popExitTransition = {
                // Slide to right + fade on back with smoother spring
                slideOutHorizontally(
                    targetOffsetX = { it / 3 }, 
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 250f)
                ) + fadeOut(animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f))
            }
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            val product = viewModel.products.value.find { it.id == productId }
            
            if (product != null) {
                ProductDetailScreen(
                    product = product,
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCartClick = {
                        navController.navigate("cart")
                    },
                    onNavigateHome = { navController.popBackStack("catalog", inclusive = false) }
                )
            }
        }
        
        composable("cart",
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it }, 
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 250f)
                ) + fadeIn(animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it }, 
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 250f)
                ) + fadeOut(animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f))
            }
        ) {
            CartScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                },
                onNavigateHome = { navController.popBackStack("catalog", inclusive = false) }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                },
                onNavigateHome = { navController.popBackStack("catalog", inclusive = false) }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateHome = { navController.popBackStack("catalog", inclusive = false) }
            )
        }

        composable("deals") {
            DealsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                },
                onNavigateHome = { navController.popBackStack("catalog", inclusive = false) }
            )
        }
    }
}
