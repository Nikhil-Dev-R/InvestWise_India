package com.investwise_india.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.investwise_india.model.MutualFund
import com.investwise_india.network.MutualFundApiService
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.FlowPreview
import com.investwise_india.data.DataModule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Job

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SelectFundDialog(
    apiService: MutualFundApiService,
    onDismiss: () -> Unit,
    onFundSelected: (MutualFund) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filteredFunds by remember { mutableStateOf<List<MutualFund>>(emptyList()) }
    
    // Create a coroutine scope that follows the Compose lifecycle
    val coroutineScope = rememberCoroutineScope()
    
    // Get repository instance
    val repository = DataModule.mutualFundRepository
    
    // Collect states from repository
    val funds by repository.allMutualFunds.collectAsState(initial = emptyList())
    val isLoading by repository.isLoading.collectAsState(initial = true)
    val error by repository.error.collectAsState(initial = null)
    val dataLoaded by repository.dataLoaded.collectAsState(initial = false)
    
    // Create a StateFlow for search query to debounce user input
    val searchFlow = remember { MutableStateFlow("") }

    // Load funds if not already loaded
    DisposableEffect(Unit) {
        var job: Job? = null
        if (!dataLoaded && funds.isEmpty()) {
            job = coroutineScope.launch {
                repository.loadAllMutualFunds()
            }
        }
        onDispose {
            job?.cancel()
        }
    }

    // Initialize filtered funds when funds are loaded
    LaunchedEffect(funds) {
        filteredFunds = funds
    }

    // Handle search query changes on background thread
    DisposableEffect(searchQuery) {
        searchFlow.value = searchQuery
        val job = coroutineScope.launch {
            searchFlow
                .debounce(300) // Add debounce to prevent excessive filtering
                .distinctUntilChanged()
                .collect { query ->
                    withContext(Dispatchers.Default) {
                        val filtered = if (query.isEmpty()) {
                            funds
                        } else {
                            funds.filter {
                                it.schemeName.contains(query, ignoreCase = true)
                            }
                        }
                        withContext(Dispatchers.Main) {
                            filteredFunds = filtered
                        }
                    }
                }
        }
        onDispose {
            job.cancel()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Mutual Fund") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search funds") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(androidx.compose.ui.Alignment.CenterHorizontally)
                        )
                    }
                    error != null -> {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    else -> {
                        LazyColumn {
                            items(filteredFunds) { fund ->
                                ListItem(
                                    headlineContent = { Text(fund.schemeName) },
                                    supportingContent = { Text("Scheme Code: ${fund.schemeCode}") },
                                    modifier = Modifier.clickable { onFundSelected(fund) }
                                )
                                Divider()
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 