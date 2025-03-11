package com.investwise_india

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.investwise_india.di.NetworkModule
import com.investwise_india.ui.navigation.AppNavigation
import com.investwise_india.ui.navigation.Screen
import com.investwise_india.ui.theme.InvestWise_IndiaTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the web client ID from google-services.json
        // This should be replaced with your actual web client ID from Firebase console
        val webClientId = getString(R.string.default_web_client_id)
        
        setContent {
            InvestWise_IndiaTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                // Initialize Firebase Auth
                val auth = FirebaseAuth.getInstance()
                val currentUser by remember { mutableStateOf(auth.currentUser) }
                
                Scaffold(
                    bottomBar = {
                        // Only show bottom navigation on main screens
                        if (currentRoute in listOf(
                                Screen.Home.route,
                                Screen.Compare.route,
                                Screen.MutualFunds.route,
                                Screen.Account.route
                            )
                        ) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Home.route,
                                    onClick = { navController.navigate(Screen.Home.route) },
                                    icon = { Icon(Icons.Default.Home, "Home") },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Compare.route,
                                    onClick = { navController.navigate(Screen.Compare.route) },
                                    icon = { 
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_compare_arrows_24),
                                            contentDescription = "Compare"
                                        )
                                    },
                                    label = { Text("Compare") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.MutualFunds.route,
                                    onClick = { navController.navigate(Screen.MutualFunds.route) },
                                    icon = { Icon(Icons.Default.List, "Mutual Funds") },
                                    label = { Text("Funds") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Account.route,
                                    onClick = { navController.navigate(Screen.Account.route) },
                                    icon = { Icon(Icons.Default.Person, "Account") },
                                    label = { Text("Account") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Use the existing AppNavigation component
                        AppNavigation(
                            navController = navController,
                            apiService = NetworkModule.mutualFundApiService,
                            currentUser = currentUser,
                            webClientId = webClientId,
                            onSignOut = {
                                auth.signOut()
                                // Refresh the account screen
                                navController.navigate(Screen.Account.route) {
                                    popUpTo(Screen.Account.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}