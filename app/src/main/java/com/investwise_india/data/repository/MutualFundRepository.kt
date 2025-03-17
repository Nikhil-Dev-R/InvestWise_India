package com.investwise_india.data.repository

import com.investwise_india.model.MutualFund
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.network.MutualFundDetails
import com.investwise_india.util.RatioCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

/**
 * Repository for handling mutual fund data operations
 */
class MutualFundRepository(private val apiService: MutualFundApiService) {
    
    // In-memory cache for mutual funds
    private val _allMutualFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val allMutualFunds: Flow<List<MutualFund>> = _allMutualFunds.asStateFlow()
    
    // Cache for raw mutual fund details (without calculated ratios)
    private val _fundDetailsCache = mutableMapOf<Int, MutualFundDetails>()
    
    // Cache for details with calculated random ratios (consistent across screens)
    private val _fundDetailsWithRandomRatiosCache = mutableMapOf<Int, MutualFundDetails>()
    
    // Cache for details with real calculated ratios (when API calls succeed)
    private val _fundDetailsWithRealRatiosCache = mutableMapOf<Int, MutualFundDetails>()
    
    // User's favorite funds
    private val _userFavoriteFunds = MutableStateFlow<List<MutualFund>>(emptyList())
    val userFavoriteFunds: Flow<List<MutualFund>> = _userFavoriteFunds.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error.asStateFlow()
    
    // Data loaded state
    private val _dataLoaded = MutableStateFlow(false)
    val dataLoaded: Flow<Boolean> = _dataLoaded.asStateFlow()
    
    /**
     * Load all mutual funds from the API
     * @return true if successful, false otherwise
     */
    suspend fun loadAllMutualFunds(): Boolean = withContext(Dispatchers.IO) {
        if (_dataLoaded.value) return@withContext true
        
        _isLoading.value = true
        _error.value = null
        
        try {
            val response = apiService.getAllMutualFunds()
            if (response.isSuccessful) {
                response.body()?.let { funds ->
                    _allMutualFunds.value = funds
                    _dataLoaded.value = true
                    _isLoading.value = false
                    return@withContext true
                }
            }
            _error.value = "Failed to load mutual funds: ${response.message()}"
            _isLoading.value = false
            return@withContext false
        } catch (e: IOException) {
            _error.value = "Network error: ${e.message}"
            _isLoading.value = false
            return@withContext false
        } catch (e: Exception) {
            _error.value = "Error loading mutual funds: ${e.message}"
            _isLoading.value = false
            return@withContext false
        }
    }
    
    /**
     * Get all mutual funds
     * @return List of all mutual funds
     */
    suspend fun getAllMutualFunds(): List<MutualFund> = withContext(Dispatchers.IO) {
        // If we already have data loaded, return it
        if (_dataLoaded.value && _allMutualFunds.value.isNotEmpty()) {
            return@withContext _allMutualFunds.value
        }
        
        // Otherwise, load the data first
        val success = loadAllMutualFunds()
        if (success) {
            return@withContext _allMutualFunds.value
        }
        
        // Return empty list if loading failed
        return@withContext emptyList()
    }
    
    /**
     * Get popular mutual funds
     * @return List of popular mutual funds
     */
    suspend fun getPopularFunds(): List<MutualFund> = withContext(Dispatchers.IO) {
        // Ensure we have all funds loaded
        val allFunds = getAllMutualFunds()
        if (allFunds.isEmpty()) return@withContext emptyList()
        
        // For now, just return a subset of all funds as "popular"
        // In a real app, this would be based on popularity metrics
        return@withContext allFunds.take(10)
    }
    
    /**
     * Get top performing mutual funds
     * @return List of top performing mutual funds
     */
    suspend fun getTopPerformingFunds(): List<MutualFund> = withContext(Dispatchers.IO) {
        // Ensure we have all funds loaded
        val allFunds = getAllMutualFunds()
        if (allFunds.isEmpty()) return@withContext emptyList()
        
        // For now, just return a different subset of all funds as "top performing"
        // In a real app, this would be based on performance metrics
        return@withContext allFunds.takeLast(10)
    }
    
    /**
     * Get user's favorite mutual funds
     * @return List of user's favorite mutual funds
     */
    suspend fun getUserFavoriteFunds(): List<MutualFund> = withContext(Dispatchers.IO) {
        // In a real app, this would fetch from a local database or remote API
        // For now, just return the in-memory list
        return@withContext _userFavoriteFunds.value
    }
    
    /**
     * Save a mutual fund to user's favorites
     * @param fund The mutual fund to save
     */
    suspend fun saveFavoriteFund(fund: MutualFund) = withContext(Dispatchers.IO) {
        // In a real app, this would save to a local database or remote API
        // For now, just update the in-memory list
        if (!_userFavoriteFunds.value.contains(fund)) {
            _userFavoriteFunds.value = _userFavoriteFunds.value + fund
        }
    }
    
    /**
     * Remove a mutual fund from user's favorites
     * @param schemeCode The scheme code of the mutual fund to remove
     */
    suspend fun removeFavoriteFund(schemeCode: Int) = withContext(Dispatchers.IO) {
        // In a real app, this would remove from a local database or remote API
        // For now, just update the in-memory list
        _userFavoriteFunds.value = _userFavoriteFunds.value.filter { it.schemeCode != schemeCode }
    }
    
    /**
     * Get mutual fund details from the API or cache
     * This gets the raw details without calculated ratios
     * @param schemeCode The scheme code of the mutual fund
     * @return The mutual fund details
     */
    suspend fun getRawMutualFundDetails(schemeCode: Int): MutualFundDetails? = withContext(Dispatchers.IO) {
        // Return from cache if available
        _fundDetailsCache[schemeCode]?.let { return@withContext it }
        
        try {
            val response = apiService.getMutualFundDetails(schemeCode)
            if (response.isSuccessful) {
                response.body()?.let { details ->
                    // Cache the result
                    _fundDetailsCache[schemeCode] = details
                    return@withContext details
                }
            }
            return@withContext null
        } catch (e: Exception) {
            return@withContext null
        }
    }
    
    /**
     * Get mutual fund details with consistent ratios
     * This method ensures the same initial random ratios are used across screens
     * and manages the real ratio calculations
     * 
     * @param schemeCode The scheme code of the mutual fund
     * @return The mutual fund details with calculated ratios
     */
    suspend fun getMutualFundDetails(schemeCode: Int): MutualFundDetails? = withContext(Dispatchers.IO) {
        // Step 1: Check if we have already calculated real ratios
        _fundDetailsWithRealRatiosCache[schemeCode]?.let { 
            return@withContext it 
        }
        
        // Step 2: Check if we have already calculated random ratios
        _fundDetailsWithRandomRatiosCache[schemeCode]?.let { randomRatios ->
            // Start calculating real ratios in the background
            calculateRealRatiosInBackground(schemeCode)
            // Return random ratios immediately
            return@withContext randomRatios
        }
        
        // Step 3: Get raw details and calculate random ratios
        val rawDetails = getRawMutualFundDetails(schemeCode) ?: return@withContext null
        
        // Calculate random ratios
        val detailsWithRandomRatios = RatioCalculator.generateRandomRatios(rawDetails)
        
        // Cache the random ratios
        _fundDetailsWithRandomRatiosCache[schemeCode] = detailsWithRandomRatios
        
        // Start calculating real ratios in the background
        calculateRealRatiosInBackground(schemeCode)
        
        // Return random ratios immediately
        return@withContext detailsWithRandomRatios
    }
    
    /**
     * Calculate real ratios in the background and update cache
     */
    private suspend fun calculateRealRatiosInBackground(schemeCode: Int) {
        try {
            val rawDetails = _fundDetailsCache[schemeCode] ?: return
            
            // Calculate real ratios
            val detailsWithRealRatios = RatioCalculator.calculateRatios(rawDetails)
            
            // Cache the real ratios
            _fundDetailsWithRealRatiosCache[schemeCode] = detailsWithRealRatios
            
            // Calculation was successful, notify observers
            // Any ViewModel observing this would need to be notified of the change
            // This could be done via a StateFlow/SharedFlow if needed
        } catch (e: Exception) {
            // If calculation fails, we still have the random ratios
            println("Failed to calculate real ratios: ${e.message}")
        }
    }
    
    /**
     * Get the latest fund details (with either random or real ratios)
     * This method should be used to check for updates without triggering new calculations
     */
    fun getLatestFundDetails(schemeCode: Int): MutualFundDetails? {
        // First try to get real ratios
        _fundDetailsWithRealRatiosCache[schemeCode]?.let { 
            return it 
        }
        
        // Fall back to random ratios
        return _fundDetailsWithRandomRatiosCache[schemeCode]
    }
    
    /**
     * Clear the cache
     */
    fun clearCache() {
        _fundDetailsCache.clear()
        _fundDetailsWithRandomRatiosCache.clear()
        _fundDetailsWithRealRatiosCache.clear()
        _dataLoaded.value = false
    }
} 