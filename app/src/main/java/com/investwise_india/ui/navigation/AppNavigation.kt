package com.investwise_india.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
//import androidx.navigation.navType.navArgument
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.ui.screens.*
import com.investwise_india.model.MutualFund
import com.investwise_india.model.MutualFundCategories
import com.investwise_india.model.InvestmentOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.investwise_india.auth.AuthScreen
import com.investwise_india.model.DebtFundSubcategories
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
    object ChatBot : Screen("chatbot")
    object MutualFundList : Screen("mutual_funds/{categoryId}") {
        fun createRoute(categoryId: Int) = "mutual_funds/$categoryId"
    }
    object MutualFundSubcategory : Screen("mutual_funds/{categoryId}/{subcategoryId}") {
        fun createRoute(categoryId: Int, subcategoryId: Int) = "mutual_funds/$categoryId/$subcategoryId"
    }
    object FundDetail : Screen("fund_detail/{schemeCode}")
    object Profile : Screen("profile")

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
                },
                user = currentUserState
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

        composable(Screen.ChatBot.route) {
            ChatPage(
                modifier = Modifier,
                viewModel = viewModel()
            )
        }

        composable(Screen.MutualFunds.route) {
            MutualFundScreen(
                onCategorySelected = { category ->
                    if (category.id == 5) { // Debt Fund
                        // Navigate to subcategories list
                        navController.navigate(Screen.MutualFundList.createRoute(category.id))
                    } else {
                        // Navigate directly to fund list for other categories
                        navController.navigate(Screen.MutualFundList.createRoute(category.id))
                    }
                }
            )
        }

        composable(
            route = Screen.MutualFundList.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: return@composable
            val category = MutualFundCategories.categories.find { it.id == categoryId }

            if (category != null) {
                MutualFundListScreen(
                    category = category,
                    apiService = apiService,
                    onBackPressed = { navController.navigateUp() },
                    onSubcategorySelected = { subcategoryId ->
                        if (categoryId == 5) { // Debt Fund
                            navController.navigate(
                                Screen.MutualFundSubcategory.createRoute(categoryId, subcategoryId)
                            )
                        }
                    }
                )
            }
        }

        composable(
            route = Screen.MutualFundSubcategory.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("subcategoryId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: return@composable
            val subcategoryId = backStackEntry.arguments?.getInt("subcategoryId") ?: return@composable
            val category = MutualFundCategories.categories.find { it.id == categoryId }
            val subcategory = DebtFundSubcategories.subcategories.find { it.id == subcategoryId }

            if (category != null && subcategory != null) {
                MutualFundListScreen(
                    category = category,
                    apiService = apiService,
                    onBackPressed = { navController.navigateUp() },
                    selectedSubcategory = subcategory
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