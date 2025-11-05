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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ProductStoreViewModel,
    onBackClick: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()

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
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Тема приложения", style = MaterialTheme.typography.titleMedium)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Выберите внешний вид",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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

                    // Mini previews
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ThemePreviewCard(
                            title = "Светлая",
                            isSelected = themeMode == ThemeMode.LIGHT,
                            background = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
                            surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
                            onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
                            onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
                            onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
                        )
                        ThemePreviewCard(
                            title = "Тёмная",
                            isSelected = themeMode == ThemeMode.DARK,
                            background = androidx.compose.ui.graphics.Color(0xFF121212),
                            surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                            onBackground = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
                            onSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
                            onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemePreviewCard(
    title: String,
    isSelected: Boolean,
    background: androidx.compose.ui.graphics.Color,
    surface: androidx.compose.ui.graphics.Color,
    onBackground: androidx.compose.ui.graphics.Color,
    onSurface: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Background preview
                Surface(
                    modifier = Modifier.size(width = 72.dp, height = 44.dp),
                    color = background,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Box(Modifier.fillMaxSize().padding(6.dp)) {
                        Text("bg", color = onBackground, style = MaterialTheme.typography.labelSmall)
                    }
                }
                // Surface preview
                Surface(
                    modifier = Modifier.size(width = 72.dp, height = 44.dp),
                    color = surface,
                    tonalElevation = 1.dp,
                    shadowElevation = 1.dp
                ) {
                    Box(Modifier.fillMaxSize().padding(6.dp)) {
                        Text("surface", color = onSurface, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}


