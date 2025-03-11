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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    apiService: MutualFundApiService,
    onBackPressed: () -> Unit,
    onAddFund: () -> Unit,
    selectedFunds: List<MutualFund> = emptyList(),
    onRemoveFund: (MutualFund) -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var fundDetails by remember { mutableStateOf<Map<Int, MutualFundDetails>>(emptyMap()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedFunds) {
        if (selectedFunds.isNotEmpty()) {
            isLoading = true
            error = null
            fundDetails = emptyMap() // Reset details when funds change
            
            withContext(Dispatchers.IO) {
                try {
                    // Create a map to store the results
                    val newDetails = mutableMapOf<Int, MutualFundDetails>()
                    
                    // Launch parallel requests for each fund
                    val deferredResults = selectedFunds.map { fund ->
                        async {
                            val response = apiService.getMutualFundDetails(fund.schemeCode)
                            if (response.isSuccessful && response.body() != null) {
                                fund.schemeCode to response.body()!!
                            } else {
                                null
                            }
                        }
                    }
                    
                    // Wait for all requests to complete
                    deferredResults.forEach { deferred ->
                        deferred.await()?.let { (schemeCode, details) ->
                            newDetails[schemeCode] = details
                        }
                    }
                    
                    // Update the UI on the main thread
                    withContext(Dispatchers.Main) {
                        fundDetails = newDetails
                        isLoading = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        error = e.message ?: "Failed to load fund details"
                        isLoading = false
                    }
                }
            }
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
                        onClick = onAddFund,
                        enabled = selectedFunds.size < 3
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
                selectedFunds.isEmpty() -> {
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
                        Button(onClick = onAddFund) {
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
                        items(selectedFunds) { fund ->
                            val details = fundDetails[fund.schemeCode]
                            ComparisonCard(
                                fund = fund,
                                details = details,
                                onRemove = { onRemoveFund(fund) }
                            )
                        }
                    }
                }
            }
        }
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