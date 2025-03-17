package com.investwise_india

import android.app.Application
import androidx.work.Configuration
import com.investwise_india.data.MutualFundDataWorker

/**
 * Application class for InvestWise India
 */
class InvestWiseApplication : Application(), Configuration.Provider {
    
    override fun onCreate() {
        super.onCreate()
        
        // Schedule background data loading
        MutualFundDataWorker.scheduleOneTimeWork(this)
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
} 