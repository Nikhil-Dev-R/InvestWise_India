package com.investwise_india.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Comparison : BottomNavItem("comparison", Icons.Default.List, "Compare")
    object MutualFunds : BottomNavItem("mutual_funds", Icons.Default.Info, "Mutual Funds")
    object Account : BottomNavItem("account", Icons.Default.AccountCircle, "Account")
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (BottomNavItem) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Comparison,
        BottomNavItem.MutualFunds,
        BottomNavItem.Account
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item) }
            )
        }
    }
} 