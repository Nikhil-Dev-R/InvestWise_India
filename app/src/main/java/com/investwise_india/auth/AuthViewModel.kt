package com.investwise_india.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    
    init {
        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }
    
    fun getGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        
        return GoogleSignIn.getClient(context, gso)
    }
    
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = auth.signInWithCredential(credential).await()
                
                // Update the user's display name if it's not set
                if (result.user?.displayName.isNullOrBlank() && !account.displayName.isNullOrBlank()) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(account.displayName)
                        .build()
                    
                    result.user?.updateProfile(profileUpdates)?.await()
                    
                    // Force refresh the current user to get updated profile
                    result.user?.reload()?.await()
                    _currentUser.value = auth.currentUser
                }
                
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Google sign in failed", e)
                _authState.value = AuthState.Error(e.message ?: "Google sign in failed")
            }
        }
    }
    
    fun handleGoogleSignInResult(data: android.content.Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            signInWithGoogle(account)
        } catch (e: ApiException) {
            Log.e("AuthViewModel", "Google sign in failed", e)
            _authState.value = AuthState.Error("Google sign in failed: ${e.statusCode}")
        }
    }
    
    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                auth.signInWithEmailAndPassword(email, password).await()
                
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Email sign in failed", e)
                _authState.value = AuthState.Error(e.message ?: "Email sign in failed")
            }
        }
    }
    
    fun createAccountWithEmailPassword(email: String, password: String, displayName: String = "") {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                
                // Set display name if provided
                if (displayName.isNotBlank()) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    
                    result.user?.updateProfile(profileUpdates)?.await()
                    
                    // Force refresh the current user to get updated profile
                    auth.currentUser?.reload()?.await()
                    _currentUser.value = auth.currentUser
                }
                
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Account creation failed", e)
                _authState.value = AuthState.Error(e.message ?: "Account creation failed")
            }
        }
    }
    
    fun updateUserDisplayName(displayName: String, context: Context) {
        viewModelScope.launch {
            try {
                Toast.makeText(context, "Reach in UpdateUserDisplayName", Toast.LENGTH_SHORT).show()
                _authState.value = AuthState.Loading
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    user.updateProfile(profileUpdates).await()
                    
                    // Force refresh the current user to get updated profile
                    user.reload().await()
                    // Now we can use reloaded user
                    _currentUser.value = FirebaseAuth.getInstance().currentUser
                    
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("No user is signed in")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Profile update failed", e)
                _authState.value = AuthState.Error(e.message ?: "Profile update failed")
            }
        }
    }
    
    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
} 