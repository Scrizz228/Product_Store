package com.example.product_store.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.product_store.data.Product
import com.example.product_store.ui.screens.CartScreen
import com.example.product_store.ui.screens.ProductCatalogScreen
import com.example.product_store.ui.screens.ProductDetailScreen
import com.example.product_store.ui.screens.FavoritesScreen
import com.example.product_store.ui.screens.SettingsScreen
import com.example.product_store.ui.screens.DealsScreen
import com.example.product_store.viewmodel.ProductStoreViewModel

@Composable
fun ProductStoreNavigation(
    navController: NavHostController,
    viewModel: ProductStoreViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "catalog"
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
                onDealsClick = { navController.navigate("deals") }
            )
        }
        
        composable("product_detail/{productId}") { backStackEntry ->
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
                    }
                )
            }
        }
        
        composable("cart") {
            CartScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("deals") {
            DealsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onProductClick = { product ->
                    navController.navigate("product_detail/${product.id}")
                }
            )
        }
    }
}
