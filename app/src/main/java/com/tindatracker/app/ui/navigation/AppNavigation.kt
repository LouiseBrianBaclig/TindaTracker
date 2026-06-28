package com.tindatracker.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.tindatracker.app.ui.screens.compare.CompareScreen
import com.tindatracker.app.ui.screens.compare.CompareViewModel
import com.tindatracker.app.ui.screens.home.HomeScreen
import com.tindatracker.app.ui.screens.products.ProductsScreen
import com.tindatracker.app.ui.screens.shopping.ShoppingListScreen
import com.tindatracker.app.ui.screens.stores.StoresScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // CompareViewModel is Activity-scoped so ProductsScreen can push a product into it
    // before navigating to the Compare tab.
    val compareViewModel: CompareViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Screen.bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == screen.route
                    } == true

                    NavigationBarItem(
                        selected    = selected,
                        onClick     = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon  = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Products.route) {
                ProductsScreen(
                    navController      = navController,
                    onNavigateToCompare = { productId ->
                        compareViewModel.selectProduct(productId)
                        navController.navigate(Screen.Compare.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
            composable(Screen.Compare.route) {
                CompareScreen(viewModel = compareViewModel)
            }
            composable(Screen.ShoppingList.route) {
                ShoppingListScreen()
            }
            composable(Screen.Stores.route) {
                StoresScreen()
            }
        }
    }
}
