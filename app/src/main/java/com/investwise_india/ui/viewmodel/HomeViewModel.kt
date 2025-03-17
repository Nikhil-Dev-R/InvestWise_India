package com.investwise_india.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investwise_india.data.DataModule
import com.investwise_india.model.MutualFund
import com.investwise_india.model.InvestmentOption
import com.investwise_india.model.InvestmentData
import com.investwise_india.network.MutualFundApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Screen that handles the business logic and state management
 * for the home screen of the app.
 */
class HomeViewModel(
    private val apiService: MutualFundApiService
) : ViewModel() {

    // Repositories
    private val repository = DataModule.mutualFundRepository
    private val marketRepository = DataModule.marketDataRepository

    // Market indices state
    private val _marketIndices = MutableStateFlow<Map<String, Double>>(emptyMap())
    val marketIndices: StateFlow<Map<String, Double>> = _marketIndices.asStateFlow()

    // Top performing funds
    private val _topPerformingFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val topPerformingFunds: StateFlow<List<MutualFund>> = _topPerformingFunds.asStateFlow()

    // Investment options
    private val _investmentOptions = MutableStateFlow(InvestmentData.allOptions)
    val investmentOptions: StateFlow<List<InvestmentOption>> = _investmentOptions.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Selected investment type filter
    private val _selectedInvestmentType = MutableStateFlow<String?>(null)
    val selectedInvestmentType: StateFlow<String?> = _selectedInvestmentType.asStateFlow()

    // Initialize the ViewModel
    init {
        loadHomeData()
    }

    /**
     * Load all data needed for the home screen
     */
    private fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Load market indices
                val indices = marketRepository.getMarketIndices()
                _marketIndices.value = indices
                
                // Load top performing funds
                val topFunds = repository.getTopPerformingFunds()
                _topPerformingFunds.value = topFunds
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load home data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Filter investment options by type
     */
    fun filterInvestmentOptions(type: String?) {
        _selectedInvestmentType.value = type
        
        _investmentOptions.value = when (type) {
            "FIXED_RETURN" -> InvestmentData.fixedReturnOptions
            "ABSOLUTE_RETURN" -> InvestmentData.absoluteReturnOptions
            else -> InvestmentData.allOptions
        }
    }

    /**
     * Refresh all home screen data
     */
    fun refreshData() {
        loadHomeData()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
} 