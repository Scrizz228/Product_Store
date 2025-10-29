package com.example.product_store.data

import androidx.compose.runtime.Immutable

@Immutable
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: ProductCategory,
    val isInStock: Boolean = true,
    val discount: Int = 0 // процент скидки
) {
    val discountedPrice: Double
        get() = if (discount > 0) price * (1 - discount / 100.0) else price
    
    val hasDiscount: Boolean
        get() = discount > 0
}

enum class ProductCategory(val displayName: String) {
    FRUITS("Фрукты"),
    VEGETABLES("Овощи"),
    DAIRY("Молочные продукты"),
    MEAT("Мясо и птица"),
    BAKERY("Выпечка"),
    BEVERAGES("Напитки"),
    SNACKS("Закуски"),
    FROZEN("Замороженные продукты")
}
