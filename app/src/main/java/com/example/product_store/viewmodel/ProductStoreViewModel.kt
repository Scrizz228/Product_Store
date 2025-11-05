package com.example.product_store.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.product_store.data.Cart
import com.example.product_store.data.Product
import com.example.product_store.data.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class ProductStoreViewModel : ViewModel() {
    
    private val _products = MutableStateFlow(getSampleProducts())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<ProductCategory?>(null)
    val selectedCategory: StateFlow<ProductCategory?> = _selectedCategory.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _useLocalImages = MutableStateFlow(false)
    val useLocalImages: StateFlow<Boolean> = _useLocalImages.asStateFlow()
    
    private val _filteredProducts = MutableStateFlow(getFilteredProducts())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()
    
    // Recent search history (in-memory)
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    // Favorites
    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    enum class SortOption { NAME, PRICE, DISCOUNT }
    private val _sortOption = MutableStateFlow(SortOption.NAME)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    // Theme
    enum class ThemeMode { SYSTEM, LIGHT, DARK }
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
    
    init {
        viewModelScope.launch {
            val debouncedQuery = searchQuery.debounce(200)
            val base = combine(products, selectedCategory, debouncedQuery) { prods, cat, q ->
                Triple(prods, cat, q)
            }
            combine(base, sortOption) { baseTriple, sort ->
                val (prods, category, query) = baseTriple
                val q = query.trim().lowercase()
                prods.filter { product ->
                    val matchesCategory = category == null || product.category == category
                    val name = product.name.lowercase()
                    val desc = product.description.lowercase()
                    val matchesQuery = q.isEmpty() || name.contains(q) || desc.contains(q)
                    matchesCategory && matchesQuery
                }.sortedWith(compareBy<Product> { p ->
                    val name = p.name.lowercase()
                    when {
                        q.isEmpty() -> 0
                        name.startsWith(q) -> 0
                        name.contains(q) -> 1
                        else -> 2
                    }
                }.thenBy { p ->
                    val name = p.name.lowercase()
                    if (q.isEmpty()) Int.MAX_VALUE else name.indexOf(q).let { if (it < 0) Int.MAX_VALUE else it }
                }.thenBy { if (sort == SortOption.NAME) it.name.lowercase() else "" }
                 .thenBy { if (sort == SortOption.PRICE) it.discountedPrice else 0.0 }
                 .thenBy { if (sort == SortOption.DISCOUNT) -it.discount else 0 })
            }.collect { filtered ->
                _filteredProducts.value = filtered
            }
        }
    }
    
    fun addToCart(product: Product, quantity: Int = 1) {
        _cart.value = _cart.value.addItem(product, quantity)
    }
    
    fun removeFromCart(productId: Int) {
        _cart.value = _cart.value.removeItem(productId)
    }
    
    fun updateCartQuantity(productId: Int, quantity: Int) {
        _cart.value = _cart.value.updateQuantity(productId, quantity)
    }
    
    fun clearCart() {
        _cart.value = Cart()
    }
    
    fun setSelectedCategory(category: ProductCategory?) {
        _selectedCategory.value = category
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setUseLocalImages(useLocal: Boolean) {
        _useLocalImages.value = useLocal
    }
    
    private fun getFilteredProducts(): List<Product> {
        val currentProducts = products.value
        val currentCategory = selectedCategory.value
        val currentQuery = searchQuery.value.trim().lowercase()
        
        return currentProducts
            .filter { product ->
                val matchesCategory = currentCategory == null || product.category == currentCategory
                val name = product.name.lowercase()
                val desc = product.description.lowercase()
                val matchesQuery = currentQuery.isEmpty() || name.contains(currentQuery) || desc.contains(currentQuery)
                matchesCategory && matchesQuery
            }
            .sortedWith(compareBy<Product> { p ->
                val name = p.name.lowercase()
                when {
                    currentQuery.isEmpty() -> 0
                    name.startsWith(currentQuery) -> 0
                    name.contains(currentQuery) -> 1
                    else -> 2
                }
            }.thenBy { p ->
                val name = p.name.lowercase()
                if (currentQuery.isEmpty()) Int.MAX_VALUE else name.indexOf(currentQuery).let { if (it < 0) Int.MAX_VALUE else it }
            }.thenBy { it.name.lowercase() })
    }
    
    fun submitSearch() {
        val q = _searchQuery.value.trim()
        if (q.isEmpty()) return
        val updated = _recentSearches.value.toMutableList()
        updated.removeAll { it.equals(q, ignoreCase = true) }
        updated.add(0, q)
        _recentSearches.value = updated.take(10)
    }
    
    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun toggleFavorite(productId: Int) {
        val updated = _favoriteIds.value.toMutableSet()
        if (!updated.add(productId)) updated.remove(productId)
        _favoriteIds.value = updated
    }

    fun clearFavorites() {
        _favoriteIds.value = emptySet()
    }

    fun setSort(option: SortOption) {
        _sortOption.value = option
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
    
    private fun getSampleProducts(): List<Product> {
        return listOf(
            Product(
                id = 1,
                name = "Яблоки Голден",
                description = "Свежие сладкие яблоки сорта Голден",
                price = 120.0,
                imageRes = com.example.product_store.R.drawable.apple,
                category = ProductCategory.FRUITS,
                discount = 10
            ),
            Product(
                id = 2,
                name = "Морковь",
                description = "Свежая морковь, богатая витаминами",
                price = 80.0,
                imageRes = com.example.product_store.R.drawable.carrot,
                category = ProductCategory.VEGETABLES
            ),
            Product(
                id = 3,
                name = "Молоко 3.2%",
                description = "Свежее коровье молоко 1 литр",
                price = 65.0,
                imageRes = com.example.product_store.R.drawable.milk,
                category = ProductCategory.DAIRY
            ),
            Product(
                id = 4,
                name = "Куриная грудка",
                description = "Свежая куриная грудка без костей",
                price = 350.0,
                imageRes = com.example.product_store.R.drawable.chicken_breast,
                category = ProductCategory.MEAT,
                discount = 15
            ),
            Product(
                id = 5,
                name = "Хлеб Бородинский",
                description = "Традиционный ржаной хлеб",
                price = 45.0,
                imageRes = com.example.product_store.R.drawable.bread,
                category = ProductCategory.BAKERY
            ),
            Product(
                id = 6,
                name = "Апельсины",
                description = "Сочные апельсины из Марокко",
                price = 150.0,
                imageRes = com.example.product_store.R.drawable.orange,
                category = ProductCategory.FRUITS
            ),
            Product(
                id = 7,
                name = "Йогурт натуральный",
                description = "Натуральный йогурт без добавок",
                price = 85.0,
                imageRes = com.example.product_store.R.drawable.yogurt,
                category = ProductCategory.DAIRY
            ),
            Product(
                id = 8,
                name = "Картофель",
                description = "Свежий картофель для варки и жарки",
                price = 60.0,
                imageRes = com.example.product_store.R.drawable.potato,
                category = ProductCategory.VEGETABLES
            ),
            Product(
                id = 9,
                name = "Сок апельсиновый",
                description = "Натуральный апельсиновый сок 1л",
                price = 120.0,
                imageRes = com.example.product_store.R.drawable.orange_juice,
                category = ProductCategory.BEVERAGES
            ),
            Product(
                id = 10,
                name = "Печенье овсяное",
                description = "Домашнее овсяное печенье",
                price = 95.0,
                imageRes = com.example.product_store.R.drawable.oatmeal_cookie,
                category = ProductCategory.SNACKS,
                discount = 20
            ),
            Product(
                id = 11,
                name = "Бананы",
                description = "Спелые бананы из Эквадора",
                price = 90.0,
                imageRes = com.example.product_store.R.drawable.banana,
                category = ProductCategory.FRUITS
            ),
            Product(
                id = 12,
                name = "Помидоры",
                description = "Свежие помидоры черри",
                price = 110.0,
                imageRes = com.example.product_store.R.drawable.tomato,
                category = ProductCategory.VEGETABLES,
                discount = 5
            ),
            Product(
                id = 13,
                name = "Сыр Гауда",
                description = "Голландский сыр Гауда 200г",
                price = 280.0,
                imageRes = com.example.product_store.R.drawable.cheese,
                category = ProductCategory.DAIRY
            ),
            Product(
                id = 14,
                name = "Рыба",
                description = "Свежая рыба для жарки",
                price = 450.0,
                imageRes = com.example.product_store.R.drawable.fish,
                category = ProductCategory.MEAT,
                discount = 12
            ),
            Product(
                id = 15,
                name = "Круассан",
                description = "Свежий французский круассан",
                price = 55.0,
                imageRes = com.example.product_store.R.drawable.croissant,
                category = ProductCategory.BAKERY
            )
        )
    }
}
