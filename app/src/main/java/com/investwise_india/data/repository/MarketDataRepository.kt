package com.investwise_india.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

/**
 * Repository for fetching market data such as index values
 * Uses Yahoo Finance API to get real-time market data
 */
class MarketDataRepository {

    // Cache time in milliseconds (5 minutes)
    private val CACHE_DURATION = 5 * 60 * 1000
    
    // API configuration
        private val YAHOO_API_KEY = "5ebf944549mshee20ac54189c760p155765jsn57e4f50da44c"
    private val YAHOO_API_HOST = "yahoo-finance15.p.rapidapi.com"
    
    // Market index symbols
    private val NIFTY_SYMBOL = "^NSEI"      // Nifty 50
    private val SENSEX_SYMBOL = "^BSESN"    // Sensex
    private val BANKNIFTY_SYMBOL = "^NSEBANK" // Bank Nifty
    
    // StateFlow for market data
    private val _marketIndices = MutableStateFlow<Map<String, MarketIndexData>>(emptyMap())
    val marketIndices: StateFlow<Map<String, MarketIndexData>> = _marketIndices.asStateFlow()
    
    // Last fetch timestamp
    private var lastFetchTime = 0L
    
    /**
     * Get market indices data
     * This method is used by ViewModels to get the current market indices
     * It will fetch fresh data if needed or return cached data
     * @return Map of index symbol to value
     */
    suspend fun getMarketIndices(): Map<String, Double> = withContext(Dispatchers.IO) {
        // First try to fetch fresh data
        val indices = fetchMarketIndices()
        
        // Convert MarketIndexData to simple Double values
        return@withContext indices.mapValues { (_, data) ->
            // Parse the value string to Double, removing any commas
            try {
                data.value.replace(",", "").toDouble()
            } catch (e: Exception) {
                // Default to 0.0 if parsing fails
                0.0
            }
        }
    }
    
    /**
     * Fetch the latest market index data
     * @param forceRefresh If true, ignore cache and fetch fresh data
     * @return Map of market data by index symbol
     */
    suspend fun fetchMarketIndices(forceRefresh: Boolean = false): Map<String, MarketIndexData> = withContext(Dispatchers.IO) {
        // Check if we can return cached data
        val currentTime = System.currentTimeMillis()
        if (!forceRefresh && _marketIndices.value.isNotEmpty() && (currentTime - lastFetchTime) < CACHE_DURATION) {
            return@withContext _marketIndices.value
        }
        
        val indices = mutableMapOf<String, MarketIndexData>()
        
        // Fetch data for each index
        val niftyData = fetchIndexData(NIFTY_SYMBOL, "NIFTY 50")
        val sensexData = fetchIndexData(SENSEX_SYMBOL, "SENSEX")
        val bankNiftyData = fetchIndexData(BANKNIFTY_SYMBOL, "BANK NIFTY")
        
        // Add to result map if not null
        if (niftyData != null) indices[NIFTY_SYMBOL] = niftyData
        if (sensexData != null) indices[SENSEX_SYMBOL] = sensexData
        if (bankNiftyData != null) indices[BANKNIFTY_SYMBOL] = bankNiftyData
        
        // Update state flow if we have new data
        if (indices.isNotEmpty()) {
            _marketIndices.value = indices
            lastFetchTime = currentTime
        }
        
        return@withContext indices
    }
    
    /**
     * Fetch data for a specific market index
     * @param symbol The Yahoo Finance symbol for the index
     * @param displayName The user-friendly name to display
     * @return Formatted market data or null if fetch failed
     */
    private suspend fun fetchIndexData(
        symbol: String, 
        displayName: String
    ): MarketIndexData? = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            
            val request = Request.Builder()
                .url("https://yahoo-finance15.p.rapidapi.com/api/v1/markets/quote?symbol=$symbol")
                .get()
                .addHeader("x-rapidapi-host", YAHOO_API_HOST)
                .addHeader("x-rapidapi-key", YAHOO_API_KEY)
                .build()
                
            val response = client.newCall(request).execute()
            val jsonData = response.body?.string()
            
            if (jsonData != null) {
                // Log the full response for debugging
                println("DEBUG: Response for $symbol: $jsonData")
                
                val jsonObject = JSONObject(jsonData)
                
                if (jsonObject.has("body") && !jsonObject.isNull("body")) {
                    val body = jsonObject.getJSONObject("body")
                    
                    // Extract the relevant data
                    val regularMarketPrice = body.getDouble("regularMarketPrice")
                    val regularMarketChange = body.getDouble("regularMarketChange")
                    val regularMarketChangePercent = body.getDouble("regularMarketChangePercent")
                    
                    // Format values
                    val formatter = NumberFormat.getNumberInstance(Locale.US)
                    formatter.maximumFractionDigits = 2
                    
                    val formattedPrice = formatter.format(regularMarketPrice)
                    val formattedChange = (if (regularMarketChange >= 0) "+" else "") + 
                                        formatter.format(regularMarketChange)
                    val formattedChangePercent = (if (regularMarketChangePercent >= 0) "+" else "") +
                                               formatter.format(regularMarketChangePercent) + "%"
                    
                    return@withContext MarketIndexData(
                        name = displayName,
                        value = formattedPrice,
                        change = formattedChangePercent,
                        isPositive = regularMarketChangePercent >= 0
                    )
                }
            }
            
            return@withContext null
        } catch (e: Exception) {
            println("Error fetching market data for $symbol: ${e.message}")
            e.printStackTrace() // Print stack trace for more detailed error info
            return@withContext null
        }
    }
    
    /**
     * Get market data from cache or default values
     * @return Map of market data by index symbol
     */
    fun getCachedOrDefaultIndices(): Map<String, MarketIndexData> {
        if (_marketIndices.value.isNotEmpty()) {
            return _marketIndices.value
        }
        
        // Return default values if no data is available
        return mapOf(
            NIFTY_SYMBOL to MarketIndexData(
                name = "NIFTY 50",
                value = "22,475.22",
                change = "+0.62%",
                isPositive = true
            ),
            SENSEX_SYMBOL to MarketIndexData(
                name = "SENSEX",
                value = "73,906.15",
                change = "+0.58%",
                isPositive = true
            ),
            BANKNIFTY_SYMBOL to MarketIndexData(
                name = "BANK NIFTY",
                value = "48,285.45",
                change = "-0.12%",
                isPositive = false
            )
        )
    }
}

/**
 * Data class to hold market index information
 */
data class MarketIndexData(
    val name: String,
    val value: String,
    val change: String,
    val isPositive: Boolean
) 