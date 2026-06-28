package com.tindatracker.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home         : Screen("home",     "Home",      Icons.Filled.Home)
    data object Products     : Screen("products", "Produkto",  Icons.Filled.Category)
    data object Compare      : Screen("compare",  "Ikumpara",  Icons.Filled.CompareArrows)
    data object ShoppingList : Screen("shopping", "Lista",     Icons.Filled.ShoppingCart)
    data object Stores       : Screen("stores",   "Tindahan",  Icons.Filled.Storefront)

    companion object {
        val bottomNavItems = listOf(Home, Products, Compare, ShoppingList, Stores)
    }
}
