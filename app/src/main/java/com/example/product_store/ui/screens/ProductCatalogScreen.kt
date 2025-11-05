package com.example.product_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.BadgedBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.onFocusChanged
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.product_store.R
import com.example.product_store.data.Product
import com.example.product_store.data.ProductCategory
import com.example.product_store.viewmodel.ProductStoreViewModel
import com.example.product_store.viewmodel.ProductStoreViewModel.SortOption
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCatalogScreen(
    viewModel: ProductStoreViewModel,
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDealsClick: () -> Unit,
    onNavigateHome: () -> Unit // добавил параметр для возврата домой
) {
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val favorites by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    
    var showSearch by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(false) }
    var isSearchFocused by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Content
        Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
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
            actions = {
                IconButton(onClick = {
                    if (!showSearch) {
                        showSearch = true
                        searchActive = true
                    } else {
                        showSearch = false
                        searchActive = false
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Sort moved into overflow menu for cleaner toolbar
                // Overflow menu: Deals, Favorites, Settings
                var overflowOpen by remember { mutableStateOf(false) }
                IconButton(onClick = { overflowOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Ещё",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                val dealsCount = viewModel.products.value.count { it.hasDiscount }
                StyledOverflowMenu(
                    expanded = overflowOpen,
                    onDismiss = { overflowOpen = false },
                    sortOption = sortOption,
                    onSortSelect = { opt -> overflowOpen = false; viewModel.setSort(opt) },
                    dealsCount = dealsCount,
                    onDealsClick = { overflowOpen = false; onDealsClick() },
                    onFavoritesClick = { overflowOpen = false; onFavoritesClick() },
                    onSettingsClick = { overflowOpen = false; onSettingsClick() }
                )
                val badgeScale = remember { Animatable(1f) }
                LaunchedEffect(cart.totalItems) {
                    if (cart.totalItems > 0) {
                        badgeScale.snapTo(1f)
                        badgeScale.animateTo(1.15f, tween(durationMillis = 120))
                        badgeScale.animateTo(1f, tween(durationMillis = 120))
                    }
                }
                ToolbarIconButton(onClick = onCartClick, badgeCount = cart.totalItems, contentDescription = "Корзина") {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .scale(badgeScale.value)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        
        // Search Bar with animation and polished UI
        AnimatedVisibility(
            visible = showSearch,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val borderColor by animateColorAsState(
                if (isSearchFocused) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), label = "searchBorder"
            )
            val elev by animateDpAsState(if (isSearchFocused) 8.dp else 2.dp, label = "searchElev")

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                tonalElevation = elev,
                shadowElevation = elev,
                border = BorderStroke(1.dp, borderColor),
                color = MaterialTheme.colorScheme.surface
            ) {
                @OptIn(ExperimentalMaterial3Api::class)
                DockedSearchBar(
                    expanded = false,
                    onExpandedChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    colors = SearchBarDefaults.colors(
                        containerColor = Color.Transparent,
                        dividerColor = Color.Transparent
                    ),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    inputField = {
                        TextField(
                            value = searchQuery,
                            onValueChange = viewModel::setSearchQuery,
                            placeholder = { Text("Поиск продуктов", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(Icons.Default.Close, contentDescription = "Очистить", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .heightIn(min = 52.dp)
                                .onFocusChanged {
                                    isSearchFocused = it.isFocused
                                    if (it.isFocused) {
                                        searchActive = true
                                    }
                                },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search,
                                hintLocales = LocaleList(Locale("ru-RU"))
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            )
                        )
                    }
                ) { }
            }
        }
        
        // Category Filter
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    onClick = { 
                        viewModel.setSelectedCategory(null)
                        if (showSearch && !isSearchFocused) {
                            showSearch = false
                        }
                    },
                    label = { Text("Все") },
                    selected = selectedCategory == null
                )
            }
            items(ProductCategory.values().toList()) { category ->
                FilterChip(
                    onClick = { 
                        viewModel.setSelectedCategory(category)
                        if (showSearch && !isSearchFocused) {
                            showSearch = false
                        }
                    },
                    label = { Text(category.displayName) },
                    selected = selectedCategory == category
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Result count
        if (showSearch) {
            Text(
                text = "Найдено: ${products.size}",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Recent searches chips under search bar
        if (showSearch && recentSearches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentSearches) { query ->
                    AssistChip(
                        onClick = { viewModel.setSearchQuery(query) },
                        label = { Text(query) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Empty state
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "🔍",
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "Ничего не найдено",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Попробуйте изменить параметры поиска",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Column
        }
        
        // Products Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = products,
                key = { _, product -> product.id },
                contentType = { _, _ -> "product" }
            ) { index, product ->
                val alpha = remember { Animatable(0f) }
                val scale = remember { Animatable(0.95f) }
                LaunchedEffect(product.id) {
                    // небольшая задержка для ступенчатого появления
                    val delay = (index % 6) * 50
                    kotlinx.coroutines.delay(delay.toLong())
                    alpha.animateTo(1f, spring(dampingRatio = 0.8f, stiffness = 300f))
                    scale.animateTo(1f, spring(dampingRatio = 0.8f, stiffness = 300f))
                }
                Box(
                    modifier = Modifier
                        .graphicsLayer(alpha = alpha.value, scaleX = scale.value, scaleY = scale.value)
                ) {
                    ProductCard(
                        product = product,
                        onProductClick = onProductClick,
                        onAddToCart = { viewModel.addToCart(product) },
                        isFavorite = favorites.contains(product.id),
                        onToggleFavorite = { viewModel.toggleFavorite(product.id) }
                    )
                }
            }
        }
        }
        
        // No overlay; tap-to-dismiss is handled by explicit actions only
    }

    // Auto-collapse search on downward scroll when empty and unfocused
    LaunchedEffect(gridState, showSearch, searchQuery, searchActive, isSearchFocused) {
        var lastIndex = gridState.firstVisibleItemIndex
        var lastOffset = gridState.firstVisibleItemScrollOffset
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val scrollingDown = index > lastIndex || (index == lastIndex && offset > lastOffset)
                if (scrollingDown && showSearch && searchQuery.isBlank() && !searchActive && !isSearchFocused) {
                    showSearch = false
                }
                lastIndex = index
                lastOffset = offset
            }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: (Product) -> Unit,
    onAddToCart: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product Image (теперь painterResource)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val context = LocalContext.current
                val req = remember(product.imageRes) {
                    coil.request.ImageRequest.Builder(context)
                        .data(product.imageRes)
                        .allowHardware(false)
                        .size(400)
                        .crossfade(true)
                        .build()
                }
                coil.compose.AsyncImage(
                    model = req,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Shared element key for smooth transition
                            // This allows coordination with detail screen
                        },
                    contentScale = ContentScale.Crop
                )
                
                // Favorite toggle
                val favScale = remember { Animatable(1f) }
                val favScope = rememberCoroutineScope()
                IconButton(
                    onClick = {
                        onToggleFavorite()
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        // micro pop
                        favScope.launch {
                            favScale.snapTo(1f)
                            favScale.animateTo(1.15f, spring(dampingRatio = 0.5f, stiffness = 600f))
                            favScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 500f))
                        }
                    },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    val iconId = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = "Избранное",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.scale(favScale.value)
                    )
                }

                // Discount Badge
                if (product.hasDiscount) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                MaterialTheme.colorScheme.error,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "-${product.discount}%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Product Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Price
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (product.hasDiscount) {
                    Text(
                        text = "${product.price.toInt()} ₽",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "${product.discountedPrice.toInt()} ₽",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Add to Cart Button
            val scope = rememberCoroutineScope()
            val addScale = remember { Animatable(1f) }
            Button(
                onClick = {
                    onAddToCart()
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    scope.launch {
                        addScale.snapTo(1f)
                        addScale.animateTo(0.95f, spring(dampingRatio = 0.5f, stiffness = 600f))
                        addScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 500f))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(addScale.value),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("В корзину")
            }
        }
    }
}

@Composable
private fun ToolbarIconButton(
    onClick: () -> Unit,
    badgeCount: Int? = null,
    contentDescription: String?,
    content: @Composable () -> Unit
) {
    IconButton(onClick = onClick, colors = IconButtonDefaults.iconButtonColors()) {
        if ((badgeCount ?: 0) > 0) {
            BadgedBox(badge = {
                Badge(containerColor = MaterialTheme.colorScheme.error) {
                    Text((badgeCount ?: 0).toString())
                }
            }) {
                content()
            }
        } else {
            content()
        }
    }
}

@Composable
private fun ToolbarActionsCluster(
    dealsBadge: Int,
    onDealsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToolbarIconButton(onClick = onDealsClick, badgeCount = dealsBadge, contentDescription = "Акции") {
                Icon(
                    painter = painterResource(id = R.drawable.ic_local_offer),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            VerticalDivider(modifier = Modifier.height(24.dp))
            ToolbarIconButton(onClick = onFavoritesClick, contentDescription = "Избранное") {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite_filled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            VerticalDivider(modifier = Modifier.height(24.dp))
            ToolbarIconButton(onClick = onSettingsClick, contentDescription = "Настройки") {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun MenuSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun StyledOverflowMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    sortOption: SortOption,
    onSortSelect: (SortOption) -> Unit,
    dealsCount: Int,
    onDealsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        MenuSectionHeader("Сортировка")
        DropdownMenuItem(
            text = { Text("По имени", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = { if (sortOption == SortOption.NAME) Icon(Icons.Default.Check, contentDescription = null) },
            onClick = { onSortSelect(SortOption.NAME) },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        )
        DropdownMenuItem(
            text = { Text("По цене", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = { if (sortOption == SortOption.PRICE) Icon(Icons.Default.Check, contentDescription = null) },
            onClick = { onSortSelect(SortOption.PRICE) },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        )
        DropdownMenuItem(
            text = { Text("По скидке", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = { if (sortOption == SortOption.DISCOUNT) Icon(Icons.Default.Check, contentDescription = null) },
            onClick = { onSortSelect(SortOption.DISCOUNT) },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        )
        HorizontalDivider()
        MenuSectionHeader("Навигация")
        DropdownMenuItem(
            text = { Text(if (dealsCount > 0) "Акции ($dealsCount)" else "Акции", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_local_offer),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = onDealsClick,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        )
        DropdownMenuItem(
            text = { Text("Избранное", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_favorite_filled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = onFavoritesClick,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        )
        DropdownMenuItem(
            text = { Text("Настройки", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = onSettingsClick,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}