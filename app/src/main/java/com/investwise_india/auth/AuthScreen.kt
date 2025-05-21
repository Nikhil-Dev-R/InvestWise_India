package com.investwise_india.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.investwise_india.R
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

    Log.d("AuthScreen", "1st time Auth Screen")


    // For Google Sign-In
        val googleSignInClient = remember {
            viewModel.getGoogleSignInClient(context, webClientId)
        }

    Log.d("AuthScreen", "2nd time Auth Screen")

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.handleGoogleSignInResult(result.data)
        }
    }

    Log.d("AuthScreen", "3rd time Auth Screen")

    // For tracking sign-up vs sign-in mode
    var isSignUp by remember { mutableStateOf(false) }
    
    // When authentication is successful, call onAuthComplete
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthComplete()
        }
    }
    Log.d("AuthScreen", "4th time Auth Screen")
    // Show loading indicator when authentication is in progress
    if (authState is AuthState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        Log.d("AuthScreen", "5th time Auth Screen")
    } else {
        // Show error message if authentication failed
        if (authState is AuthState.Error) {
            LaunchedEffect(authState) {
                // You could show a snackbar or toast here
                println("Auth error: ${(authState as AuthState.Error).message}")
            }
        }
        Log.d("AuthScreen", "6th time Auth Screen")
        
        // Show the account screen for authentication
        AccountScreen(
            user = currentUser,
            onSignInClick = {
                Log.d("AuthScreen", "7th time Auth Screen")
                // Sign out first to ensure account picker is shown
                    // Configure sign-in to request the user's ID, email address, and basic profile
                    val signInIntent = googleSignInClient.signInIntent
                // Always force showing the account picker
                    signInIntent.putExtra("prompt", "select_account")
                    googleSignInLauncher.launch(signInIntent)

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
                Toast.makeText(context, "Update Profile Auth Screen function", Toast.LENGTH_SHORT).show()
                viewModel.updateUserDisplayName(displayName,context)
            }
        )
        Log.d("AuthScreen", "8th time Auth Screen")
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen(
        webClientId = R.string.default_web_client_id.toString(),
        viewModel = viewModel(),
        onAuthComplete = {}
    )
}
