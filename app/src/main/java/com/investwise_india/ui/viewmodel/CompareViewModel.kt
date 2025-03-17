package com.investwise_india.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.investwise_india.model.MutualFund
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.network.MutualFundDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.investwise_india.data.repository.MutualFundRepository
import com.investwise_india.data.DataModule
import retrofit2.Response
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.sqrt
import com.investwise_india.util.RatioCalculator

/**
 * ViewModel for the Compare Screen that handles the business logic and state management
 * for comparing mutual funds.
 */
class CompareViewModel(
    private val apiService: MutualFundApiService
) : ViewModel() {

    // Get the repository instance
    private val repository: MutualFundRepository = DataModule.mutualFundRepository

    // State for the list of selected funds to compare
    private val _selectedFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val selectedFunds: StateFlow<List<MutualFund>> = _selectedFunds.asStateFlow()

    // State for the details of each fund
    private val _fundDetails = MutableStateFlow<Map<Int, MutualFundDetails>>(emptyMap())
    val fundDetails: StateFlow<Map<Int, MutualFundDetails>> = _fundDetails.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Dialog state
    private val _showSelectFundDialog = MutableStateFlow(false)
    val showSelectFundDialog: StateFlow<Boolean> = _showSelectFundDialog.asStateFlow()
    
    // Track which funds have already been loaded to prevent reloading on configuration changes
    private val loadedFundCodes = mutableSetOf<Int>()

    /**
     * Add a fund to the comparison list
     * @param fund The mutual fund to add
     */
    fun addFund(fund: MutualFund) {
        if (_selectedFunds.value.size < 3 && !_selectedFunds.value.contains(fund)) {
            _selectedFunds.update { currentList ->
                currentList + fund
            }
            // Load the details for the newly added fund only
            loadFundDetails(listOf(fund))
        }
    }

    /**
     * Remove a fund from the comparison list
     * @param fund The mutual fund to remove
     */
    fun removeFund(fund: MutualFund) {
        _selectedFunds.update { currentList ->
            currentList.filter { it != fund }
        }
        // Update the fund details map to remove the details for the removed fund
        _fundDetails.update { currentMap ->
            currentMap.toMutableMap().apply {
                remove(fund.schemeCode)
            }
        }
        // Remove from loaded funds tracking
        loadedFundCodes.remove(fund.schemeCode)
    }

    /**
     * Show the select fund dialog
     */
    fun showSelectFundDialog() {
        _showSelectFundDialog.value = true
    }

    /**
     * Hide the select fund dialog
     */
    fun hideSelectFundDialog() {
        _showSelectFundDialog.value = false
    }

    /**
     * Load the details for all selected funds
     */
    fun loadFundDetails() {
        loadFundDetails(_selectedFunds.value)
    }
    
    /**
     * Load the details for specific funds
     * @param funds The list of funds to load details for
     */
    private fun loadFundDetails(funds: List<MutualFund>) {
        if (funds.isEmpty()) return
        
        // Filter out funds that have already been loaded
        val fundsToLoad = funds.filter { !loadedFundCodes.contains(it.schemeCode) }
        
        if (fundsToLoad.isEmpty()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Create a map to store the results
                val newDetails = mutableMapOf<Int, MutualFundDetails>()
                
                // Launch parallel requests for each fund, using the repository
                val deferredResults = fundsToLoad.map { fund ->
                    async(Dispatchers.IO) {
                        try {
                            // Use the repository to get fund details with calculated ratios
                            // The repository handles both random and real ratios consistently
                            val details = repository.getMutualFundDetails(fund.schemeCode)
                            if (details != null) {
                                fund.schemeCode to details
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            // Log the error but don't fail the entire operation
                            println("Error loading details for fund ${fund.schemeCode}: ${e.message}")
                            null
                        }
                    }
                }
                
                // Wait for all requests to complete
                deferredResults.forEach { deferred ->
                    deferred.await()?.let { (schemeCode, details) ->
                        newDetails[schemeCode] = details
                        loadedFundCodes.add(schemeCode) // Mark as loaded
                    }
                }
                
                // Update the UI by merging with existing details
                _fundDetails.update { currentDetails ->
                    currentDetails + newDetails
                }
                
                // Set up a periodic check for updated ratios
                setupPeriodicRatioCheck(fundsToLoad)
                
                // Only show error if we couldn't load any funds
                if (newDetails.isEmpty() && fundsToLoad.isNotEmpty()) {
                    _error.value = "Error loading the mutual funds. Please try again."
                } else {
                    _error.value = null
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.SocketTimeoutException -> "Connection timed out. Please check your internet and try again."
                    is java.net.UnknownHostException -> "No internet connection. Please check your network settings."
                    is retrofit2.HttpException -> "Server error (${e.code()}). Please try again later."
                    else -> "Error loading the mutual funds: ${e.message}"
                }
                _error.value = errorMessage
                _isLoading.value = false
            }
        }
    }

    /**
     * Set up periodic checks to see if real ratios have been calculated
     * and update the UI accordingly
     */
    private fun setupPeriodicRatioCheck(funds: List<MutualFund>) {
        viewModelScope.launch {
            // Check once after a delay to see if real ratios are calculated
            kotlinx.coroutines.delay(3000)
            
            funds.forEach { fund ->
                try {
                    // Get the latest details (might have real ratios now)
                    val latestDetails = repository.getLatestFundDetails(fund.schemeCode)
                    
                    // Update UI if we have latest details
                    if (latestDetails != null) {
                        _fundDetails.update { currentDetails ->
                            val updatedMap = currentDetails.toMutableMap()
                            updatedMap[fund.schemeCode] = latestDetails
                            updatedMap
                        }
                    }
                } catch (e: Exception) {
                    // Just log, no need to interrupt the process
                    println("Error checking for updated ratios: ${e.message}")
                }
            }
        }
    }

    /**
     * Clear all selected funds and reset the state
     */
    fun clearAllFunds() {
        _selectedFunds.value = emptyList()
        _fundDetails.value = emptyMap()
        _error.value = null
        loadedFundCodes.clear()
    }
} 