package com.investwise_india.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investwise_india.data.DataModule
import com.investwise_india.model.MutualFund
import com.investwise_india.model.MutualFundCategories
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.network.MutualFundDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Mutual Fund screens that handles the business logic and state management
 * for mutual fund related screens.
 */
class MutualFundViewModel(
    private val apiService: MutualFundApiService
) : ViewModel() {

    // Repository
    private val repository = DataModule.mutualFundRepository

    // All mutual funds
    private val _allFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val allFunds: StateFlow<List<MutualFund>> = _allFunds.asStateFlow()

    // Filtered mutual funds
    private val _filteredFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val filteredFunds: StateFlow<List<MutualFund>> = _filteredFunds.asStateFlow()

    // Selected fund details
    private val _selectedFundDetails = MutableStateFlow<MutualFundDetails?>(null)
    val selectedFundDetails: StateFlow<MutualFundDetails?> = _selectedFundDetails.asStateFlow()

    // Selected category filter
    private val _selectedCategory = MutableStateFlow<Int?>(null)
    val selectedCategory: StateFlow<Int?> = _selectedCategory.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // User favorites
    private val _favoriteFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val favoriteFunds: StateFlow<List<MutualFund>> = _favoriteFunds.asStateFlow()

    // Initialize the ViewModel
    init {
        loadAllFunds()
        loadUserFavorites()
    }

    /**
     * Load all mutual funds
     */
    private fun loadAllFunds() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val funds = repository.getAllMutualFunds()
                _allFunds.value = funds
                _filteredFunds.value = funds
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load mutual funds: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Load user's favorite funds
     */
    private fun loadUserFavorites() {
        viewModelScope.launch {
            try {
                val favorites = repository.getUserFavoriteFunds()
                _favoriteFunds.value = favorites
            } catch (e: Exception) {
                // Just log the error, don't show to user as this is not critical
                println("Failed to load favorites: ${e.message}")
            }
        }
    }

    /**
     * Load details for a specific mutual fund
     */
    fun loadFundDetails(schemeCode: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val details = repository.getMutualFundDetails(schemeCode)
                _selectedFundDetails.value = details
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load fund details: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Filter funds by category
     */
    fun filterByCategory(categoryId: Int?) {
        _selectedCategory.value = categoryId
        applyFilters()
    }

    /**
     * Set search query and filter funds
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    /**
     * Apply all filters (category and search)
     */
    private fun applyFilters() {
        val categoryId = _selectedCategory.value
        val query = _searchQuery.value.lowercase()

        _filteredFunds.update { funds ->
            _allFunds.value.filter { fund ->
                val matchesCategory = categoryId == null || 
                    // In a real app, you would have a category field in the MutualFund model
                    // This is a placeholder for the filtering logic
                    true
                
                val matchesSearch = query.isEmpty() || 
                    fund.schemeName.lowercase().contains(query)
                
                matchesCategory && matchesSearch
            }
        }
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
                    repository.saveFavoriteFund(fund)
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
                repository.removeFavoriteFund(fund.schemeCode)
            } catch (e: Exception) {
                _error.value = "Failed to remove favorite: ${e.message}"
            }
        }
    }

    /**
     * Check if a fund is in favorites
     */
    fun isFavorite(fund: MutualFund): Boolean {
        return _favoriteFunds.value.any { it.schemeCode == fund.schemeCode }
    }

    /**
     * Refresh all data
     */
    fun refreshData() {
        loadAllFunds()
        loadUserFavorites()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
} 