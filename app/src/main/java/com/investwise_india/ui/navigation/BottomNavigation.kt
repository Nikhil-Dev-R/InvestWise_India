package com.investwise_india.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.investwise_india.R

@Composable
fun BottomNavigationBar(
    navController: NavController,
    showBottomBar: Boolean = true
) {
    if (!showBottomBar) return

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        // Home
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = { 
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, "Home") },
            label = { Text("Home") }
        )

        // Compare
        NavigationBarItem(
            selected = currentRoute == Screen.Compare.route,
            onClick = { 
                if (currentRoute != Screen.Compare.route) {
                    navController.navigate(Screen.Compare.route) {
                        popUpTo(Screen.Compare.route) { inclusive = true }
                    }
                }
            },
            icon = { 
                Icon(
                    painter = painterResource(R.drawable.baseline_compare_arrows_24),
                    contentDescription = "Compare"
                )
            },
            label = { Text("Compare") }
        )

        // Mutual Funds
        NavigationBarItem(
            selected = currentRoute == Screen.MutualFunds.route,
            onClick = { 
                if (currentRoute != Screen.MutualFunds.route) {
                    navController.navigate(Screen.MutualFunds.route) {
                        popUpTo(Screen.MutualFunds.route) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.List, "Mutual Funds") },
            label = { Text("Funds") }
        )

        // Account
        NavigationBarItem(
            selected = currentRoute == Screen.Account.route,
            onClick = { 
                if (currentRoute != Screen.Account.route) {
                    navController.navigate(Screen.Account.route) {
                        popUpTo(Screen.Account.route) { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.Person, "Account") },
            label = { Text("Account") }
        )
    }
} 