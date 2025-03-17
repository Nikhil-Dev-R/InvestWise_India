package com.investwise_india.data

import android.content.Context
import android.util.Log
import androidx.work.*
import com.investwise_india.data.repository.MutualFundRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Worker class to load mutual fund data in the background
 */
class MutualFundDataWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val TAG = "MutualFundDataWorker"
        const val WORK_NAME = "mutual_fund_data_loader"
        
        /**
         * Schedule the worker to run once when the app starts
         */
        fun scheduleOneTimeWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
                
            val workRequest = OneTimeWorkRequestBuilder<MutualFundDataWorker>()
                .setConstraints(constraints)
                .build()
                
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.KEEP,
                    workRequest
                )
                
            Log.d(TAG, "Scheduled one-time work for mutual fund data loading")
        }
        
        /**
         * Schedule the worker to run periodically
         */
        fun schedulePeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
                
            val workRequest = PeriodicWorkRequestBuilder<MutualFundDataWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()
                
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
                
            Log.d(TAG, "Scheduled periodic work for mutual fund data loading")
        }
    }
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting mutual fund data loading work")
        
        try {
            val repository = DataModule.mutualFundRepository
            val success = repository.loadAllMutualFunds()
            
            return@withContext if (success) {
                Log.d(TAG, "Successfully loaded mutual fund data")
                Result.success()
            } else {
                Log.e(TAG, "Failed to load mutual fund data")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading mutual fund data", e)
            return@withContext Result.failure()
        }
    }
} 