package com.investwise_india.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.investwise_india.ui.screens.AccountScreen

@Composable
fun AuthScreen(
    webClientId: String,
    viewModel: AuthViewModel = viewModel(),
    onAuthComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val authState by viewModel.authState.collectAsState()
    
    // For Google Sign-In
    val googleSignInClient = remember {
        viewModel.getGoogleSignInClient(context, webClientId)
    }
    
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.handleGoogleSignInResult(result.data)
        }
    }
    
    // For tracking sign-up vs sign-in mode
    var isSignUp by remember { mutableStateOf(false) }
    
    // When authentication is successful, call onAuthComplete
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthComplete()
        }
    }
    
    // Show loading indicator when authentication is in progress
    if (authState is AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Show error message if authentication failed
        if (authState is AuthState.Error) {
            LaunchedEffect(authState) {
                // You could show a snackbar or toast here
                println("Auth error: ${(authState as AuthState.Error).message}")
            }
        }
        
        // Show the account screen for authentication
        AccountScreen(
            user = currentUser,
            onSignInClick = {
                // Sign out first to ensure account picker is shown
                googleSignInClient.signOut().addOnCompleteListener {
                    // Configure sign-in to request the user's ID, email address, and basic profile
                    val signInIntent = googleSignInClient.signInIntent
                    // Always force showing the account picker
                    signInIntent.putExtra("prompt", "select_account")
                    googleSignInLauncher.launch(signInIntent)
                }
            },
            onSignOutClick = {
                viewModel.signOut()
                googleSignInClient.signOut()
                onAuthComplete()
            },
            onEmailSignInClick = { email, password ->
                if (isSignUp) {
                    viewModel.createAccountWithEmailPassword(email, password)
                } else {
                    viewModel.signInWithEmailPassword(email, password)
                }
            },
            onEmailSignUpClick = { email, password, displayName ->
                viewModel.createAccountWithEmailPassword(email, password, displayName)
            },
            onUpdateProfile = { displayName ->
                viewModel.updateUserDisplayName(displayName)
            }
        )
    }
} 