package com.example.product_store.data

import androidx.compose.runtime.Immutable

@Immutable
data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.discountedPrice * quantity
}

@Immutable
data class Cart(
    val items: List<CartItem> = emptyList()
) {
    val totalItems: Int
        get() = items.sumOf { it.quantity }
    
    val totalPrice: Double
        get() = items.sumOf { it.totalPrice }
    
    val isEmpty: Boolean
        get() = items.isEmpty()
    
    fun addItem(product: Product, quantity: Int = 1): Cart {
        val existingItem = items.find { it.product.id == product.id }
        return if (existingItem != null) {
            val updatedItems = items.map { item ->
                if (item.product.id == product.id) {
                    item.copy(quantity = item.quantity + quantity)
                } else {
                    item
                }
            }
            Cart(updatedItems)
        } else {
            Cart(items + CartItem(product, quantity))
        }
    }
    
    fun removeItem(productId: Int): Cart {
        return Cart(items.filter { it.product.id != productId })
    }
    
    fun updateQuantity(productId: Int, quantity: Int): Cart {
        if (quantity <= 0) {
            return removeItem(productId)
        }
        return Cart(
            items.map { item ->
                if (item.product.id == productId) {
                    item.copy(quantity = quantity)
                } else {
                    item
                }
            }
        )
    }
}
