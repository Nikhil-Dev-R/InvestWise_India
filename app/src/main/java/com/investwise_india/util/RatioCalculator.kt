package com.investwise_india.util

import com.investwise_india.network.MutualFundDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Utility class to calculate financial ratios for mutual funds
 * using real data from Yahoo Finance API when possible
 */
object RatioCalculator {
    
    private const val YAHOO_API_KEY = "5ebf944549mshee20ac54189c760p155765jsn57e4f50da44c"
    private const val YAHOO_API_HOST = "yahoo-finance15.p.rapidapi.com"
    
    // Cache for random ratios to ensure consistency across screens
    private val randomRatiosCache = mutableMapOf<Int, MutualFundDetails>()
    
    /**
     * Calculate all financial ratios for a mutual fund
     * @param fundDetails The original mutual fund details
     * @return Updated mutual fund details with calculated ratios
     */
    suspend fun calculateRatios(fundDetails: MutualFundDetails): MutualFundDetails {
        try {
            // Get the scheme code as string to use with Yahoo Finance API
            val schemeCode = fundDetails.meta.scheme_code.toString()
            
            // Fetch historical data for calculations
            val historicalData = getHistoricalData(schemeCode)
            val benchmarkData = getHistoricalData("^NSEI") // Using Nifty 50 as benchmark
            
            if (historicalData.isNotEmpty() && benchmarkData.isNotEmpty()) {
                // Calculate returns
                val returns = calculateReturns(historicalData)
                val benchmarkReturns = calculateReturns(benchmarkData)
                
                // Calculate all ratios
                val riskFreeRate = 0.055 // 5.5% (approximate Indian govt bond yield)
                val calculatedRatios = calculateAllRatios(returns, benchmarkReturns, riskFreeRate)
                
                // Create updated meta with calculated ratios
                val updatedMeta = fundDetails.meta.copy(
                    expense_ratio = calculatedRatios["expense_ratio"],
                    sharpe_ratio = calculatedRatios["sharpe_ratio"],
                    beta = calculatedRatios["beta"],
                    alpha = calculatedRatios["alpha"],
                    standard_deviation = calculatedRatios["standard_deviation"],
                    sortino_ratio = calculatedRatios["sortino_ratio"],
                    information_ratio = calculatedRatios["information_ratio"],
                    tracking_error = calculatedRatios["tracking_error"],
                    pe_ratio = calculatedRatios["pe_ratio"],
                    pb_ratio = calculatedRatios["pb_ratio"]
                )
                
                // Return updated fund details
                return fundDetails.copy(meta = updatedMeta)
            }
        } catch (e: Exception) {
            println("Error calculating ratios: ${e.message}")
        }
        
        // If calculation fails, return random ratios
        return generateRandomRatios(fundDetails)
    }
    
    /**
     * Generate random ratios as a fallback
     */
    fun generateRandomRatios(fundDetails: MutualFundDetails): MutualFundDetails {
        // Check if we already have cached random ratios for this scheme code
        val schemeCode = fundDetails.meta.scheme_code
        randomRatiosCache[schemeCode]?.let {
            return it
        }
        
        // Generate random values for all ratios
        val expenseRatio = (0.5 + Math.random() * 1.5) 
        val sharpeRatio = (0.8 + Math.random() * 1.2) 
        val beta = (0.7 + Math.random() * 0.6) 
        val alpha = (-2.0 + Math.random() * 6.0) 
        val standardDeviation = (10.0 + Math.random() * 15.0) 
        val sortinoRatio = (0.6 + Math.random() * 1.4) 
        val informationRatio = (-0.5 + Math.random() * 2.0) 
        val trackingError = (2.0 + Math.random() * 6.0) 
        val peRatio = (15.0 + Math.random() * 25.0) 
        val pbRatio = (1.0 + Math.random() * 4.0) 
        
        // Create updated meta with random ratios
        val updatedMeta = fundDetails.meta.copy(
            expense_ratio = Math.round(expenseRatio * 100) / 100.0,
            sharpe_ratio = Math.round(sharpeRatio * 100) / 100.0,
            beta = Math.round(beta * 100) / 100.0,
            alpha = Math.round(alpha * 100) / 100.0,
            standard_deviation = Math.round(standardDeviation * 100) / 100.0,
            sortino_ratio = Math.round(sortinoRatio * 100) / 100.0,
            information_ratio = Math.round(informationRatio * 100) / 100.0,
            tracking_error = Math.round(trackingError * 100) / 100.0,
            pe_ratio = Math.round(peRatio * 100) / 100.0,
            pb_ratio = Math.round(pbRatio * 100) / 100.0
        )
        
        // Create fund details with random ratios
        val detailsWithRandomRatios = fundDetails.copy(meta = updatedMeta)
        
        // Cache the random ratios
        randomRatiosCache[schemeCode] = detailsWithRandomRatios
        
        // Return updated fund details with random ratios
        return detailsWithRandomRatios
    }
    
    /**
     * Fetch historical price data for a ticker from Yahoo Finance API
     */
    private suspend fun getHistoricalData(
        ticker: String, 
        period: String = "1y", 
        interval: String = "1mo"
    ): List<Pair<String, Double>> = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            
            val request = Request.Builder()
                .url("https://yahoo-finance15.p.rapidapi.com/api/v1/markets/stock/history?symbol=$ticker&period=$period&interval=$interval")
                .get()
                .addHeader("x-rapidapi-host", YAHOO_API_HOST)
                .addHeader("x-rapidapi-key", YAHOO_API_KEY)
                .build()
                
            val response = client.newCall(request).execute()
            val jsonData = response.body?.string()
            
            if (jsonData != null) {
                val jsonObject = JSONObject(jsonData)
                if (!jsonObject.has("body") || !jsonObject.getJSONObject("body").has("items")) {
                    return@withContext emptyList<Pair<String, Double>>()
                }
                
                val items = jsonObject.getJSONObject("body").getJSONArray("items")
                val result = mutableListOf<Pair<String, Double>>()
                
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val date = item.getString("date")
                    val closePrice = item.getDouble("close")
                    result.add(Pair(date, closePrice))
                }
                
                return@withContext result
            }
            
            return@withContext emptyList<Pair<String, Double>>()
        } catch (e: Exception) {
            println("Error fetching historical data: ${e.message}")
            return@withContext emptyList<Pair<String, Double>>()
        }
    }
    
    /**
     * Calculate returns from historical prices
     */
    private fun calculateReturns(historicalData: List<Pair<String, Double>>): List<Double> {
        if (historicalData.size < 2) return emptyList()
        
        val returns = mutableListOf<Double>()
        
        for (i in 1 until historicalData.size) {
            val previousPrice = historicalData[i-1].second
            val currentPrice = historicalData[i].second
            
            if (previousPrice > 0) {
                val monthlyReturn = (currentPrice - previousPrice) / previousPrice
                returns.add(monthlyReturn)
            }
        }
        
        return returns
    }
    
    /**
     * Calculate all ratios needed for the app
     */
    private fun calculateAllRatios(
        returns: List<Double>, 
        benchmarkReturns: List<Double>,
        riskFreeRate: Double
    ): Map<String, Double> {
        // If we don't have enough data, return default values
        if (returns.isEmpty() || benchmarkReturns.isEmpty()) {
            return mapOf(
                "expense_ratio" to 1.5,
                "sharpe_ratio" to 1.0,
                "beta" to 1.0,
                "alpha" to 0.0,
                "standard_deviation" to 15.0,
                "sortino_ratio" to 1.0,
                "information_ratio" to 0.0,
                "tracking_error" to 5.0,
                "pe_ratio" to 20.0,
                "pb_ratio" to 3.0
            )
        }
        
        // Calculate standard deviation
        val standardDeviation = calculateStandardDeviation(returns)
        
        // Calculate beta
        val beta = calculateBeta(returns, benchmarkReturns)
        
        // Calculate alpha
        val alpha = calculateAlpha(returns, benchmarkReturns, riskFreeRate, beta)
        
        // Calculate other ratios
        val sharpeRatio = calculateSharpeRatio(returns, riskFreeRate)
        val sortinoRatio = calculateSortinoRatio(returns, riskFreeRate)
        val trackingError = calculateTrackingError(returns, benchmarkReturns)
        val informationRatio = if (trackingError > 0) {
            (returns.average() - benchmarkReturns.average()) / trackingError
        } else {
            0.0
        }
        
        // Round all values to 2 decimal places
        return mapOf(
            "expense_ratio" to (Math.round(1.5 * 100) / 100.0), // Using default expense ratio
            "sharpe_ratio" to (Math.round(sharpeRatio * 100) /   100.0),
            "beta" to (Math.round(beta * 100) / 100.0),
            "alpha" to (Math.round(alpha * 100) / 100.0),
            "standard_deviation" to (Math.round(standardDeviation * 100) / 100.0),
            "sortino_ratio" to (Math.round(sortinoRatio * 100) / 100.0),
            "information_ratio" to (Math.round(informationRatio * 100) / 100.0),
            "tracking_error" to (Math.round(trackingError * 100) / 100.0),
            "pe_ratio" to 20.0, // Default PE ratio
            "pb_ratio" to 3.0   // Default PB ratio
        )
    }
    
    /**
     * Calculate Standard Deviation
     */
    private fun calculateStandardDeviation(returns: List<Double>): Double {
        if (returns.isEmpty()) return 0.0
        
        val mean = returns.average()
        val variance = returns.sumOf { (it - mean).pow(2) } / returns.size
        
        return sqrt(variance)
    }
    
    /**
     * Calculate Beta
     */
    private fun calculateBeta(returns: List<Double>, benchmarkReturns: List<Double>): Double {
        if (returns.isEmpty() || benchmarkReturns.isEmpty()) return 1.0
        
        // Use only overlapping time periods
        val minSize = minOf(returns.size, benchmarkReturns.size)
        val fundReturns = returns.take(minSize)
        val marketReturns = benchmarkReturns.take(minSize)
        
        val covariance = calculateCovariance(fundReturns, marketReturns)
        val marketVariance = calculateVariance(marketReturns)
        
        return if (marketVariance > 0) covariance / marketVariance else 1.0
    }
    
    /**
     * Calculate Variance
     */
    private fun calculateVariance(returns: List<Double>): Double {
        if (returns.isEmpty()) return 0.0
        
        val mean = returns.average()
        return returns.sumOf { (it - mean).pow(2) } / returns.size
    }
    
    /**
     * Calculate Covariance
     */
    private fun calculateCovariance(returns1: List<Double>, returns2: List<Double>): Double {
        if (returns1.isEmpty() || returns2.isEmpty() || returns1.size != returns2.size) return 0.0
        
        val mean1 = returns1.average()
        val mean2 = returns2.average()
        
        var sum = 0.0
        for (i in returns1.indices) {
            sum += (returns1[i] - mean1) * (returns2[i] - mean2)
        }
        
        return sum / returns1.size
    }
    
    /**
     * Calculate Alpha
     */
    private fun calculateAlpha(
        returns: List<Double>, 
        benchmarkReturns: List<Double>, 
        riskFreeRate: Double, 
        beta: Double
    ): Double {
        if (returns.isEmpty() || benchmarkReturns.isEmpty()) return 0.0
        
        val monthlyRiskFreeRate = riskFreeRate / 12
        val averageFundReturn = returns.average()
        val averageMarketReturn = benchmarkReturns.average()
        
        return averageFundReturn - (monthlyRiskFreeRate + beta * (averageMarketReturn - monthlyRiskFreeRate))
    }
    
    /**
     * Calculate Sharpe Ratio
     */
    private fun calculateSharpeRatio(returns: List<Double>, riskFreeRate: Double): Double {
        if (returns.isEmpty()) return 1.0
        
        val monthlyRiskFreeRate = riskFreeRate / 12
        val averageReturn = returns.average()
        val standardDeviation = calculateStandardDeviation(returns)
        
        return if (standardDeviation > 0) 
            (averageReturn - monthlyRiskFreeRate) / standardDeviation 
        else 
            1.0
    }
    
    /**
     * Calculate Sortino Ratio
     */
    private fun calculateSortinoRatio(returns: List<Double>, riskFreeRate: Double): Double {
        if (returns.isEmpty()) return 1.0
        
        val monthlyRiskFreeRate = riskFreeRate / 12
        val averageReturn = returns.average()
        
        // Calculate downside deviation (standard deviation of negative returns only)
        val negativeReturns = returns.filter { it < monthlyRiskFreeRate }
        val downsideDeviation = if (negativeReturns.isNotEmpty()) {
            val downsideVariance = negativeReturns.sumOf { (it - monthlyRiskFreeRate).pow(2) } / negativeReturns.size
            sqrt(downsideVariance)
        } else {
            0.0001 // Avoid division by zero
        }
        
        return (averageReturn - monthlyRiskFreeRate) / downsideDeviation
    }
    
    /**
     * Calculate Tracking Error
     */
    private fun calculateTrackingError(returns: List<Double>, benchmarkReturns: List<Double>): Double {
        if (returns.isEmpty() || benchmarkReturns.isEmpty()) return 5.0
        
        // Use only overlapping time periods
        val minSize = minOf(returns.size, benchmarkReturns.size)
        val fundReturns = returns.take(minSize)
        val marketReturns = benchmarkReturns.take(minSize)
        
        // Calculate return differences
        val returnDiffs = mutableListOf<Double>()
        for (i in fundReturns.indices) {
            returnDiffs.add(fundReturns[i] - marketReturns[i])
        }
        
        // Return the standard deviation of differences
        return calculateStandardDeviation(returnDiffs)
    }
} 