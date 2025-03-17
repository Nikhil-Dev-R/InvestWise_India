package com.investwise_india.data

import com.investwise_india.data.repository.MarketDataRepository
import com.investwise_india.data.repository.MutualFundRepository
import com.investwise_india.di.NetworkModule

/**
 * Singleton to provide data repositories throughout the app
 */
object DataModule {
    // Lazy initialization of the repositories
    val mutualFundRepository: MutualFundRepository by lazy {
        MutualFundRepository(NetworkModule.mutualFundApiService)
    }
    
    // Market data repository for stock indices 
    val marketDataRepository: MarketDataRepository by lazy {
        MarketDataRepository()
    }
} 