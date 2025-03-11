package com.investwise_india.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.ui.screens.*
import com.investwise_india.model.MutualFund
import com.investwise_india.model.MutualFundCategories
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.investwise_india.auth.AuthScreen
import com.investwise_india.ui.components.SelectFundDialog

// Define navigation routes
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MutualFunds : Screen("mutual_funds")
    object Compare : Screen("compare")
    object Account : Screen("account")
    object Auth : Screen("auth")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    apiService: MutualFundApiService,
    currentUser: FirebaseUser? = null,
    webClientId: String = "",
    onSignOut: () -> Unit = {}
) {
    var showSelectFundDialog by remember { mutableStateOf(false) }
    var selectedFunds by remember { mutableStateOf<List<MutualFund>>(emptyList()) }
    
    // Initialize Firebase Auth
    val auth = remember { FirebaseAuth.getInstance() }
    var currentUserState by remember { mutableStateOf(currentUser) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        
        composable(Screen.Compare.route) {
            CompareScreen(
                apiService = apiService,
                onBackPressed = { navController.navigateUp() },
                onAddFund = { showSelectFundDialog = true },
                selectedFunds = selectedFunds,
                onRemoveFund = { fund -> 
                    selectedFunds = selectedFunds.filter { it != fund } 
                }
            )

            if (showSelectFundDialog) {
                SelectFundDialog(
                    apiService = apiService,
                    onDismiss = { showSelectFundDialog = false },
                    onFundSelected = { fund ->
                        if (!selectedFunds.contains(fund) && selectedFunds.size < 3) {
                            selectedFunds = selectedFunds + fund
                        }
                        showSelectFundDialog = false
                    }
                )
            }
        }
        
        composable(Screen.MutualFunds.route) {
            MutualFundScreen(
                onCategorySelected = { category ->
                    navController.navigate("mutual_funds/${category.id}")
                }
            )
        }
        
        composable("mutual_funds/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: 1
            val category = MutualFundCategories.categories.find { it.id == categoryId }
            if (category != null) {
                MutualFundListScreen(
                    category = category,
                    apiService = apiService,
                    onBackPressed = { navController.navigateUp() }
                )
            }
        }
        
        composable(Screen.Account.route) {
            AccountScreen(
                user = currentUserState,
                onSignInClick = {
                    // Navigate to auth screen for Google sign-in
                    navController.navigate(Screen.Auth.route)
                },
                onSignOutClick = {
                    auth.signOut()
                    currentUserState = null
                    onSignOut()
                },
                onEmailSignInClick = { email, password ->
                    // Navigate to auth screen for email sign-in
                    navController.navigate("${Screen.Auth.route}?email=$email&password=$password")
                }
            )
        }
        
        composable(Screen.Auth.route) {
            // Auth screen - only shown when user chooses to sign in
            AuthScreen(
                webClientId = webClientId,
                onAuthComplete = {
                    // Return to account screen after authentication
                    navController.popBackStack()
                }
            )
        }
    }
}