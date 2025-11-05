package com.example.product_store.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Share
import android.content.Intent
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.product_store.R
import com.example.product_store.data.Product
import com.example.product_store.viewmodel.ProductStoreViewModel
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import kotlin.math.max


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: ProductStoreViewModel,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val cartItem = cart.items.find { it.product.id == product.id }
    var quantity by remember { mutableStateOf(cartItem?.quantity ?: 1) }
    val haptics = LocalHapticFeedback.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            },
            actions = {
                // Share button
                val context = LocalContext.current
                IconButton(onClick = {
                    val shareText = "${product.name} - ${product.discountedPrice.toInt()} ₽\n${product.description}"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Поделиться продуктом"))
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Поделиться"
                    )
                }
                val badgeScale = remember { Animatable(1f) }
                LaunchedEffect(cart.totalItems) {
                    if (cart.totalItems > 0) {
                        badgeScale.snapTo(1f)
                        badgeScale.animateTo(1.15f, spring(dampingRatio = 0.6f, stiffness = 400f))
                        badgeScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 500f))
                    }
                }
                Box {
                    IconButton(onClick = onCartClick) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Корзина"
                        )
                    }
                    if (cart.totalItems > 0) {
                        Badge(
                            modifier = Modifier
                                .offset(x = (-8).dp, y = 8.dp)
                                .scale(badgeScale.value)
                        ) {
                            Text(cart.totalItems.toString())
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        
        // Content with parallax scroll
        val scrollState = rememberScrollState()
        val scrollOffset = remember { derivedStateOf { scrollState.value } }
        val parallaxOffset = remember { derivedStateOf { max(0f, -scrollOffset.value * 0.5f) } }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Product Image with parallax effect (теперь painterResource)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .graphicsLayer {
                        translationY = parallaxOffset.value
                        alpha = max(0f, 1f - scrollOffset.value / 600f)
                    }
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val context = LocalContext.current
                val req = remember(product.imageRes) {
                    coil.request.ImageRequest.Builder(context)
                        .data(product.imageRes)
                        .allowHardware(false)
                        .size(800)
                        .crossfade(true)
                        .build()
                }
                coil.compose.AsyncImage(
                    model = req,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Smooth scale-in on appear (shared element effect)
                            // Coordinated with navigation transition
                        },
                    contentScale = ContentScale.Crop
                )
                
                // Discount Badge
                if (product.hasDiscount) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                MaterialTheme.colorScheme.error,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .offset(x = (-8).dp, y = 8.dp)
                    ) {
                        Text(
                            text = "-${product.discount}%",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Product Info with fade-in animation
            val contentAlpha = remember { Animatable(0f) }
            LaunchedEffect(product.id) {
                // Delay content appearance for smooth shared element transition
                kotlinx.coroutines.delay(150)
                contentAlpha.animateTo(1f, spring(dampingRatio = 0.85f, stiffness = 300f))
            }
            
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .graphicsLayer { alpha = contentAlpha.value }
            ) {
                // Product Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Category
                Text(
                    text = product.category.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Price
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.hasDiscount) {
                        Text(
                            text = "${product.price.toInt()} ₽",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "${product.discountedPrice.toInt()} ₽",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                Text(
                    text = "Описание",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Quantity Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Количество:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val minusScale = remember { Animatable(1f) }
                        val minusScope = rememberCoroutineScope()
                        IconButton(
                            onClick = { 
                                if (quantity > 1) {
                                    quantity--
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    minusScope.launch {
                                        minusScale.snapTo(1f)
                                        minusScale.animateTo(0.9f, spring(dampingRatio = 0.6f, stiffness = 600f))
                                        minusScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 500f))
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.scale(minusScale.value)
                            )
                        }
                        
                        Text(
                            text = quantity.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        val plusScale = remember { Animatable(1f) }
                        val plusScope = rememberCoroutineScope()
                        IconButton(
                            onClick = { 
                                quantity++
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                plusScope.launch {
                                    plusScale.snapTo(1f)
                                    plusScale.animateTo(0.9f, spring(dampingRatio = 0.6f, stiffness = 600f))
                                    plusScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 500f))
                                }
                            }
                        ) {
                            Text(
                                text = "+",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.scale(plusScale.value)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Add to Cart Button
                val scope = rememberCoroutineScope()
                val addScale = remember { Animatable(1f) }
                Button(
                    onClick = {
                        viewModel.addToCart(product, quantity)
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        val cartItem = viewModel.cart.value.items.find { it.product.id == product.id }
                        scope.launch {
                            addScale.snapTo(1f)
                            addScale.animateTo(0.95f, spring(dampingRatio = 0.5f, stiffness = 600f))
                            addScale.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 500f))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(addScale.value),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (cartItem != null) "Обновить корзину" else "Добавить в корзину",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
