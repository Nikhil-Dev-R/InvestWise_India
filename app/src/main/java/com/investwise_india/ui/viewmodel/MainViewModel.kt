package com.investwise_india.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investwise_india.data.DataModule
import com.investwise_india.data.repository.MarketDataRepository
import com.investwise_india.data.repository.MutualFundRepository
import com.investwise_india.model.MutualFund
import com.investwise_india.model.InvestmentOption
import com.investwise_india.model.InvestmentData
import com.investwise_india.network.MutualFundApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MainViewModel serves as the central ViewModel for the app, handling shared state
 * and functionality across different screens.
 */
class MainViewModel(
    private val apiService: MutualFundApiService
) : ViewModel() {

    // Repositories
    private val mutualFundRepository: MutualFundRepository = DataModule.mutualFundRepository
    private val marketDataRepository: MarketDataRepository = DataModule.marketDataRepository

    // User authentication state
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    // App loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Market data state
    private val _marketIndices = MutableStateFlow<Map<String, Double>>(emptyMap())
    val marketIndices: StateFlow<Map<String, Double>> = _marketIndices.asStateFlow()

    // Mutual funds state
    private val _popularFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val popularFunds: StateFlow<List<MutualFund>> = _popularFunds.asStateFlow()

    // Investment options
    private val _investmentOptions = MutableStateFlow(InvestmentData.allOptions)
    val investmentOptions: StateFlow<List<InvestmentOption>> = _investmentOptions.asStateFlow()

    // User favorites
    private val _favoriteFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val favoriteFunds: StateFlow<List<MutualFund>> = _favoriteFunds.asStateFlow()

    // Initialize the ViewModel
    init {
        loadInitialData()
    }

    /**
     * Load initial data for the app
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load market indices
                loadMarketIndices()
                
                // Load popular mutual funds
                loadPopularFunds()
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load initial data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Load market indices data
     */
    private fun loadMarketIndices() {
        viewModelScope.launch {
            try {
                val indices = marketDataRepository.getMarketIndices()
                _marketIndices.value = indices
            } catch (e: Exception) {
                _error.value = "Failed to load market indices: ${e.message}"
            }
        }
    }

    /**
     * Load popular mutual funds
     */
    private fun loadPopularFunds() {
        viewModelScope.launch {
            try {
                val funds = mutualFundRepository.getPopularFunds()
                _popularFunds.value = funds
            } catch (e: Exception) {
                _error.value = "Failed to load popular funds: ${e.message}"
            }
        }
    }

    /**
     * Set user login state
     */
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        _isUserLoggedIn.value = isLoggedIn
        if (isLoggedIn) {
            loadUserData()
        } else {
            clearUserData()
        }
    }

    /**
     * Load user-specific data
     */
    private fun loadUserData() {
        viewModelScope.launch {
            try {
                // Load user's favorite funds
                val favorites = mutualFundRepository.getUserFavoriteFunds()
                _favoriteFunds.value = favorites
            } catch (e: Exception) {
                _error.value = "Failed to load user data: ${e.message}"
            }
        }
    }

    /**
     * Clear user-specific data
     */
    private fun clearUserData() {
        _favoriteFunds.value = emptyList()
    }

    /**
     * Add a fund to favorites
     */
    fun addToFavorites(fund: MutualFund) {
        if (!_favoriteFunds.value.contains(fund)) {
            _favoriteFunds.update { currentList ->
                currentList + fund
            }
            // Persist to backend/local storage
            viewModelScope.launch {
                try {
                    mutualFundRepository.saveFavoriteFund(fund)
                } catch (e: Exception) {
                    _error.value = "Failed to save favorite: ${e.message}"
                }
            }
        }
    }

    /**
     * Remove a fund from favorites
     */
    fun removeFromFavorites(fund: MutualFund) {
        _favoriteFunds.update { currentList ->
            currentList.filter { it.schemeCode != fund.schemeCode }
        }
        // Persist to backend/local storage
        viewModelScope.launch {
            try {
                mutualFundRepository.removeFavoriteFund(fund.schemeCode)
            } catch (e: Exception) {
                _error.value = "Failed to remove favorite: ${e.message}"
            }
        }
    }

    /**
     * Filter investment options by type
     */
    fun filterInvestmentOptions(type: String?) {
        _investmentOptions.value = when (type) {
            "FIXED_RETURN" -> InvestmentData.fixedReturnOptions
            "ABSOLUTE_RETURN" -> InvestmentData.absoluteReturnOptions
            else -> InvestmentData.allOptions
        }
    }

    /**
     * Refresh all data
     */
    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Refresh market indices
                loadMarketIndices()
                
                // Refresh popular funds
                loadPopularFunds()
                
                // Refresh user data if logged in
                if (_isUserLoggedIn.value) {
                    loadUserData()
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to refresh data: ${e.message}"
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