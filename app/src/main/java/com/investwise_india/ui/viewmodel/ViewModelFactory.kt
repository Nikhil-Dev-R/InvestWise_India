package com.investwise_india.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.investwise_india.network.MutualFundApiService

/**
 * Factory for creating ViewModels with dependencies
 */
class ViewModelFactory(
    private val apiService: MutualFundApiService
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CompareViewModel::class.java) -> {
                CompareViewModel(apiService) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(apiService) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(apiService) as T
            }
            modelClass.isAssignableFrom(MutualFundViewModel::class.java) -> {
                MutualFundViewModel(apiService) as T
            }
            modelClass.isAssignableFrom(AccountViewModel::class.java) -> {
                AccountViewModel() as T
            }
            // Add other ViewModels here as needed
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 