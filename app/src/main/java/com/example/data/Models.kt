package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String,
    val description: String,
    val price: Double,
    val discount: Double, // percentage, e.g., 15.0 for 15% off
    val imagesJson: String, // Stored as serialized JSON list of string image URLs
    val sizesJson: String,  // Stored as serialized JSON list of strings, e.g. ["S", "M", "L"]
    val colorsJson: String, // Stored as serialized JSON list of colors, e.g. ["#222222", "#F8F9FA"]
    val stock: Int,
    val category: String, // Men, Women, Kids, Shoes, Bags, Accessories, Sportswear
    val rating: Double,
    val reviewsJson: String, // Stored as serialized JSON list of Review objects
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val finalPrice: Double
        get() = price * (1 - (discount / 100.0))
}

data class Review(
    val id: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val date: String
)

@Entity(tableName = "cart")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val productName: String,
    val productBrand: String,
    val productPrice: Double,
    val productDiscount: Double,
    val productImage: String,
    val quantity: Int,
    val selectedSize: String,
    val selectedColor: String
) {
    val finalPrice: Double
        get() = productPrice * (1 - (productDiscount / 100.0))
}

@Entity(tableName = "wishlist")
data class WishlistItem(
    @PrimaryKey val productId: String,
    val name: String,
    val brand: String,
    val price: Double,
    val discount: Double,
    val image: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String,
    val itemsJson: String, // Stored as serialized JSON list of CartItem
    val address: String,
    val shippingMethod: String,
    val paymentMethod: String,
    val couponCode: String?,
    val shippingFee: Double,
    val tax: Double,
    val subtotal: Double,
    val total: Double,
    val status: String, // Pending, Confirmed, Preparing, Shipped, Out for Delivery, Delivered
    val trackingProgress: Float, // 0.0 to 1.0 representing shipping visual progress
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val phone: String,
    val profilePhotoUrl: String,
    val addressesJson: String, // Serialized List<String>
    val paymentMethodsJson: String, // Serialized List<String>
    val isGoogleUser: Boolean = false,
    val isVerified: Boolean = true
)

@Entity(tableName = "coupons")
data class Coupon(
    @PrimaryKey val code: String,
    val discountPercentage: Double,
    val description: String,
    val minOrderAmount: Double
)

@Entity(tableName = "banners")
data class PromoBanner(
    @PrimaryKey val id: String,
    val imageUrl: String,
    val title: String,
    val subtitle: String,
    val categoryTrigger: String? = null
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val type: String, // Order, Promotion, Coupon, Alert
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
