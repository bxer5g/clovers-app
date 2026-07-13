package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CloversViewModel
import androidx.compose.ui.platform.testTag

class MainActivity : ComponentActivity() {
    private val viewModel: CloversViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val isAdminMode by viewModel.isAdminMode.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoggedIn) {
                        // Guard Screen
                        LoginRegisterScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    } else if (isAdminMode) {
                        // Admin Dashboard Full Overlay
                        AdminDashboardScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    } else {
                        // Core Shopping Client
                        MainShoppingScaffold(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainShoppingScaffold(viewModel: CloversViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItems = listOf(
        NavigationItem("home", Icons.Default.Home, "Home"),
        NavigationItem("wishlist", Icons.Default.Favorite, "Wishlist"),
        NavigationItem("stylist", Icons.Default.AutoAwesome, "AI Stylist"),
        NavigationItem("cart", Icons.Default.ShoppingBag, "Cart"),
        NavigationItem("profile", Icons.Default.Person, "Profile")
    )

    Scaffold(
        bottomBar = {
            // Standard Bottom Bar (hidden on secondary details/tracking screens for clean visual boundaries)
            val showBottomBar = navItems.any { it.route == currentRoute }
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("client_bottom_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(viewModel = viewModel, navController = navController, modifier = Modifier.fillMaxSize())
            }
            composable("wishlist") {
                WishlistScreen(viewModel = viewModel, navController = navController, modifier = Modifier.fillMaxSize())
            }
            composable("stylist") {
                AiStylistScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            }
            composable("cart") {
                CartScreen(viewModel = viewModel, navController = navController, modifier = Modifier.fillMaxSize())
            }
            composable("profile") {
                ProfileScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            }
            composable("product/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(productId = productId, viewModel = viewModel, navController = navController, modifier = Modifier.fillMaxSize())
            }
            composable("checkout") {
                CheckoutScreen(viewModel = viewModel, navController = navController, modifier = Modifier.fillMaxSize())
            }
            composable("tracking") {
                OrderTrackingScreen(viewModel = viewModel, navController = navController, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)
