package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.*
import com.example.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class CloversRepository(context: Context) {
    
    private val db: CloversDatabase = Room.databaseBuilder(
        context.applicationContext,
        CloversDatabase::class.java,
        "clovers_db"
    )
    .fallbackToDestructiveMigration()
    .build()

    private val productDao = db.productDao()
    private val cartDao = db.cartDao()
    private val wishlistDao = db.wishlistDao()
    private val orderDao = db.orderDao()
    private val couponDao = db.couponDao()
    private val bannerDao = db.bannerDao()
    private val notificationDao = db.notificationDao()
    private val userDao = db.userDao()

    // Flows
    val productsFlow: Flow<List<Product>> = productDao.getAllProducts()
    val cartFlow: Flow<List<CartItem>> = cartDao.getCartItems()
    val wishlistFlow: Flow<List<WishlistItem>> = wishlistDao.getWishlist()
    val ordersFlow: Flow<List<Order>> = orderDao.getAllOrders()
    val bannersFlow: Flow<List<PromoBanner>> = bannerDao.getBanners()
    val notificationsFlow: Flow<List<NotificationItem>> = notificationDao.getNotifications()
    val currentUserFlow: Flow<User?> = userDao.getCurrentUser()

    fun getProductsByCategory(category: String): Flow<List<Product>> {
        return productDao.getProductsByCategory(category)
    }

    fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query)
    }

    fun isFavoriteFlow(productId: String): Flow<Boolean> {
        return wishlistDao.isFavorite(productId)
    }

    // Initialize Mock Data
    suspend fun initializeMockDataIfNeeded() {
        // Only insert mock data if there are no products
        val currentProducts = productsFlow.first()
        if (currentProducts.isEmpty()) {
            val mockProducts = createMockProducts()
            productDao.insertProducts(mockProducts)

            val mockBanners = createMockBanners()
            bannerDao.insertBanners(mockBanners)

            val mockCoupons = createMockCoupons()
            couponDao.insertCoupons(mockCoupons)

            val defaultUser = User(
                email = "bxer5g@gmail.com",
                name = "Aura Clovers",
                phone = "+66 81 234 5678",
                profilePhotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=200",
                addressesJson = """["101 Sukhumvit Rd, Khlong Toei, Bangkok 10110, Thailand", "456 Nimmanahaeminda Rd, Chiang Mai 50200, Thailand"]""",
                paymentMethodsJson = """["Visa ending in *4242", "PromptPay Wallet"]"""
            )
            userDao.insertUser(defaultUser)

            val initNotifications = listOf(
                NotificationItem(
                    title = "Welcome to Clovers ✨",
                    body = "Explore the pinnacle of eco-luxury and modern tailoring. Enjoy 15% off your first order with code CLOVERS15.",
                    type = "Promotion"
                ),
                NotificationItem(
                    title = "Summer Flash Sale Live 🔥",
                    body = "For the next 24 hours, take up to 20% off selected luxury shoes and bags.",
                    type = "Flash Sale"
                )
            )
            for (notif in initNotifications) {
                notificationDao.insertNotification(notif)
            }
        }
    }

    // Product Admin Ops
    suspend fun addProduct(product: Product) {
        productDao.insertProducts(listOf(product))
    }

    suspend fun deleteProduct(productId: String) {
        // Safe mock deletion via local list update or database query if needed
    }

    suspend fun clearAllData() {
        productDao.deleteAllProducts()
        orderDao.deleteAllOrders()
        cartDao.clearCart()
        userDao.deleteUser()
    }

    // Cart Operations
    suspend fun addToCart(product: Product, size: String, color: String, qty: Int = 1) {
        val existingItem = cartFlow.first().find { 
            it.productId == product.id && it.selectedSize == size && it.selectedColor == color 
        }
        if (existingItem != null) {
            cartDao.updateQuantity(existingItem.id, existingItem.quantity + qty)
        } else {
            val item = CartItem(
                productId = product.id,
                productName = product.name,
                productBrand = product.brand,
                productPrice = product.price,
                productDiscount = product.discount,
                productImage = getFirstImage(product.imagesJson),
                quantity = qty,
                selectedSize = size,
                selectedColor = color
            )
            cartDao.insertCartItem(item)
        }
    }

    suspend fun updateCartQuantity(itemId: Int, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(itemId)
        } else {
            cartDao.updateQuantity(itemId, quantity)
        }
    }

    suspend fun removeCartItem(itemId: Int) {
        cartDao.deleteCartItem(itemId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Wishlist Operations
    suspend fun toggleWishlist(product: Product) {
        val isFav = wishlistFlow.first().any { it.productId == product.id }
        if (isFav) {
            wishlistDao.deleteWishlistItem(product.id)
        } else {
            val item = WishlistItem(
                productId = product.id,
                name = product.name,
                brand = product.brand,
                price = product.price,
                discount = product.discount,
                image = getFirstImage(product.imagesJson)
            )
            wishlistDao.insertWishlistItem(item)
        }
    }

    // Order Operations
    suspend fun placeOrder(
        items: List<CartItem>,
        address: String,
        shippingMethod: String,
        paymentMethod: String,
        couponCode: String?,
        shippingFee: Double,
        tax: Double,
        subtotal: Double,
        total: Double
    ): Order {
        val orderId = "CLV-${System.currentTimeMillis().toString().takeLast(6)}"
        
        // Serialize items to JSON
        val itemsBuilder = StringBuilder("[")
        items.forEachIndexed { index, item ->
            itemsBuilder.append("""{"productId":"${item.productId}","productName":"${item.productName}","productBrand":"${item.productBrand}","productPrice":${item.productPrice},"productDiscount":${item.productDiscount},"productImage":"${item.productImage}","quantity":${item.quantity},"selectedSize":"${item.selectedSize}","selectedColor":"${item.selectedColor}"}""")
            if (index < items.size - 1) itemsBuilder.append(",")
        }
        itemsBuilder.append("]")

        val order = Order(
            id = orderId,
            itemsJson = itemsBuilder.toString(),
            address = address,
            shippingMethod = shippingMethod,
            paymentMethod = paymentMethod,
            couponCode = couponCode,
            shippingFee = shippingFee,
            tax = tax,
            subtotal = subtotal,
            total = total,
            status = "Confirmed",
            trackingProgress = 0.2f
        )
        orderDao.insertOrder(order)
        
        // Clear Cart
        cartDao.clearCart()

        // Send order confirmed notification
        notificationDao.insertNotification(
            NotificationItem(
                title = "Order Confirmed 🛍️",
                body = "Your order $orderId of ${items.size} item(s) has been placed successfully. Thank you for shopping with Clovers!",
                type = "Order"
            )
        )

        return order
    }

    suspend fun updateOrderStatus(orderId: String, status: String, progress: Float) {
        orderDao.updateOrderStatus(orderId, status, progress)
        
        // Send status change notification
        val bodyText = when(status) {
            "Preparing" -> "Our team in Bangkok is carefully preparing your tailored pieces."
            "Shipped" -> "Your Clovers package has been shipped! Tracking reference: TH-${System.currentTimeMillis().toString().takeLast(8)}."
            "Out for Delivery" -> "Your package is with the courier and will arrive today."
            "Delivered" -> "Delivered! We hope you love your premium Clovers outfit."
            else -> "Your order status was updated to: $status."
        }
        
        notificationDao.insertNotification(
            NotificationItem(
                title = "Order $status 🚚",
                body = bodyText,
                type = "Order"
            )
        )
    }

    suspend fun getCoupon(code: String): Coupon? {
        return couponDao.getCouponByCode(code.uppercase())
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    // Helper to extract first image URL from serialized JSON
    private fun getFirstImage(json: String): String {
        return try {
            val cleaned = json.replace("[", "").replace("]", "").replace("\"", "")
            val split = cleaned.split(",")
            split.firstOrNull()?.trim() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    // Mock Creator Methods
    private fun createMockProducts(): List<Product> {
        return listOf(
            Product(
                id = "1",
                name = "Signature Emerald Tailored Trench",
                brand = "Clovers Atelier",
                description = "Masterfully tailored from lightweight double-faced virgin wool. Features an impeccable silhouette, hand-finished seams, tortoiseshell horn buttons, and our custom luxury silk-satin inner lining.",
                price = 380.0,
                discount = 15.0,
                imagesJson = """["https://images.unsplash.com/photo-1591047139829-d91aecb6caea?q=80&w=600"]""",
                sizesJson = """["XS", "S", "M", "L"]""",
                colorsJson = """["#2E8B57", "#222222"]""",
                stock = 14,
                category = "Women",
                rating = 4.9,
                reviewsJson = """[{"userName":"Sophia L.","rating":5,"comment":"The lining is breathtaking! Perfectly structured. Worth every cent.","date":"July 10, 2026"},{"userName":"Manee S.","rating":5,"comment":"Such elegant fabric. Fits true to size and the emerald color is rich.","date":"July 02, 2026"}]"""
            ),
            Product(
                id = "2",
                name = "Oversized French-Terry Hoodie",
                brand = "Clovers Essentials",
                description = "Knitted in dense 450 GSM organic cotton loopback French terry. Minimal brand embroidery on wrist, extra-deep double-layered hood, and zero-drawcord neckline for a clean modernist drape.",
                price = 85.0,
                discount = 0.0,
                imagesJson = """["https://images.unsplash.com/photo-1556821840-3a63f95609a7?q=80&w=600"]""",
                sizesJson = """["S", "M", "L", "XL"]""",
                colorsJson = """["#222222", "#F8F9FA", "#2E8B57"]""",
                stock = 45,
                category = "Men",
                rating = 4.7,
                reviewsJson = """[{"userName":"James K.","rating":4,"comment":"Very heavy, feels incredibly premium. Highly recommend.","date":"June 29, 2026"}]"""
            ),
            Product(
                id = "3",
                name = "Retro Chunky Knit Sneakers",
                brand = "Clovers Footwear",
                description = "Our classic chunky sneaker reconstructed with breathable high-flex flyknit yarn and supple nubuck details. Features a multi-layered ultra-soft foam outsole.",
                price = 140.0,
                discount = 20.0,
                imagesJson = """["https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=600"]""",
                sizesJson = """["38", "39", "40", "41", "42"]""",
                colorsJson = """["#FFFFFF", "#222222"]""",
                stock = 25,
                category = "Shoes",
                rating = 4.8,
                reviewsJson = """[{"userName":"Karan P.","rating":5,"comment":"Incredibly bouncy and look stunning with oversized trousers!","date":"July 11, 2026"}]"""
            ),
            Product(
                id = "4",
                name = "Saffiano Leather Monogram Bag",
                brand = "Clovers Leatherware",
                description = "Constructed from tarnish-resistant Saffiano calfskin. Embossed with Clovers subtle gold insignia, 18k gold-finished closures, and a spacious custom satin compartment.",
                price = 295.0,
                discount = 10.0,
                imagesJson = """["https://images.unsplash.com/photo-1584917865442-de89df76afd3?q=80&w=600"]""",
                sizesJson = """["One Size"]""",
                colorsJson = """["#222222", "#D4AF37", "#2E8B57"]""",
                stock = 9,
                category = "Bags",
                rating = 4.9,
                reviewsJson = """[{"userName":"Emilia R.","rating":5,"comment":"An absolute masterpiece. Fits everything, leather smells divine.","date":"July 05, 2026"}]"""
            ),
            Product(
                id = "5",
                name = "Satin Pleated Evening Slip",
                brand = "Clovers Atelier",
                description = "Captivating bias-cut evening gown drape with intricate back straps. Sewn in fluid high-weight satin that falls beautifully over contours with liquid shine.",
                price = 240.0,
                discount = 0.0,
                imagesJson = """["https://images.unsplash.com/photo-1595777457583-95e059d581b8?q=80&w=600"]""",
                sizesJson = """["S", "M", "L"]""",
                colorsJson = """["#D4AF37", "#222222", "#2E8B57"]""",
                stock = 12,
                category = "Women",
                rating = 4.6,
                reviewsJson = """[{"userName":"Anya T.","rating":4,"comment":"Flows beautifully. Slightly long, but with heels it is magical.","date":"June 18, 2026"}]"""
            ),
            Product(
                id = "6",
                name = "AeroKnit Gym Performance Tee",
                brand = "Clovers Tech",
                description = "Designed for peak focus. Anti-bacterial micro-weave mesh that accelerates moisture dispersion. Fitted with side slits and friction-free flatlock seams.",
                price = 45.0,
                discount = 10.0,
                imagesJson = """["https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=600"]""",
                sizesJson = """["S", "M", "L", "XL"]""",
                colorsJson = """["#2E8B57", "#222222"]""",
                stock = 30,
                category = "Sportswear",
                rating = 4.5,
                reviewsJson = """[{"userName":"Mark B.","rating":5,"comment":"Incredibly lightweight, doesn't smell or trap sweat. Best gym tee.","date":"June 25, 2026"}]"""
            ),
            Product(
                id = "7",
                name = "Gold Monogram Link Bracelet",
                brand = "Clovers Jewelry",
                description = "Artisanal chunky chain links forged in hypo-allergenic brass and bathed in heavy 18k yellow gold. Fitted with our custom Clovers leaf-toggle clasp mechanism.",
                price = 60.0,
                discount = 0.0,
                imagesJson = """["https://images.unsplash.com/photo-1611591437281-460bfbe1220a?q=80&w=600"]""",
                sizesJson = """["One Size"]""",
                colorsJson = """["#D4AF37"]""",
                stock = 18,
                category = "Accessories",
                rating = 4.7,
                reviewsJson = """[{"userName":"Natcha S.","rating":5,"comment":"Pairs with everything. Nice weighty feel without being heavy.","date":"July 09, 2026"}]"""
            ),
            Product(
                id = "8",
                name = "Flax Linen Resort Collar Shirt",
                brand = "Clovers Essentials",
                description = "Woven entirely from long-staple organic Belgian flax linen. Pre-laundered for an exquisite broken-in hand feel. Features a relaxed Cuban collar and real mother-of-pearl buttons.",
                price = 90.0,
                discount = 15.0,
                imagesJson = """["https://images.unsplash.com/photo-1596755094514-f87e34085b2c?q=80&w=600"]""",
                sizesJson = """["S", "M", "L", "XL"]""",
                colorsJson = """["#F8F9FA", "#2E8B57"]""",
                stock = 20,
                category = "Men",
                rating = 4.8,
                reviewsJson = """[{"userName":"Oliver W.","rating":5,"comment":"Perfect drape and doesn't itch like cheaper linen. Brilliant.","date":"July 12, 2026"}]"""
            )
        )
    }

    private fun createMockBanners(): List<PromoBanner> {
        return listOf(
            PromoBanner(
                id = "b1",
                imageUrl = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?q=80&w=1200",
                title = "Emerald Atelier Edition",
                subtitle = "Discover the signature green tailoring that redefines modern minimalism.",
                categoryTrigger = "Women"
            ),
            PromoBanner(
                id = "b2",
                imageUrl = "https://images.unsplash.com/photo-1488161628813-04466f872be2?q=80&w=1200",
                title = "Urban Organic Casuals",
                subtitle = "Luxury comfort rendered in pure organic French-Terry cotton weights.",
                categoryTrigger = "Men"
            )
        )
    }

    private fun createMockCoupons(): List<Coupon> {
        return listOf(
            Coupon("CLOVERS15", 15.0, "15% off first order on our premiere catalog.", 50.0),
            Coupon("EMERALD20", 20.0, "Exclusive 20% off with minimum $150 spend.", 150.0),
            Coupon("FREESHIP", 100.0, "Free global shipping coupon code.", 0.0)
        )
    }
}
