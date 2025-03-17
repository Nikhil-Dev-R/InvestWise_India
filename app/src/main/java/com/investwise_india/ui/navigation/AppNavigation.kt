package com.investwise_india.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.ui.screens.*
import com.investwise_india.model.MutualFund
import com.investwise_india.model.MutualFundCategories
import com.investwise_india.model.InvestmentOption
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
    object InvestmentDetail : Screen("investment_detail")
    object Categories : Screen("categories")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object MutualFundList : Screen("mutual_fund_list")
    object FundDetail : Screen("fund_detail/{schemeCode}")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
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
    
    // State for holding the selected investment for details screen
    var selectedInvestmentForDetails by remember { mutableStateOf<InvestmentOption?>(null) }
    
    // Initialize Firebase Auth
    val auth = remember { FirebaseAuth.getInstance() }
    var currentUserState by remember { mutableStateOf(currentUser) }
    
    // Update currentUserState when currentUser changes
    LaunchedEffect(currentUser) {
        currentUserState = currentUser
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onInvestmentClick = { investment ->
                    selectedInvestmentForDetails = investment
                    navController.navigate(Screen.InvestmentDetail.route)
                }
            )
        }
        
        composable(Screen.Compare.route) {
            CompareScreen(
                apiService = apiService,
                onBackPressed = { navController.navigateUp() },
                onAddFund = { /* This is now handled by the ViewModel */ },
                onRemoveFund = { /* This is now handled by the ViewModel */ }
            )
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
                    // Instead of just popping back, we'll navigate to Account and clear the back stack
                    navController.navigate(Screen.Account.route) {
                        // Pop up to Account route to remove Auth from back stack
                        popUpTo(Screen.Account.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.InvestmentDetail.route) {
            // Only show the detail screen if we have a selected investment
            selectedInvestmentForDetails?.let { investment ->
                InvestmentDetailScreen(
                    investment = investment,
                    onBackPressed = { navController.popBackStack() }
                )
            } ?: run {
                // If no investment is selected, go back to home
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}