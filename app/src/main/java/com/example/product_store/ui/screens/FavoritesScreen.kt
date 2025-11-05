package com.example.product_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.product_store.data.Product
import com.example.product_store.viewmodel.ProductStoreViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: ProductStoreViewModel,
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onNavigateHome: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val favorites by viewModel.favoriteIds.collectAsState()
    val favProducts = products.filter { favorites.contains(it.id) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "7Even",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onNavigateHome)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (favProducts.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearFavorites() }) {
                            Text("Очистить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (favProducts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Нет избранных товаров")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onProductClick = onProductClick,
                        onAddToCart = { viewModel.addToCart(product) },
                        isFavorite = true,
                        onToggleFavorite = { viewModel.toggleFavorite(product.id) }
                    )
                }
            }
        }
    }
}




