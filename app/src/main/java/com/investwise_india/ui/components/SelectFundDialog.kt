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

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SelectFundDialog(
    apiService: MutualFundApiService,
    onDismiss: () -> Unit,
    onFundSelected: (MutualFund) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var funds by remember { mutableStateOf<List<MutualFund>>(emptyList()) }
    var filteredFunds by remember { mutableStateOf<List<MutualFund>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Create a StateFlow for search query to debounce user input
    val searchFlow = remember { MutableStateFlow("") }

    // Load funds on initialization
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllMutualFunds()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        funds = response.body()!!
                        filteredFunds = funds
                    } else {
                        error = "Failed to load mutual funds"
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = e.message ?: "An error occurred"
                    isLoading = false
                }
            }
        }
    }

    // Handle search query changes on background thread
    LaunchedEffect(searchQuery) {
        searchFlow.value = searchQuery
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