package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.api.GeminiService
import com.example.data.repository.CloversRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class StylistMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class CloversViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CloversRepository(application)

    // Flash Sale Timer State
    private val _flashSaleTime = MutableStateFlow("02:14:45")
    val flashSaleTime: StateFlow<String> = _flashSaleTime.asStateFlow()

    // Skeleton Loading State
    private val _isCatalogLoading = MutableStateFlow(false)
    val isCatalogLoading: StateFlow<Boolean> = _isCatalogLoading.asStateFlow()

    // Recently Viewed State
    private val _recentlyViewedIds = MutableStateFlow<List<String>>(emptyList())
    val recentlyViewed: StateFlow<List<Product>> = combine(_recentlyViewedIds, repository.productsFlow) { ids, allProducts ->
        ids.mapNotNull { id -> allProducts.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Initialization
    init {
        viewModelScope.launch {
            repository.initializeMockDataIfNeeded()
        }
        
        // Start Countdown Timer Loop
        viewModelScope.launch {
            var seconds = 2 * 3600 + 14 * 60 + 45
            while (seconds > 0) {
                delay(1000)
                seconds--
                val h = seconds / 3600
                val m = (seconds % 3600) / 60
                val s = seconds % 60
                _flashSaleTime.value = String.format("%02d:%02d:%02d", h, m, s)
            }
        }
    }

    // Auth States
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authScreenState = MutableStateFlow("Login") // Login, Register, Forgot
    val authScreenState: StateFlow<String> = _authScreenState.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    val currentUser: StateFlow<User?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Catalog States
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Products Flow
    val products: StateFlow<List<Product>> = combine(
        repository.productsFlow,
        _selectedCategory,
        _searchQuery
    ) { allProducts, category, query ->
        var list = allProducts
        if (category != "All") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (query.isNotEmpty()) {
            list = list.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.brand.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val banners: StateFlow<List<PromoBanner>> = repository.bannersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart and Wishlist
    val cartItems: StateFlow<List<CartItem>> = repository.cartFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlist: StateFlow<List<WishlistItem>> = repository.wishlistFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Coupons & Calculations
    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _couponError = MutableStateFlow<String?>(null)
    val couponError: StateFlow<String?> = _couponError.asStateFlow()

    val subtotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.finalPrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val shippingFee: StateFlow<Double> = combine(subtotal, _appliedCoupon) { sub, coupon ->
        if (sub == 0.0) return@combine 0.0
        if (coupon?.code == "FREESHIP" || sub > 200.0) 0.0 else 15.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 15.0)

    val tax: StateFlow<Double> = subtotal.map { sub ->
        sub * 0.07 // 7% tax standard
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val couponDiscount: StateFlow<Double> = combine(subtotal, _appliedCoupon) { sub, coupon ->
        if (coupon == null) return@combine 0.0
        if (coupon.code == "FREESHIP") return@combine 0.0
        sub * (coupon.discountPercentage / 100.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val total: StateFlow<Double> = combine(subtotal, shippingFee, tax, couponDiscount) { sub, ship, tx, disc ->
        (sub + ship + tx - disc).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Checkout Details
    private val _checkoutAddress = MutableStateFlow("101 Sukhumvit Rd, Khlong Toei, Bangkok 10110, Thailand")
    val checkoutAddress: StateFlow<String> = _checkoutAddress.asStateFlow()

    private val _checkoutShippingMethod = MutableStateFlow("Clovers Express (2-3 Days)")
    val checkoutShippingMethod: StateFlow<String> = _checkoutShippingMethod.asStateFlow()

    private val _checkoutPaymentMethod = MutableStateFlow("Credit Card")
    val checkoutPaymentMethod: StateFlow<String> = _checkoutPaymentMethod.asStateFlow()

    // Orders Tracking
    val orders: StateFlow<List<Order>> = repository.ordersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    // AI Stylist States
    private val _stylistMessages = MutableStateFlow<List<StylistMessage>>(listOf(
        StylistMessage(
            text = "Welcome to Clovers AI Styling Suite! I am Aura, your personal luxury fashion concierge. Ask me for outfit suggestions, sizes, or return questions.",
            isUser = false
        )
    ))
    val stylistMessages: StateFlow<List<StylistMessage>> = _stylistMessages.asStateFlow()

    private val _isStylistLoading = MutableStateFlow(false)
    val isStylistLoading: StateFlow<Boolean> = _isStylistLoading.asStateFlow()

    // AI Search States
    private val _aiSearchActive = MutableStateFlow(false)
    val aiSearchActive: StateFlow<Boolean> = _aiSearchActive.asStateFlow()

    private val _aiSearchResult = MutableStateFlow<List<Product>?>(null)
    val aiSearchResult: StateFlow<List<Product>?> = _aiSearchResult.asStateFlow()

    private val _isAiSearching = MutableStateFlow(false)
    val isAiSearching: StateFlow<Boolean> = _isAiSearching.asStateFlow()

    // Admin Panel States
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Notifications
    val notifications: StateFlow<List<NotificationItem>> = repository.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wishlist check
    fun isFavorite(productId: String): Flow<Boolean> = repository.isFavoriteFlow(productId)

    // Action Methods

    // Auth
    fun setAuthScreenState(state: String) {
        _authScreenState.value = state
        _authError.value = null
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            delay(1000) // Aesthetic delay for professional feel
            if (email.contains("@") && password.length >= 6) {
                _isLoggedIn.value = true
                _authLoading.value = false
            } else {
                _authError.value = "Invalid email or password (min 6 characters)."
                _authLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            delay(1000)
            if (name.isNotEmpty() && email.contains("@") && phone.isNotEmpty()) {
                val newUser = User(
                    email = email,
                    name = name,
                    phone = phone,
                    profilePhotoUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=200",
                    addressesJson = """["$checkoutAddress"]""",
                    paymentMethodsJson = """["Visa ending in *9999"]"""
                )
                repository.insertUser(newUser)
                _isLoggedIn.value = true
                _authLoading.value = false
            } else {
                _authError.value = "All fields are required. Please enter valid email."
                _authLoading.value = false
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _authScreenState.value = "Login"
    }

    // Catalog Navigation
    fun selectCategory(category: String) {
        _selectedCategory.value = category
        viewModelScope.launch {
            _isCatalogLoading.value = true
            delay(500) // Simulated loading delay for smooth premium skeleton effect
            _isCatalogLoading.value = false
        }
    }

    fun addToRecentlyViewed(productId: String) {
        val current = _recentlyViewedIds.value.toMutableList()
        current.remove(productId)
        current.add(0, productId)
        _recentlyViewedIds.value = current.take(6) // limit to top 6 recently viewed
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _aiSearchActive.value = false
            _aiSearchResult.value = null
        }
    }

    // AI Semantic Search
    fun executeAiSearch(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) return@launch
            _isAiSearching.value = true
            _aiSearchActive.value = true
            
            // Build system prompt that guides Gemini to match our database products
            val prompt = """
                The user wants to find clothing based on this search query: "$query".
                Here are the active product categories: Men, Women, Kids, Shoes, Bags, Accessories, Sportswear.
                Analyze the user query, and return a simple comma-separated list of category names or keywords that fit.
                Respond with nothing but the keywords. Example: "Men, Shoes, Hoodie"
            """.trimIndent()

            val geminiResponse = GeminiService.generateResponse(prompt)
            delay(1200) // Visual pacing

            val keywords = geminiResponse.split(",").map { it.trim().lowercase() }
            
            // Filter all database products locally based on AI matching
            val allProducts = repository.productsFlow.first()
            val matches = allProducts.filter { prod ->
                keywords.any { key ->
                    prod.name.lowercase().contains(key) ||
                    prod.brand.lowercase().contains(key) ||
                    prod.category.lowercase().contains(key) ||
                    prod.description.lowercase().contains(key) ||
                    (key.contains("under") && prod.finalPrice < 30.0) ||
                    (key.contains("under") && query.contains("30") && prod.finalPrice < 30.0) ||
                    (key.contains("under") && query.contains("100") && prod.finalPrice < 100.0) ||
                    (key.contains("under") && query.contains("300") && prod.finalPrice < 300.0)
                }
            }

            _aiSearchResult.value = matches.ifEmpty { 
                // Default search query filter as reliable fallback
                allProducts.filter { 
                    it.name.contains(query, ignoreCase = true) || 
                    it.description.contains(query, ignoreCase = true) 
                }
            }
            _isAiSearching.value = false
        }
    }

    fun clearAiSearch() {
        _aiSearchActive.value = false
        _aiSearchResult.value = null
        _searchQuery.value = ""
    }

    // Cart ops
    fun addToCart(product: Product, size: String, color: String) {
        viewModelScope.launch {
            repository.addToCart(product, size, color, 1)
        }
    }

    fun updateCartQuantity(itemId: Int, qty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(itemId, qty)
        }
    }

    fun removeFromCart(itemId: Int) {
        viewModelScope.launch {
            repository.removeCartItem(itemId)
        }
    }

    // Wishlist ops
    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            repository.toggleWishlist(product)
        }
    }

    // Coupon Apply
    fun applyCoupon(code: String) {
        viewModelScope.launch {
            _couponError.value = null
            val coupon = repository.getCoupon(code)
            if (coupon != null) {
                if (subtotal.value >= coupon.minOrderAmount) {
                    _appliedCoupon.value = coupon
                } else {
                    _couponError.value = "Min spend for this code is \$${coupon.minOrderAmount}"
                }
            } else {
                _couponError.value = "Invalid discount coupon code."
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponError.value = null
    }

    // Checkout Selection
    fun selectAddress(addr: String) {
        _checkoutAddress.value = addr
    }

    fun selectShippingMethod(method: String) {
        _checkoutShippingMethod.value = method
    }

    fun selectPaymentMethod(method: String) {
        _checkoutPaymentMethod.value = method
    }

    // Place Order
    fun placeOrder(onOrderPlaced: (String) -> Unit) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            val couponCode = appliedCoupon.value?.code
            val order = repository.placeOrder(
                items = items,
                address = checkoutAddress.value,
                shippingMethod = checkoutShippingMethod.value,
                paymentMethod = checkoutPaymentMethod.value,
                couponCode = couponCode,
                shippingFee = shippingFee.value,
                tax = tax.value,
                subtotal = subtotal.value,
                total = total.value
            )

            _selectedOrder.value = order
            _appliedCoupon.value = null
            onOrderPlaced(order.id)

            // Automate status transition tracking for rich visual demo!
            simulateShippingLifeCycle(order.id)
        }
    }

    // Select active order to view details
    fun selectOrderForTracking(order: Order) {
        _selectedOrder.value = order
    }

    // Simulate standard shipping status changes over time (for visual demo in app)
    private fun simulateShippingLifeCycle(orderId: String) {
        viewModelScope.launch {
            delay(15000) // 15 seconds to "Preparing"
            repository.updateOrderStatus(orderId, "Preparing", 0.4f)
            refreshActiveOrderTracking(orderId)

            delay(20000) // Next 20 seconds to "Shipped"
            repository.updateOrderStatus(orderId, "Shipped", 0.6f)
            refreshActiveOrderTracking(orderId)

            delay(20000) // Next 20 seconds to "Out for Delivery"
            repository.updateOrderStatus(orderId, "Out for Delivery", 0.8f)
            refreshActiveOrderTracking(orderId)

            delay(20000) // Next 20 seconds to "Delivered"
            repository.updateOrderStatus(orderId, "Delivered", 1.0f)
            refreshActiveOrderTracking(orderId)
        }
    }

    private suspend fun refreshActiveOrderTracking(orderId: String) {
        val currentSelected = _selectedOrder.value
        if (currentSelected != null && currentSelected.id == orderId) {
            val fresh = repository.ordersFlow.first().find { it.id == orderId }
            if (fresh != null) {
                _selectedOrder.value = fresh
            }
        }
    }

    // Chat AI Stylist
    fun sendStylistMessage(text: String) {
        if (text.isBlank()) return
        
        val userMsg = StylistMessage(text = text, isUser = true)
        _stylistMessages.value = _stylistMessages.value + userMsg
        _isStylistLoading.value = true

        viewModelScope.launch {
            // Build standard system prompt that outlines the brand guidelines
            val sysPrompt = """
                You are Aura, the elite personal stylist for "Clovers", a luxury eco-conscious fashion brand.
                Be polite, high-end, inspiring, and elegant.
                Give rich, specific fashion styling advice, recommending products by name from the following list of Clovers apparel:
                - Signature Emerald Tailored Trench ($380, Women, luxurious wool-cashmere blend)
                - Oversized French-Terry Hoodie ($85, Men, heavy 450 GSM cotton, modern look)
                - Retro Chunky Knit Sneakers ($140, Shoes, high-flex flyknit, incredibly comfortable)
                - Saffiano Leather Monogram Bag ($295, Bags, Italian Saffiano calfskin, gold-plated hardware)
                - Satin Pleated Evening Slip ($240, Women, heavyweight satin dress)
                - AeroKnit Gym Performance Tee ($45, Sportswear, mesh performance cooling)
                - Gold Monogram Link Bracelet ($60, Accessories, 18k yellow gold-plated)
                - Flax Linen Resort Collar Shirt ($90, Men, pure organic Belgian flax linen)
                
                Always explain WHY the recommended item fits the user's needs, and style it with other accessories. Keep responses relatively concise but stylish.
            """.trimIndent()

            val aiResponse = GeminiService.generateResponse(text, sysPrompt)
            _stylistMessages.value = _stylistMessages.value + StylistMessage(text = aiResponse, isUser = false)
            _isStylistLoading.value = false
        }
    }

    // Admin Panel Actions
    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    fun adminUpdateOrderStatus(orderId: String, status: String, progress: Float) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, progress)
            refreshActiveOrderTracking(orderId)
        }
    }

    fun adminAddProduct(
        name: String,
        brand: String,
        description: String,
        price: Double,
        discount: Double,
        image: String,
        category: String,
        sizes: List<String>,
        colors: List<String>
    ) {
        viewModelScope.launch {
            val id = (repository.productsFlow.first().size + 1).toString()
            
            // Serializers
            val imgJson = """["$image"]"""
            val sizeBuilder = StringBuilder("[")
            sizes.forEachIndexed { i, s ->
                sizeBuilder.append("\"$s\"")
                if (i < sizes.size - 1) sizeBuilder.append(",")
            }
            sizeBuilder.append("]")

            val colorBuilder = StringBuilder("[")
            colors.forEachIndexed { i, c ->
                colorBuilder.append("\"$c\"")
                if (i < colors.size - 1) colorBuilder.append(",")
            }
            colorBuilder.append("]")

            val product = Product(
                id = id,
                name = name,
                brand = brand,
                description = description,
                price = price,
                discount = discount,
                imagesJson = imgJson,
                sizesJson = sizeBuilder.toString(),
                colorsJson = colorBuilder.toString(),
                stock = 30,
                category = category,
                rating = 5.0,
                reviewsJson = "[]"
            )

            repository.addProduct(product)

            repository.notificationsFlow.first() // Trigger notifications refresh
        }
    }
}
