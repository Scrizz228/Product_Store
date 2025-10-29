package com.example.product_store.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import com.example.product_store.viewmodel.ProductStoreViewModel
import com.example.product_store.viewmodel.ProductStoreViewModel.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ProductStoreViewModel,
    onBackClick: () -> Unit
) {
    val useLocalImages by viewModel.useLocalImages.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Тема", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(
                    selected = themeMode == ThemeMode.SYSTEM,
                    onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                    label = { Text("Системная") }
                )
                FilterChip(
                    selected = themeMode == ThemeMode.LIGHT,
                    onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                    label = { Text("Светлая") }
                )
                FilterChip(
                    selected = themeMode == ThemeMode.DARK,
                    onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                    label = { Text("Тёмная") }
                )
            }

            HorizontalDivider()

            // Toggle: use local images (for offline/testing)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Локальные изображения")
                Switch(checked = useLocalImages, onCheckedChange = { viewModel.setUseLocalImages(it) })
            }

            HorizontalDivider()

            // Actions
            Button(onClick = { viewModel.clearRecentSearches() }, modifier = Modifier.fillMaxWidth()) {
                Text("Очистить последние поиски")
            }
            Button(onClick = { viewModel.clearFavorites() }, modifier = Modifier.fillMaxWidth()) {
                Text("Очистить избранное")
            }
            Button(onClick = { viewModel.clearCart() }, modifier = Modifier.fillMaxWidth()) {
                Text("Очистить корзину")
            }
        }
    }
}


