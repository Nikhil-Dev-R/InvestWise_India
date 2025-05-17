package com.investwise_india

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.investwise_india.di.NetworkModule
import com.investwise_india.ui.navigation.AppNavigation
import com.investwise_india.ui.navigation.BottomNavigationBar
import com.investwise_india.ui.navigation.Screen
import com.investwise_india.ui.theme.InvestWise_IndiaTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.DisposableEffect
import com.investwise_india.data.DataModule
import com.investwise_india.data.MutualFundDataWorker

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the web client ID
        val webClientId = getString(R.string.default_web_client_id)

        // Start background data loading
        MutualFundDataWorker.scheduleOneTimeWork(this)

        setContent {
            InvestWise_IndiaTheme {
                val navController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                // Initialize Firebase Auth
                val auth = FirebaseAuth.getInstance()
                var currentUser by remember { mutableStateOf(auth.currentUser) }

                // Listen for auth state changes
                DisposableEffect(Unit) {
                    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        currentUser = firebaseAuth.currentUser
                    }
                    auth.addAuthStateListener(authStateListener)

                    onDispose {
                        auth.removeAuthStateListener(authStateListener)
                    }
                }

                // Determine if we should show the bottom bar
                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Compare.route,
                    Screen.MutualFunds.route,
                    Screen.Account.route
                )

                // Observe mutual fund data loading state
                val dataLoaded by DataModule.mutualFundRepository.dataLoaded.collectAsState(initial = false)
                val isLoading by DataModule.mutualFundRepository.isLoading.collectAsState(initial = false)

                // Log the data loading state
                LaunchedEffect(dataLoaded, isLoading) {
                    if (dataLoaded) {
                        android.util.Log.d("MainActivity", "Mutual fund data loaded successfully")
                    } else if (isLoading) {
                        android.util.Log.d("MainActivity", "Loading mutual fund data...")
                    }
                }

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            navController = navController,
                            showBottomBar = showBottomBar
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(
                            navController = navController,
                            apiService = NetworkModule.mutualFundApiService,
                            currentUser = currentUser,
                            webClientId = webClientId,
                            onSignOut = {
                                auth.signOut()
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