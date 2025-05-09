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
import com.investwise_india.util.RatioCalculator
import com.investwise_india.data.repository.MutualFundRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.remember
import com.investwise_india.model.DebtFundSubcategories
import com.investwise_india.model.DebtFundSubcategory

// The MutualFundRepository is used for consistent ratio calculations
private lateinit var mutualFundRepository: MutualFundRepository

@Composable
fun MutualFundListScreen(
    category: MutualFundCategory,
    apiService: MutualFundApiService,
    onBackPressed: () -> Unit,
    onSubcategorySelected: ((Int) -> Unit)? = null,
    selectedSubcategory: DebtFundSubcategory? = null
) {
    // Initialize the repository if it hasn't been already
    if (!::mutualFundRepository.isInitialized) {
        mutualFundRepository = MutualFundRepository(apiService)
    }
    
    var mutualFunds by remember { mutableStateOf<List<MutualFund>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Define scheme codes for each category
    val categorySchemes = remember(category, selectedSubcategory) {
        when (category.id) {
            5 -> { // Debt Fund
                selectedSubcategory?.schemeCodes ?: emptyList()
            }
            1 -> listOf(118632, 120586, 119598, 119250) // Large Cap
            2 -> listOf(127042, 118989, 118668, 125307, 119775, 120841) // Mid Cap
            3 -> listOf(147946, 118778, 120828, 148618, 125354, 130503) // Small Cap
            4 -> listOf(120484, 120251, 118624, 120674, 143537, 120819, 119019) // Hybrid
            6 -> listOf(120847, 120270, 119723, 133386, 119242) // ELSS
            7 -> listOf(120843, 122639, 118955, 129046, 120492) // Flexicap
            8 -> listOf(120700, 148747, 152064) // Thematic
            9 -> listOf(147622, 148555, 148807, 148726, 149892, 149389) // Index
            10 -> listOf(118545, 118849, 120698, 125504, 118582) // International
            else -> emptyList()
        }
    }
    
    LaunchedEffect(category, selectedSubcategory) {
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
                    text = if (category.id == 5 && selectedSubcategory != null) {
                        selectedSubcategory.name
                    } else {
                        category.name
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Empty box to balance the layout
                Box(modifier = Modifier.size(48.dp))
            }

            // Show subcategories for Debt Fund
            if (category.id == 5 && selectedSubcategory == null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(DebtFundSubcategories.subcategories) { subcategory ->
                        SubcategoryCard(
                            subcategory = subcategory,
                            onClick = { onSubcategorySelected?.invoke(subcategory.id) }
                        )
                    }
                }
            } else {
                // Display loading, error, or content
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    error != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error!!,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
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
}

@Composable
private fun SubcategoryCard(
    subcategory: DebtFundSubcategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = subcategory.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = subcategory.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
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
    
    // Create a coroutine scope that follows the Compose lifecycle
    val coroutineScope = rememberCoroutineScope()
    
    // Initialize the repository if it hasn't been already
    if (!::mutualFundRepository.isInitialized) {
        mutualFundRepository = MutualFundRepository(apiService)
    }
    
    LaunchedEffect(showDetails) {
        if (showDetails && fundDetails == null && !isLoadingDetails) {
            isLoadingDetails = true
            detailsError = null
            try {
                // Use the repository to get fund details with consistent ratios
                val details = mutualFundRepository.getMutualFundDetails(fund.schemeCode)
                if (details != null) {
                    fundDetails = details
                    
                    // Set up a check to see if real ratios are calculated after a delay
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(3000)
                        
                        // Check for updated ratios
                        val updatedDetails = mutualFundRepository.getLatestFundDetails(fund.schemeCode)
                        if (updatedDetails != null) {
                            withContext(Dispatchers.Main) {
                                fundDetails = updatedDetails
                            }
                        }
                    }
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
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        InfoColumn(
                            label = "Latest NAV",
                            value = "₹${fundDetails?.data?.firstOrNull()?.nav ?: "N/A"}"
                        )
                        
                        InfoColumn(
                            label = "As of",
                            value = fundDetails?.data?.firstOrNull()?.date ?: "N/A"
                        )
                    }
                    
                    // Display fund ratios section
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Fund Ratios",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // First row of ratios
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "Expense Ratio",
                            value = fundDetails?.meta?.expense_ratio?.let { "${it}%" } ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        
                        InfoColumn(
                            label = "Sharpe Ratio",
                            value = fundDetails?.meta?.sharpe_ratio?.toString() ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Second row of ratios
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "Alpha",
                            value = fundDetails?.meta?.alpha?.toString() ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        
                        InfoColumn(
                            label = "Beta",
                            value = fundDetails?.meta?.beta?.toString() ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Third row of ratios
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "P/E Ratio",
                            value = fundDetails?.meta?.pe_ratio?.toString() ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        
                        InfoColumn(
                            label = "P/B Ratio",
                            value = fundDetails?.meta?.pb_ratio?.toString() ?: "N/A",
                            modifier = Modifier.weight(1f)
                        )
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
            onBackPressed = {},
            onSubcategorySelected = null,
            selectedSubcategory = null
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