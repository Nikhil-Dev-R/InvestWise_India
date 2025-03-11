package com.investwise_india.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.investwise_india.model.MutualFund
import com.investwise_india.model.MutualFundCategory
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.network.MutualFundDetails
import com.investwise_india.network.MutualFundMeta
import com.investwise_india.network.MutualFundData
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.clickable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import retrofit2.Response

@Composable
fun MutualFundListScreen(
    category: MutualFundCategory,
    apiService: MutualFundApiService,
    onBackPressed: () -> Unit
) {
    var mutualFunds by remember { mutableStateOf<List<MutualFund>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Define scheme codes for each category
    val categorySchemes = remember(category) {
        when (category.id) {
            1 -> listOf(100171, 119598, 120465, 125497, 118533) // Large Cap
            2 -> listOf(118834, 118566, 120822, 125494, 118528) // Mid Cap
            3 -> listOf(118701, 120684, 125496, 118532, 118568) // Small Cap
            4 -> listOf(118533, 118837, 120686, 125498, 118570) // Hybrid
            5 -> listOf(118535, 118839, 120688, 125499, 118572) // Debt
            6 -> listOf(118537, 118841, 120690, 125500, 118574) // ELSS
            7 -> listOf(118539, 118843, 120692, 125501, 118576) // Flexicap
            8 -> listOf(118541, 118845, 120694, 125502, 118578) // Thematic
            9 -> listOf(118543, 118847, 120696, 125503, 118580) // Index
            10 -> listOf(118545, 118849, 120698, 125504, 118582) // International
            else -> emptyList()
        }
    }
    
    LaunchedEffect(category) {
        isLoading = true
        error = null
        mutualFunds = emptyList()
        
        try {
            val fundsList = mutableListOf<MutualFund>()
            
            // Fetch details for each scheme code
            categorySchemes.forEach { schemeCode ->
                try {
                    val response = apiService.getMutualFundDetails(schemeCode)
                    if (response.isSuccessful && response.body() != null) {
                        val details = response.body()!!
                        fundsList.add(
                            MutualFund(
                                schemeCode = details.meta.scheme_code,
                                schemeName = details.meta.scheme_name,
                                isinGrowth = details.meta.isin_growth,
                                isinDivReinvestment = details.meta.isin_div_reinvestment
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Skip failed fund and continue with others
                }
            }
            
            if (fundsList.isNotEmpty()) {
                mutualFunds = fundsList
            } else {
                error = "No mutual funds found for this category"
            }
        } catch (e: Exception) {
            error = when (e) {
                is java.net.SocketTimeoutException -> "Connection timed out. Please check your internet and try again"
                is java.net.UnknownHostException -> "No internet connection. Please check your network settings"
                is retrofit2.HttpException -> "Server error (${e.code()}). Please try again later"
                else -> "An unexpected error occurred: ${e.message}"
            }
        } finally {
            isLoading = false
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Empty box to balance the layout
                Box(modifier = Modifier.size(48.dp))
            }
            
            // Display loading, error, or content
            when {
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    isLoading = true
                                    error = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> {
                    if (mutualFunds.isNotEmpty()) {
                        Text(
                            text = "Available Funds",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = mutualFunds,
                            key = { it.schemeCode }
                        ) { fund ->
                            MutualFundItem(fund = fund, apiService = apiService)
                        }
                        
                        item {
                            if (isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MutualFundItem(
    fund: MutualFund,
    apiService: MutualFundApiService
) {
    var showDetails by remember { mutableStateOf(false) }
    var fundDetails by remember { mutableStateOf<MutualFundDetails?>(null) }
    var isLoadingDetails by remember { mutableStateOf(false) }
    var detailsError by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(showDetails) {
        if (showDetails && fundDetails == null && !isLoadingDetails) {
            isLoadingDetails = true
            detailsError = null
            try {
                val response = apiService.getMutualFundDetails(fund.schemeCode)
                if (response.isSuccessful && response.body() != null) {
                    fundDetails = response.body()
                } else {
                    detailsError = "Failed to load fund details"
                }
            } catch (e: Exception) {
                detailsError = "Error loading details: ${e.message}"
            } finally {
                isLoadingDetails = false
            }
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetails = !showDetails },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Main fund information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fund.schemeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                IconButton(onClick = { showDetails = !showDetails }) {
                    Icon(
                        imageVector = if (showDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (showDetails) "Show less" else "Show more"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Basic information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn(
                    label = "Scheme Code",
                    value = fund.schemeCode.toString()
                )
                
                InfoColumn(
                    label = "ISIN (Growth)",
                    value = fund.isinGrowth ?: "N/A"
                )
            }
            
            if (showDetails) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                // Additional ISIN information
                Text(
                    text = "Additional Information",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InfoColumn(
                    label = "ISIN (Dividend Reinvestment)",
                    value = fund.isinDivReinvestment ?: "N/A"
                )
                
                // Fund house and scheme details (if available)
                if (fundDetails != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    InfoColumn(
                        label = "Fund House",
                        value = fundDetails?.meta?.fund_house ?: "N/A"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    InfoColumn(
                        label = "Scheme Type",
                        value = fundDetails?.meta?.scheme_type ?: "N/A"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    InfoColumn(
                        label = "Scheme Category",
                        value = fundDetails?.meta?.scheme_category ?: "N/A"
                    )
                    
                    // Show latest NAV if available
                    if (!fundDetails?.data.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Latest NAV Information",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val latestNav = fundDetails?.data?.firstOrNull()
                        if (latestNav != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoColumn(
                                    label = "Date",
                                    value = latestNav.date
                                )
                                
                                InfoColumn(
                                    label = "NAV",
                                    value = "₹${latestNav.nav}"
                                )
                            }
                        }
                    }
                }
                
                // Loading state
                if (isLoadingDetails) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                
                // Error state
                if (detailsError != null) {
                    Text(
                        text = detailsError ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(
    name = "Mutual Fund List Screen",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    device = "id:pixel_6_pro"
)
@Composable
fun PreviewMutualFundListScreen() {
    val mockCategory = MutualFundCategory(
        id = 1,
        name = "Large Cap",
        description = "Top 100 companies by market capitalization",
        iconResId = 0,
        colorResId = 0
    )
    
    val mockApiService = object : MutualFundApiService {
        override suspend fun getAllMutualFunds(): Response<List<MutualFund>> {
            val mockFunds = listOf(
                MutualFund(
                    schemeCode = 119598,
                    schemeName = "Axis Bluechip Fund Direct Plan Growth",
                    isinGrowth = "INF846K01EW2",
                    isinDivReinvestment = "INF846K01EX0"
                ),
                MutualFund(
                    schemeCode = 120465,
                    schemeName = "SBI Blue Chip Fund Direct Plan Growth",
                    isinGrowth = "INF200K01LN7",
                    isinDivReinvestment = "INF200K01LO5"
                )
            )
            return Response.success(mockFunds)
        }
        
        override suspend fun getMutualFundDetails(schemeCode: Int): Response<MutualFundDetails> {
            val mockDetails = MutualFundDetails(
                meta = MutualFundMeta(
                    fund_house = "Axis Mutual Fund",
                    scheme_type = "Open Ended",
                    scheme_category = "Equity",
                    scheme_code = schemeCode,
                    scheme_name = "Axis Bluechip Fund Direct Plan Growth"
                ),
                data = listOf(
                    MutualFundData(
                        date = "10-03-2024",
                        nav = "45.6789"
                    )
                )
            )
            return Response.success(mockDetails)
        }
    }
    
    MaterialTheme {
        MutualFundListScreen(
            category = mockCategory,
            apiService = mockApiService,
            onBackPressed = {}
        )
    }
}

@Preview(
    name = "Mutual Fund Item - Collapsed",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewMutualFundItemCollapsed() {
    val mockFund = MutualFund(
        schemeCode = 119598,
        schemeName = "Axis Bluechip Fund Direct Plan Growth",
        isinGrowth = "INF846K01EW2",
        isinDivReinvestment = "INF846K01EX0"
    )
    
    val mockApiService = object : MutualFundApiService {
        override suspend fun getAllMutualFunds(): Response<List<MutualFund>> {
            return Response.success(emptyList())
        }
        
        override suspend fun getMutualFundDetails(schemeCode: Int): Response<MutualFundDetails> {
            val mockDetails = MutualFundDetails(
                meta = MutualFundMeta(
                    fund_house = "Axis Mutual Fund",
                    scheme_type = "Open Ended",
                    scheme_category = "Equity",
                    scheme_code = schemeCode,
                    scheme_name = mockFund.schemeName
                ),
                data = listOf(
                    MutualFundData(
                        date = "10-03-2024",
                        nav = "45.6789"
                    )
                )
            )
            return Response.success(mockDetails)
        }
    }
    
    MaterialTheme {
        MutualFundItem(
            fund = mockFund,
            apiService = mockApiService
        )
    }
}

@Preview(
    name = "Info Column",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewInfoColumn() {
    MaterialTheme {
        InfoColumn(
            label = "Scheme Code",
            value = "119598"
        )
    }
} 