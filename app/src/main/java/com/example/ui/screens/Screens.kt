package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.viewmodel.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.testTag

// --- LOGIN / AUTHENTICATION SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen(
    viewModel: CloversViewModel,
    modifier: Modifier = Modifier
) {
    val authScreenState by viewModel.authScreenState.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val authLoading by viewModel.authLoading.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Luxury Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = "Clovers Logo",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CLOVERS",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "E C O - L U X U R Y  A T E L I E R",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Title
            Text(
                text = when (authScreenState) {
                    "Register" -> "Create Account"
                    "Forgot" -> "Reset Password"
                    else -> "Welcome Back"
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Display
            authError?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Input Fields
            if (authScreenState == "Register") {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("name_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phone_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_input"),
                shape = RoundedCornerShape(12.dp)
            )

            if (authScreenState != "Forgot") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            if (authLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = {
                        when (authScreenState) {
                            "Register" -> viewModel.register(name, email, phone)
                            "Forgot" -> {
                                viewModel.login("bxer5g@gmail.com", "123456") // Demo quick pass
                            }
                            else -> viewModel.login(email, password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = when (authScreenState) {
                            "Register" -> "Sign Up"
                            "Forgot" -> "Recover Account"
                            else -> "Sign In"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign-In Accent Button
            if (authScreenState == "Login") {
                OutlinedButton(
                    onClick = { viewModel.login("bxer5g@gmail.com", "123456") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("google_auth_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Google Sign In", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continue with Google", fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Navigation toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (authScreenState == "Login") {
                    TextButton(onClick = { viewModel.setAuthScreenState("Forgot") }) {
                        Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(onClick = { viewModel.setAuthScreenState("Register") }) {
                        Text("Register", color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    TextButton(onClick = { viewModel.setAuthScreenState("Login") }) {
                        Text("Already have an account? Login", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer"
        )
        Brush.linearGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.2f),
                Color.LightGray.copy(alpha = 0.6f)
            ),
            start = Offset.Zero,
            end = Offset(x = translateAnimation, y = translateAnimation)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun SkeletonProductCard() {
    val brush = shimmerBrush()
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(brush)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Box(modifier = Modifier.width(60.dp).height(12.dp).background(brush, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(brush, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.width(100.dp).height(14.dp).background(brush, RoundedCornerShape(4.dp)))
            }
        }
    }
}

@Composable
fun QuickPreviewDialog(
    product: Product,
    viewModel: CloversViewModel,
    onDismiss: () -> Unit
) {
    val images = remember(product.imagesJson) {
        product.imagesJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
    }
    val sizes = remember(product.sizesJson) {
        product.sizesJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
    }
    val colors = remember(product.colorsJson) {
        product.colorsJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
    }

    var selectedSize by remember { mutableStateOf(sizes.firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(colors.firstOrNull() ?: "#222222") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Quick View",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, null)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = images.firstOrNull() ?: "",
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.brand.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${product.finalPrice.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Colors selection
                Text("Select Color", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { col ->
                        val colorVal = try { Color(android.graphics.Color.parseColor(col)) } catch(e: Exception) { Color.Gray }
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colorVal)
                                .border(
                                    width = if (selectedColor == col) 2.5.dp else 1.dp,
                                    color = if (selectedColor == col) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = col }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sizes selection
                Text("Select Size", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sizes.forEach { size ->
                        val isSel = selectedSize == size
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { selectedSize = size }
                                .border(
                                    1.dp,
                                    if (isSel) Color.Transparent else Color.LightGray,
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                size,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.addToCart(product, selectedSize, selectedColor)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add To Bag", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- HOME SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CloversViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val aiSearchActive by viewModel.aiSearchActive.collectAsState()
    val aiSearchResult by viewModel.aiSearchResult.collectAsState()
    val isAiSearching by viewModel.isAiSearching.collectAsState()

    // Premium newly added VM state collectors
    val isCatalogLoading by viewModel.isCatalogLoading.collectAsState()
    val recentlyViewed by viewModel.recentlyViewed.collectAsState()
    val flashSaleTime by viewModel.flashSaleTime.collectAsState()

    var previewProduct by remember { mutableStateOf<Product?>(null) }
    var bannerIndex by remember { mutableStateOf(0) }

    // Auto Banner Slider Effect
    LaunchedEffect(banners) {
        if (banners.isNotEmpty()) {
            while (true) {
                delay(4000)
                bannerIndex = (bannerIndex + 1) % banners.size
            }
        }
    }

    // AI Pulse animation for search trigger
    val infiniteTransition = rememberInfiniteTransition(label = "ai_btn_pulse")
    val aiPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ai_btn_pulse"
    )

    val categories = listOf("All", "Men", "Women", "Kids", "Shoes", "Bags", "Accessories", "Sportswear")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Premium Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CLOVERS",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "AURA ATELIER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }

                // Quick Toggle for User Mode / Admin Mode
                IconButton(
                    onClick = { viewModel.toggleAdminMode() },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                        .testTag("admin_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Admin View",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Glassmorphism Search Bar with AI Trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search Clovers or ask AI...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearAiSearch() }) {
                                Icon(Icons.Default.Close, "Clear")
                            }
                        } else {
                            IconButton(
                                onClick = { 
                                    viewModel.updateSearchQuery("Emerald green satin formal attire")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Stylist Auto",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .testTag("search_bar_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
                    )
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Pulsing AI Search Action Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .scale(if (isAiSearching) 1f else aiPulseScale)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                        .clickable { viewModel.executeAiSearch(searchQuery) }
                        .testTag("ai_search_submit"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isAiSearching) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, "Run AI Search", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
            }

            // Horizontal Premium Category Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSel = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .clickable { viewModel.selectCategory(cat) }
                            .padding(horizontal = 18.dp, vertical = 11.dp)
                            .testTag("category_tab_${cat.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Main List Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                if (aiSearchActive) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI Stylist Curated Matches",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            TextButton(onClick = { viewModel.clearAiSearch() }) {
                                Text("Reset Filter", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    val aiList = aiSearchResult ?: emptyList()
                    if (aiList.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 60.dp, horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(56.dp), tint = Color.Gray)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("No curated matches found for \"$searchQuery\"", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    } else {
                        items(aiList.chunked(2)) { pair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                pair.forEach { product ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ProductCard(
                                            product = product,
                                            viewModel = viewModel,
                                            onQuickPreview = { previewProduct = product },
                                            onClick = {
                                                viewModel.addToRecentlyViewed(product.id)
                                                navController.navigate("product/${product.id}")
                                            }
                                        )
                                    }
                                }
                                if (pair.size == 1) {
                                    Box(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                } else {
                    // SKELETON SHIMMER LOADER
                    if (isCatalogLoading) {
                        items(3) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) { SkeletonProductCard() }
                                Box(modifier = Modifier.weight(1f)) { SkeletonProductCard() }
                            }
                        }
                    } else {
                        // Large Promotional Slider Banner
                        if (banners.isNotEmpty()) {
                            item {
                                val banner = banners[bannerIndex % banners.size]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(210.dp)
                                        .padding(horizontal = 24.dp, vertical = 8.dp)
                                        .clickable { viewModel.selectCategory(banner.categoryTrigger ?: "All") }
                                        .testTag("promo_banner"),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        AsyncImage(
                                            model = banner.imageUrl,
                                            contentDescription = banner.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        // Premium Gradient Dark Overlay
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                                    )
                                                )
                                        )
                                        Column(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(20.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("TRENDING COLLECTIONS", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(banner.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                            Text(banner.subtitle, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        }

                                        // Custom slider dot indicators
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            banners.forEachIndexed { idx, _ ->
                                                val isSelected = idx == bannerIndex % banners.size
                                                Box(
                                                    modifier = Modifier
                                                        .size(if (isSelected) 18.dp else 8.dp, 8.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f))
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // LIVE FLASH SALE SECTION (Countdown Timer)
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔥 Clovers Flash Sale",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                // Countdown capsules
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("ENDS IN", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray, letterSpacing = 1.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    val parts = flashSaleTime.split(":")
                                    if (parts.size == 3) {
                                        val hh = parts[0]
                                        val mm = parts[1]
                                        val ss = parts[2]

                                        Box(modifier = Modifier.background(Color(0xFF222222), RoundedCornerShape(8.dp)).padding(horizontal = 7.dp, vertical = 5.dp)) {
                                            Text(hh, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                        }
                                        Text(":", color = Color.Gray, fontWeight = FontWeight.Bold)
                                        Box(modifier = Modifier.background(Color(0xFF222222), RoundedCornerShape(8.dp)).padding(horizontal = 7.dp, vertical = 5.dp)) {
                                            Text(mm, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                        }
                                        Text(":", color = Color.Gray, fontWeight = FontWeight.Bold)
                                        Box(modifier = Modifier.background(Color(0xFF222222), RoundedCornerShape(8.dp)).padding(horizontal = 7.dp, vertical = 5.dp)) {
                                            Text(ss, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            val saleProducts = products.filter { it.discount > 0.0 }
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(saleProducts) { product ->
                                    Box(modifier = Modifier.width(185.dp)) {
                                        ProductCard(
                                            product = product,
                                            viewModel = viewModel,
                                            onQuickPreview = { previewProduct = product },
                                            onClick = {
                                                viewModel.addToRecentlyViewed(product.id)
                                                navController.navigate("product/${product.id}")
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // RECENTLY VIEWED PRODUCTS
                        if (recentlyViewed.isNotEmpty()) {
                            item {
                                Text(
                                    text = "👁️ Recently Viewed",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                                )
                            }
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 24.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(recentlyViewed) { product ->
                                        Box(modifier = Modifier.width(185.dp)) {
                                            ProductCard(
                                                product = product,
                                                viewModel = viewModel,
                                                onQuickPreview = { previewProduct = product },
                                                onClick = {
                                                    viewModel.addToRecentlyViewed(product.id)
                                                    navController.navigate("product/${product.id}")
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // TRENDING ATELIER ACCESSORIES
                        item {
                            Text(
                                text = "📈 Trending Accessories",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        }
                        item {
                            val trendingList = products.filter { it.category == "Accessories" || it.category == "Bags" }
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(trendingList) { product ->
                                    Box(modifier = Modifier.width(185.dp)) {
                                        ProductCard(
                                            product = product,
                                            viewModel = viewModel,
                                            onQuickPreview = { previewProduct = product },
                                            onClick = {
                                                viewModel.addToRecentlyViewed(product.id)
                                                navController.navigate("product/${product.id}")
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // STANDARD LISTING (Curated New Arrivals)
                        item {
                            Text(
                                text = "✨ Curated New Arrivals",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp)
                            )
                        }

                        items(products.chunked(2)) { pair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                pair.forEach { product ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ProductCard(
                                            product = product,
                                            viewModel = viewModel,
                                            onQuickPreview = { previewProduct = product },
                                            onClick = {
                                                viewModel.addToRecentlyViewed(product.id)
                                                navController.navigate("product/${product.id}")
                                            }
                                        )
                                    }
                                }
                                if (pair.size == 1) {
                                    Box(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // AI Stylist pulsing FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
        ) {
            // Pulsing decorative background ring
            val fabPulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.25f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "fab_pulse"
            )

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), CircleShape)
                    .scale(fabPulseScale)
                    .align(Alignment.Center)
            )

            FloatingActionButton(
                onClick = { navController.navigate("stylist") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.Center)
                    .testTag("stylist_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Ask Stylist",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Quick Preview Dialog Popup Overlay
        previewProduct?.let { p ->
            QuickPreviewDialog(
                product = p,
                viewModel = viewModel,
                onDismiss = { previewProduct = null }
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    viewModel: CloversViewModel,
    onQuickPreview: () -> Unit,
    onClick: () -> Unit
) {
    val images = remember(product.imagesJson) {
        product.imagesJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
    }
    val mainImage = images.firstOrNull() ?: ""
    val isFavorite by viewModel.isFavorite(product.id).collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    // Animating favorite icon scaling on click
    var isPulsing by remember { mutableStateOf(false) }
    val scaleAmount by animateFloatAsState(
        targetValue = if (isPulsing) 1.4f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "favorite_pulse",
        finishedListener = { isPulsing = false }
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
            ) {
                AsyncImage(
                    model = mainImage,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlapping Glassmorphic Badge Row (Discount, Best Seller, New Arrival)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (product.discount > 0) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE74C3C), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "-${product.discount.toInt()}% OFF",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    if (product.rating >= 4.8) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "BEST SELLER",
                                color = Color.Black,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    if (product.id == "1" || product.id == "5" || product.id == "8") {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "NEW ARRIVAL",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Glassmorphic Favorite Button (animated heart)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .clickable {
                            isPulsing = true
                            viewModel.toggleWishlist(product)
                        }
                        .scale(scaleAmount),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Quick View Icon button (glass pill at bottom of product image)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable { onQuickPreview() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Quick Preview",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "QUICK VIEW",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = product.brand.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (product.discount > 0) {
                            Text(
                                text = "$${product.price.toInt()}",
                                fontSize = 11.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = "$${product.finalPrice.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFF1C40F), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = product.rating.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// --- PRODUCT DETAILS SCREEN ---
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: CloversViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }

    if (product == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found.")
        }
        return
    }

    var previewProduct by remember { mutableStateOf<Product?>(null) }

    val images = remember(product.imagesJson) {
        product.imagesJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
    }
    val sizes = remember(product.sizesJson) {
        product.sizesJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
    }
    val colors = remember(product.colorsJson) {
        product.colorsJson.replace("[", "").replace("]", "").replace("\"", "").split(",")
    }

    var selectedSize by remember { mutableStateOf(sizes.firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(colors.firstOrNull() ?: "#222222") }
    val isFavorite by viewModel.isFavorite(product.id).collectAsState(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Clovers Atelier", fontSize = 16.sp, fontWeight = FontWeight.Black)
            IconButton(
                onClick = { viewModel.toggleWishlist(product) },
                modifier = Modifier.testTag("wishlist_toggle")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Large Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = images.firstOrNull() ?: "",
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Brand & Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.brand.uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFF1C40F), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(product.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Name & Price
                Text(
                    text = product.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "$${product.finalPrice.toInt()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (product.discount > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$${product.price.toInt()}",
                            fontSize = 18.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Colors
                Text("Select Color", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { col ->
                        val colorVal = try { Color(android.graphics.Color.parseColor(col)) } catch(e: Exception) { Color.Gray }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colorVal)
                                .border(
                                    width = if (selectedColor == col) 3.dp else 1.dp,
                                    color = if (selectedColor == col) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = col }
                                .testTag("color_dot_$col")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sizes
                Text("Select Size", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    sizes.forEach { size ->
                        val isSel = selectedSize == size
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { selectedSize = size }
                                .border(
                                    1.dp,
                                    if (isSel) Color.Transparent else Color.LightGray,
                                    RoundedCornerShape(10.dp)
                                )
                                .testTag("size_button_$size"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                size,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Reviews Mini Summary
                Text("Verified Customer Reviews", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("100% genuine customer reviews", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "\"Impeccable tailor-work. The stitching on the sleeves is completely hidden. Worth every dollar!\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("— Aria K., Verified Purchase", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                // Recommended Products Section
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "You May Also Appreciate",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                
                val recommendations = products.filter { it.category == product.category && it.id != product.id }
                if (recommendations.isEmpty()) {
                    Text("More curated pieces are on the way.", fontSize = 13.sp, color = Color.Gray)
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(recommendations) { rec ->
                            Box(modifier = Modifier.width(170.dp)) {
                                ProductCard(
                                    product = rec,
                                    viewModel = viewModel,
                                    onQuickPreview = { previewProduct = rec },
                                    onClick = {
                                        viewModel.addToRecentlyViewed(rec.id)
                                        navController.navigate("product/${rec.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action CTA Bar
        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add to Cart
                OutlinedButton(
                    onClick = { viewModel.addToCart(product, selectedSize, selectedColor) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("add_to_cart_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Add To Cart", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                // Buy Now Direct Checkout
                Button(
                    onClick = {
                        viewModel.addToCart(product, selectedSize, selectedColor)
                        navController.navigate("cart")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("buy_now_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Buy Now", fontWeight = FontWeight.Bold)
                }
            }
        }

        previewProduct?.let { p ->
            QuickPreviewDialog(
                product = p,
                viewModel = viewModel,
                onDismiss = { previewProduct = null }
            )
        }
    }
}

// --- SHOPPING CART SCREEN ---
@Composable
fun CartScreen(
    viewModel: CloversViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val shippingFee by viewModel.shippingFee.collectAsState()
    val tax by viewModel.tax.collectAsState()
    val discount by viewModel.couponDiscount.collectAsState()
    val total by viewModel.total.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponError by viewModel.couponError.collectAsState()

    var couponInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Your Shopping Cart", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your cart is completely empty.", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { navController.navigate("home") }) {
                        Text("Start Luxury Shopping", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            return
        }

        // Cart items list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(cartItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                        .testTag("cart_item_${item.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.productImage,
                        contentDescription = item.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.productBrand.uppercase(), fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                        Text(item.productName, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text("Size: ${item.selectedSize}  |  Qty: ${item.quantity}", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${item.finalPrice.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }

                    // Increment / Decrement actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    ) {
                        IconButton(
                            onClick = { viewModel.updateCartQuantity(item.id, item.quantity - 1) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("qty_dec_${item.id}")
                        ) {
                            Icon(Icons.Default.Remove, "Remove one", modifier = Modifier.size(16.dp))
                        }
                        Text(item.quantity.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        IconButton(
                            onClick = { viewModel.updateCartQuantity(item.id, item.quantity + 1) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("qty_inc_${item.id}")
                        ) {
                            Icon(Icons.Default.Add, "Add one", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Coupon Code input box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Apply Promo Coupon", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it },
                                placeholder = { Text("e.g. CLOVERS15, EMERALD20") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("coupon_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.applyCoupon(couponInput) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("apply_coupon_btn")
                            ) {
                                Text("Apply")
                            }
                        }

                        // Display active coupon or error
                        couponError?.let {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Available Atelier Coupons", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val availableCoupons = listOf(
                                Pair("CLOVERS15", "15% OFF Atelier Wide"),
                                Pair("EMERALD20", "20% OFF Luxury Picks"),
                                Pair("VIP30", "30% OFF Elite Club")
                            )
                            items(availableCoupons) { cp ->
                                val isApplied = appliedCoupon?.code == cp.first
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isApplied) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            else MaterialTheme.colorScheme.surface
                                        )
                                        .border(
                                            1.5.dp,
                                            if (isApplied) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            viewModel.applyCoupon(cp.first)
                                            couponInput = cp.first
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ConfirmationNumber,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(cp.first, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                            Text(cp.second, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }

                        appliedCoupon?.let { cp ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Active: ${cp.code} (-${cp.discountPercentage.toInt()}%)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                IconButton(onClick = { viewModel.removeCoupon() }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Close, "Remove Coupon", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Price Breakdown summary
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = Color.Gray)
                        Text("$${subtotal.toInt()}")
                    }
                    if (discount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Coupon Discount", color = MaterialTheme.colorScheme.primary)
                            Text("-$${discount.toInt()}", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shipping Delivery", color = Color.Gray)
                        Text(if (shippingFee == 0.0) "Free" else "$${shippingFee.toInt()}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tax VAT (7%)", color = Color.Gray)
                        Text("$${tax.toInt()}")
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("$${total.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Checkout Button CTA
        Surface(
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("checkout") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(52.dp)
                    .testTag("checkout_cta_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// --- CHECKOUT SCREEN ---
@Composable
fun CheckoutScreen(
    viewModel: CloversViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val subtotal by viewModel.subtotal.collectAsState()
    val shippingFee by viewModel.shippingFee.collectAsState()
    val tax by viewModel.tax.collectAsState()
    val discount by viewModel.couponDiscount.collectAsState()
    val total by viewModel.total.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    val checkoutAddress by viewModel.checkoutAddress.collectAsState()
    val checkoutShippingMethod by viewModel.checkoutShippingMethod.collectAsState()
    val checkoutPaymentMethod by viewModel.checkoutPaymentMethod.collectAsState()

    val scope = rememberCoroutineScope()

    val addresses = listOf(
        "101 Sukhumvit Rd, Khlong Toei, Bangkok 10110, Thailand",
        "456 Nimmanahaeminda Rd, Chiang Mai 50200, Thailand"
    )

    val payments = listOf(
        "Credit Card",
        "PromptPay QR",
        "Cash on Delivery"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Checkout Atelier", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Address Section
            Column {
                Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                addresses.forEach { addr ->
                    val isSel = checkoutAddress == addr
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectAddress(addr) }
                            .testTag("address_card_${if(addr.contains("Sukhumvit")) "sukhumvit" else "chiangmai"}"),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent
                        ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isSel, onClick = { viewModel.selectAddress(addr) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(addr, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Payment Selection
            Column {
                Text("Payment Option", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                payments.forEach { pay ->
                    val isSel = checkoutPaymentMethod == pay
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectPaymentMethod(pay) }
                            .testTag("payment_card_${pay.replace(" ", "").lowercase()}"),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent
                        ),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isSel, onClick = { viewModel.selectPaymentMethod(pay) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = when(pay) {
                                    "Credit Card" -> Icons.Default.CreditCard
                                    "PromptPay QR" -> Icons.Default.QrCode
                                    else -> Icons.Default.Payments
                                },
                                contentDescription = pay,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(pay, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // QR PromptPay Interactive Card Generator!
            if (checkoutPaymentMethod == "PromptPay QR") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF00385D), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("PROMPTPAY QR GENERATOR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Code Vector Simulator
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .background(Color.White)
                                .border(1.dp, Color.LightGray)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "PromptPay QR Simulator",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color(0xFF00385D)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Scan code to pay securely via any bank app.", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            text = "Amount: $${total.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Color(0xFF00385D)
                        )
                    }
                }
            }

            // Summary Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Bag Total", color = Color.Gray)
                        Text("$${subtotal.toInt()}")
                    }
                    if (discount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Coupon Discount", color = MaterialTheme.colorScheme.primary)
                            Text("-$${discount.toInt()}", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shipping", color = Color.Gray)
                        Text(if (shippingFee == 0.0) "Free" else "$${shippingFee.toInt()}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("VAT Tax", color = Color.Gray)
                        Text("$${tax.toInt()}")
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Final Total", fontWeight = FontWeight.Black)
                        Text("$${total.toInt()}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Place order button
        Surface(
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.placeOrder { orderId ->
                        navController.navigate("tracking")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(52.dp)
                    .testTag("place_order_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Place Order & Pay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- ORDER TRACKING / PROGRESS SCREEN ---
@Composable
fun OrderTrackingScreen(
    viewModel: CloversViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val selectedOrder by viewModel.selectedOrder.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(Icons.Default.Home, "Home")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Order Track & Trace", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        if (selectedOrder == null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No order active. Place an order to track it here.")
            }
            return
        }

        val order = selectedOrder!!

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Congrats Title
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Your Clovers order has been dispatched! ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Order Reference: ${order.id}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }

            // Tracking progress steps
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text("Shipping Progress", fontWeight = FontWeight.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(20.dp))

                val steps = listOf("Confirmed", "Preparing", "Shipped", "Out for Delivery", "Delivered")
                val activeIndex = steps.indexOf(order.status)

                steps.forEachIndexed { index, step ->
                    val isDone = index <= activeIndex
                    val isCurrent = index == activeIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dot tracker circle
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    if (isDone) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = step,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDone) MaterialTheme.colorScheme.onBackground else Color.Gray,
                                fontSize = 15.sp
                            )
                            if (isCurrent) {
                                Text(
                                    text = "Active Status - Tracking live",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    if (index < steps.size - 1) {
                        // Vertical tracker line
                        Box(
                            modifier = Modifier
                                .padding(start = 11.dp)
                                .width(2.dp)
                                .height(24.dp)
                                .background(if (index < activeIndex) MaterialTheme.colorScheme.primary else Color.LightGray)
                        )
                    }
                }
            }

            // Delivery Details block
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Delivery Particulars", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Delivery Address:", fontSize = 12.sp, color = Color.Gray)
                    Text(order.address, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Selected Courier:", fontSize = 12.sp, color = Color.Gray)
                    Text(order.shippingMethod, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Continue Shopping", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- WISHLIST SCREEN ---
@Composable
fun WishlistScreen(
    viewModel: CloversViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val wishlist by viewModel.wishlist.collectAsState()
    val products by viewModel.products.collectAsState()

    var previewProduct by remember { mutableStateOf<Product?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Curated Wishlist", fontSize = 20.sp, fontWeight = FontWeight.Black)
            }

            if (wishlist.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No favorite pieces saved yet.", color = Color.Gray, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(wishlist) { item ->
                        // Map back to dynamic Product model to reuse card cleanly
                        val fullProduct = products.find { it.id == item.productId }
                        if (fullProduct != null) {
                            ProductCard(
                                product = fullProduct,
                                viewModel = viewModel,
                                onQuickPreview = { previewProduct = fullProduct },
                                onClick = {
                                    navController.navigate("product/${fullProduct.id}")
                                }
                            )
                        }
                    }
                }
            }
        }

        previewProduct?.let { p ->
            QuickPreviewDialog(
                product = p,
                viewModel = viewModel,
                onDismiss = { previewProduct = null }
            )
        }
    }
}

// --- PROFILE & SETTINGS SCREEN ---
@Composable
fun ProfileScreen(
    viewModel: CloversViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val orders by viewModel.orders.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Photo & Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = currentUser?.profilePhotoUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=200",
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                currentUser?.name ?: "Aura Clovers",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
            Text(
                currentUser?.email ?: "bxer5g@gmail.com",
                color = Color.Gray,
                fontSize = 13.sp
            )
            Text(
                currentUser?.phone ?: "+66 81 234 5678",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }

        Divider(color = MaterialTheme.colorScheme.surfaceVariant)

        // Historical Orders list summary
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Your Purchase History", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            if (orders.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text("No order history yet.", modifier = Modifier.padding(16.dp), color = Color.Gray)
                }
            } else {
                orders.forEach { ord ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Order Ref: ${ord.id}", fontWeight = FontWeight.Bold)
                                Text("Amount Paid: $${ord.total.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(ord.status, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("logout_button")
        ) {
            Text("Sign Out Session", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// --- GEMINI POWERED AI STYLIST CHAT SCREEN ---
@Composable
fun AiStylistScreen(
    viewModel: CloversViewModel,
    modifier: Modifier = Modifier
) {
    val stylistMessages by viewModel.stylistMessages.collectAsState()
    val isStylistLoading by viewModel.isStylistLoading.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val suggestions = listOf(
        "I need a formal outfit for an interview",
        "Oversized black hoodie under $100",
        "What is your return policy?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Concierge Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Spa, "Concierge Sparkle", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Aura AI Stylist Suite", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Powered by Gemini 3.5 Flash", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stylistMessages) { msg ->
                val isUser = msg.isUser
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                )
                            )
                            .background(
                                if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = msg.text,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            if (isStylistLoading) {
                item {
                    Row(horizontalArrangement = Arrangement.Start) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Concierge Aura is thinking...", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestions) { sug ->
                Card(
                    modifier = Modifier.clickable { 
                        viewModel.sendStylistMessage(sug)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = sug,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Input Keyboard block
        Surface(
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = { Text("Ask Aura about tailoring, size recommendations...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text"),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = {
                        val txt = chatInput
                        chatInput = ""
                        viewModel.sendStylistMessage(txt)
                        scope.launch {
                            delay(300)
                            listState.animateScrollToItem(stylistMessages.size)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .testTag("send_chat_btn"),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Send, "Send")
                }
            }
        }
    }
}

// --- ADMIN CONTROL DASHBOARD ---
@Composable
fun AdminDashboardScreen(
    viewModel: CloversViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val orders by viewModel.orders.collectAsState()

    var activeAdminTab by remember { mutableStateOf("Dashboard") } // Dashboard, Products, Orders

    // New Product form inputs
    var prodName by remember { mutableStateOf("") }
    var prodBrand by remember { mutableStateOf("") }
    var prodDesc by remember { mutableStateOf("") }
    var prodPrice by remember { mutableStateOf("") }
    var prodImage by remember { mutableStateOf("https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?q=80&w=600") }
    var prodCat by remember { mutableStateOf("Women") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Clovers Admin Panel", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Operational Control Suite", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
            IconButton(
                onClick = { viewModel.toggleAdminMode() },
                modifier = Modifier.testTag("admin_close")
            ) {
                Icon(Icons.Default.Close, "Exit Admin", tint = Color.White)
            }
        }

        // Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Dashboard", "Add Product", "Manage Orders").forEach { tab ->
                val isSel = activeAdminTab == tab
                TextButton(onClick = { activeAdminTab = tab }) {
                    Text(
                        text = tab,
                        color = if (isSel) MaterialTheme.colorScheme.primary else Color.Gray,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (activeAdminTab) {
                "Dashboard" -> {
                    // Analytics Summary
                    Text("Store Metrics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Sales Revenue", fontSize = 11.sp, color = Color.Gray)
                                Text("$12,450", fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Card(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Active Orders", fontSize = 11.sp, color = Color.Gray)
                                Text(orders.size.toString(), fontWeight = FontWeight.Black, fontSize = 20.sp)
                            }
                        }
                    }

                    // Inventory summary
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total Catalog Items: ${products.size}", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Total stock levels: ${products.sumOf { it.stock }} pieces", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                "Add Product" -> {
                    Text("Configure Apparel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    OutlinedTextField(
                        value = prodName,
                        onValueChange = { prodName = it },
                        label = { Text("Apparel Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = prodBrand,
                        onValueChange = { prodBrand = it },
                        label = { Text("Designer Brand") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = prodDesc,
                        onValueChange = { prodDesc = it },
                        label = { Text("Fabric & Style Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = prodPrice,
                        onValueChange = { prodPrice = it },
                        label = { Text("Base Price ($)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = prodImage,
                        onValueChange = { prodImage = it },
                        label = { Text("Product Image URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category dropdown simulator
                    Column {
                        Text("Category: $prodCat", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            listOf("Men", "Women", "Kids", "Shoes", "Bags", "Sportswear").forEach { cat ->
                                Card(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clickable { prodCat = cat },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (prodCat == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Text(
                                        cat,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = if (prodCat == cat) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.adminAddProduct(
                                name = prodName,
                                brand = prodBrand,
                                description = prodDesc,
                                price = prodPrice.toDoubleOrNull() ?: 99.0,
                                discount = 0.0,
                                image = prodImage,
                                category = prodCat,
                                sizes = listOf("S", "M", "L"),
                                colors = listOf("#222222", "#2E8B57")
                            )
                            activeAdminTab = "Dashboard"
                            // clear form
                            prodName = ""
                            prodBrand = ""
                            prodDesc = ""
                            prodPrice = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_add_product_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save & Publish Product", fontWeight = FontWeight.Bold)
                    }
                }

                "Manage Orders" -> {
                    Text("Modify Dispatch Flow", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (orders.isEmpty()) {
                        Text("No orders placed to manage.", color = Color.Gray)
                    } else {
                        orders.forEach { ord ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("Ref: ${ord.id}", fontWeight = FontWeight.Bold)
                                        Text("Total: $${ord.total.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Current State: ${ord.status}", fontSize = 12.sp, color = Color.Gray)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Direct control triggers
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                        listOf("Preparing", "Shipped", "Out for Delivery", "Delivered").forEach { state ->
                                            val progress = when(state) {
                                                "Preparing" -> 0.4f
                                                "Shipped" -> 0.6f
                                                "Out for Delivery" -> 0.8f
                                                else -> 1.0f
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.adminUpdateOrderStatus(ord.id, state, progress) },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.testTag("admin_status_${ord.id}_${state.lowercase().replace(" ", "")}")
                                            ) {
                                                Text(state, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
