package com.investwise_india.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.investwise_india.data.DataModule
import com.investwise_india.model.MutualFund
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Account Screen that handles the business logic and state management
 * for user account related functionality.
 */
class AccountViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser
//    val account = GoogleSignIn.getLastSignedInAccount(context)

    // Repository
    private val repository = DataModule.mutualFundRepository

    // User profile data
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _userPhoneNumber = MutableStateFlow<String?>(null)
    val userPhoneNumber: StateFlow<String?> = _userPhoneNumber.asStateFlow()

    private val _userProfilePicture = MutableStateFlow<String?>(null)
    val userProfilePicture: StateFlow<String?> = _userProfilePicture.asStateFlow()

    // User's favorite funds
    private val _favoriteFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val favoriteFunds: StateFlow<List<MutualFund>> = _favoriteFunds.asStateFlow()

    // User's investment portfolio
    private val _portfolioValue = MutableStateFlow(0.0)
    val portfolioValue: StateFlow<Double> = _portfolioValue.asStateFlow()

    private val _portfolioGrowth = MutableStateFlow(0.0)
    val portfolioGrowth: StateFlow<Double> = _portfolioGrowth.asStateFlow()

    // Authentication state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Initialize the ViewModel
    init {
        checkLoginStatus()
    }

    /**
     * Check if the user is logged in
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoading.value = true

            if (currentUser.value != null) {
                _isLoggedIn.value = true
                loadUserData()
            } else {
                _isLoggedIn.value = false
            }
        }
    }

    /**
     * Load user data if logged in
     */
    private fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Load user profile data

                _userName.value = currentUser.value?.displayName
                _userEmail.value = currentUser.value?.email
                _userProfilePicture.value = currentUser.value?.photoUrl.toString()
                
                // Load user's favorite funds
                val favorites = repository.getUserFavoriteFunds()
                _favoriteFunds.value = favorites
                
                // Load portfolio data
                // In a real app, this would come from your portfolio service
                _portfolioValue.value = 100000.0
                _portfolioGrowth.value = 12.5
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load user data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Login with email and password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Simulate login
                // In a real app, this would call your authentication service
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    // Successful login
                    _isLoggedIn.value = true
                    loadUserData()
                } else {
                    // Failed login
                    _error.value = "Invalid email or password"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Login failed: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Register a new user
     */
    fun register(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Simulate registration
                // In a real app, this would call your authentication service
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    // Successful registration
                    _userName.value = name
                    _userEmail.value = email
                    _userPhoneNumber.value = phone
                    _isLoggedIn.value = true
                } else {
                    // Failed registration
                    _error.value = "Please fill all required fields"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Registration failed: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Logout the user
     */
    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Simulate logout
            // In a real app, this would call your authentication service
            _isLoggedIn.value = false
            _userName.value = null
            _userEmail.value = null
            _userPhoneNumber.value = null
            _userProfilePicture.value = null
            _favoriteFunds.value = emptyList()
            _portfolioValue.value = 0.0
            _portfolioGrowth.value = 0.0
            
            _isLoading.value = false
        }
    }

    /**
     * Update user profile
     */
    fun updateProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Simulate profile update
                // In a real app, this would call your user service
                if (name.isNotEmpty() && email.isNotEmpty()) {
                    _userName.value = name
                    _userEmail.value = email
                    _userPhoneNumber.value = phone
                } else {
                    _error.value = "Please fill all required fields"
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Profile update failed: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
} 