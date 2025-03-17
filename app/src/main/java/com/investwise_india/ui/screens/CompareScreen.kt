package com.investwise_india.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investwise_india.model.MutualFund
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.network.MutualFundDetails
import com.investwise_india.network.MutualFundMeta
import com.investwise_india.network.MutualFundData
import androidx.compose.ui.tooling.preview.Preview
import retrofit2.Response
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.CoroutineScope
import com.investwise_india.ui.components.SelectFundDialog
import com.investwise_india.ui.viewmodel.CompareViewModel
import com.investwise_india.ui.viewmodel.ViewModelFactory
import com.investwise_india.data.DataModule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    apiService: MutualFundApiService,
    onBackPressed: () -> Unit,
    onAddFund: () -> Unit,
    selectedFunds: List<MutualFund> = emptyList(),
    onRemoveFund: (MutualFund) -> Unit = {}
) {
    // Create a coroutine scope that follows the Compose lifecycle
    val coroutineScope = rememberCoroutineScope()
    
    // Create the ViewModel using the factory with a fixed key to ensure it's retained
    val viewModel: CompareViewModel = viewModel(
        factory = ViewModelFactory(apiService),
        key = "CompareViewModel" // Fixed key to ensure the ViewModel is retained
    )
    
    // Collect state from the ViewModel
    val funds by viewModel.selectedFunds.collectAsState()
    val fundDetails by viewModel.fundDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val showSelectFundDialog by viewModel.showSelectFundDialog.collectAsState()
    
    // Initialize the ViewModel with the selected funds if provided
    // Use DisposableEffect to handle lifecycle properly
    DisposableEffect(Unit) {
        if (selectedFunds.isNotEmpty() && funds.isEmpty()) {
            coroutineScope.launch {
                selectedFunds.forEach { viewModel.addFund(it) }
            }
        }
        onDispose { }
    }
    
    // Ensure mutual fund data is loaded
    val repository = DataModule.mutualFundRepository
    val dataLoaded by repository.dataLoaded.collectAsState(initial = false)
    
    LaunchedEffect(Unit) {
        if (!dataLoaded) {
            repository.loadAllMutualFunds()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Compare Mutual Funds",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showSelectFundDialog() },
                        enabled = funds.size < 3
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add fund to compare"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                funds.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No funds selected for comparison",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.showSelectFundDialog() }) {
                            Text("Add Fund")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(funds) { fund ->
                            val details = fundDetails[fund.schemeCode]
                            ComparisonCard(
                                fund = fund,
                                details = details,
                                onRemove = { 
                                    viewModel.removeFund(fund)
                                    onRemoveFund(fund) // Also notify parent
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Show the select fund dialog if needed
    if (showSelectFundDialog) {
        SelectFundDialog(
            apiService = apiService,
            onDismiss = { viewModel.hideSelectFundDialog() },
            onFundSelected = { fund ->
                viewModel.addFund(fund)
                viewModel.hideSelectFundDialog() // Explicitly hide dialog after selection
                onAddFund() // Notify parent
            }
        )
    }
}

@Composable
private fun ComparisonCard(
    fund: MutualFund,
    details: MutualFundDetails?,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fund.schemeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove fund"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Scheme Code: ${fund.schemeCode}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (details != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fund House: ${details.meta.fund_house}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Category: ${details.meta.scheme_category}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Type: ${details.meta.scheme_type}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (details.data.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Latest NAV: ₹${details.data.first().nav}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "As of: ${details.data.first().date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Display fund ratios
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Fund Ratios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // First row of ratios
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RatioItem(
                        label = "Expense Ratio",
                        value = details.meta.expense_ratio?.let { "${it}%" } ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                    RatioItem(
                        label = "Sharpe Ratio",
                        value = details.meta.sharpe_ratio?.toString() ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Second row of ratios
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RatioItem(
                        label = "Alpha",
                        value = details.meta.alpha?.toString() ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                    RatioItem(
                        label = "Beta",
                        value = details.meta.beta?.toString() ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Third row of ratios
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    RatioItem(
                        label = "P/E Ratio",
                        value = details.meta.pe_ratio?.toString() ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                    RatioItem(
                        label = "P/B Ratio",
                        value = details.meta.pb_ratio?.toString() ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterHorizontally),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

/**
 * Composable to display a single ratio item with label and value
 */
@Composable
private fun RatioItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCompareScreen() {
    MaterialTheme {
        CompareScreen(
            apiService = object : MutualFundApiService {
                override suspend fun getAllMutualFunds(): Response<List<MutualFund>> {
                    return Response.success(emptyList())
                }
                
                override suspend fun getMutualFundDetails(schemeCode: Int): Response<MutualFundDetails> {
                    return Response.success(
                        MutualFundDetails(
                            meta = MutualFundMeta(
                                fund_house = "Sample Fund House",
                                scheme_type = "Open Ended",
                                scheme_category = "Equity",
                                scheme_code = schemeCode,
                                scheme_name = "Sample Fund"
                            ),
                            data = listOf(
                                MutualFundData(
                                    date = "10-03-2024",
                                    nav = "45.6789"
                                )
                            )
                        )
                    )
                }
            },
            onBackPressed = {},
            onAddFund = {}
        )
    }
} 