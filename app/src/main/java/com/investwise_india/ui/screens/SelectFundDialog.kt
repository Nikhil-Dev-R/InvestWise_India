package com.investwise_india.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.investwise_india.model.MutualFund
import com.investwise_india.network.MutualFundApiService
import com.investwise_india.network.MutualFundData
import com.investwise_india.network.MutualFundDetails
import com.investwise_india.network.MutualFundMeta
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Response
import kotlin.math.min

data class SearchResult(
    val fund: MutualFund,
    val matchScore: Int,
    val matchRanges: List<IntRange> = emptyList()
)

@OptIn(FlowPreview::class)
@Composable
fun SelectFundDialog(
    onDismiss: () -> Unit,
    onFundSelected: (MutualFund) -> Unit,
    apiService: MutualFundApiService
) {
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var allFunds by remember { mutableStateOf<List<MutualFund>>(emptyList()) }
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    
    // Create a dispatcher for background processing
    val scope = rememberCoroutineScope()

    // Load initial funds
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllMutualFunds()
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Default) {
                        allFunds = response.body()!!
                        searchResults = allFunds.map { SearchResult(it, 0) }
                    }
                } else {
                    error = "Failed to load funds"
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // Handle search
    LaunchedEffect(searchQuery, allFunds) {
        if (!isLoading && error == null) {
            scope.launch(Dispatchers.Default) {
                val query = searchQuery.trim()
                val results = if (query.isEmpty()) {
                    allFunds.map { SearchResult(it, 0) }
                } else {
                    allFunds.asSequence()
                        .mapNotNull { fund -> calculateSearchMatch(fund, query) }
                        .filter { it.matchScore > 0 }
                        .sortedByDescending { it.matchScore }
                        .toList()
                }
                withContext(Dispatchers.Main) {
                    searchResults = results
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Fund to Compare",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search funds by name or code...") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    error != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    else -> {
                        if (searchResults.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No matching funds found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = searchResults,
                                    key = { it.fund.schemeCode }
                                ) { result ->
                                    FundSelectionItem(
                                        searchResult = result,
                                        searchQuery = searchQuery,
                                        onClick = { onFundSelected(result.fund) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun FundSelectionItem(
    searchResult: SearchResult,
    searchQuery: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            if (searchQuery.isNotEmpty() && searchResult.matchRanges.isNotEmpty()) {
                // Highlighted text for matching search
                Text(
                    buildAnnotatedString {
                        val text = searchResult.fund.schemeName
                        var lastIndex = 0
                        
                        searchResult.matchRanges.forEach { range ->
                            // Add non-highlighted text before match
                            append(text.substring(lastIndex, range.first))
                            
                            // Add highlighted text
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            ) {
                                append(text.substring(range.first, range.last + 1))
                            }
                            
                            lastIndex = range.last + 1
                        }
                        
                        // Add remaining text
                        if (lastIndex < text.length) {
                            append(text.substring(lastIndex))
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = searchResult.fund.schemeName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = "Scheme Code: ${searchResult.fund.schemeCode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

private fun calculateSearchMatch(fund: MutualFund, query: String): SearchResult? {
    val queryLower = query.lowercase()
    val nameLower = fund.schemeName.lowercase()
    val schemeCode = fund.schemeCode.toString()
    
    // Check exact matches first
    if (nameLower.contains(queryLower) || schemeCode.contains(query)) {
        val matchRanges = mutableListOf<IntRange>()
        
        // Find all occurrences in name
        var index = nameLower.indexOf(queryLower)
        while (index != -1) {
            matchRanges.add(index until index + query.length)
            index = nameLower.indexOf(queryLower, index + 1)
        }
        
        // Calculate match score (higher is better)
        val score = when {
            nameLower.startsWith(queryLower) -> 100 // Highest priority for starts-with matches
            schemeCode.contains(query) -> 90 // High priority for scheme code matches
            matchRanges.isNotEmpty() -> 80 - matchRanges.first().first // Priority based on position
            else -> 0
        }
        
        return SearchResult(fund, score, matchRanges)
    }
    
    // Fuzzy matching for more lenient search
    val maxDistance = (query.length / 2).coerceAtLeast(1)
    val distance = levenshteinDistance(queryLower, nameLower)
    
    return if (distance <= maxDistance) {
        SearchResult(fund, 50 - distance) // Lower score for fuzzy matches
    } else {
        null
    }
}

private fun levenshteinDistance(s1: String, s2: String): Int {
    val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
    
    for (i in 0..s1.length) {
        for (j in 0..s2.length) {
            dp[i][j] = when {
                i == 0 -> j
                j == 0 -> i
                else -> min(
                    dp[i - 1][j - 1] + if (s1[i - 1] == s2[j - 1]) 0 else 1,
                    min(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1
                    )
                )
            }
        }
    }
    
    return dp[s1.length][s2.length]
}

@Preview(showBackground = true)
@Composable
fun SelectFundDialogPreview() {
    val mockFund = MutualFund(
        schemeCode = 100171,
        schemeName = "Sample Mutual Fund",
        isinGrowth = "INF123XYZ789",
        isinDivReinvestment = null
    )
    
    val mockApiService = object : MutualFundApiService {
        override suspend fun getAllMutualFunds(): Response<List<MutualFund>> {
            return Response.success(listOf(mockFund))
        }
        
        override suspend fun getMutualFundDetails(schemeCode: Int): Response<MutualFundDetails> {
            return Response.success(
                MutualFundDetails(
                    meta = MutualFundMeta(
                        fund_house = "Sample Fund House",
                        scheme_type = "Growth",
                        scheme_category = "Equity",
                        scheme_code = 100171,
                        scheme_name = "Sample Mutual Fund",
                        isin_growth = "INF123XYZ789",
                        isin_div_reinvestment = null
                    ),
                    data = listOf(
                        MutualFundData(
                            date = "2024-03-20",
                            nav = "25.4321"
                        )
                    )
                )
            )
        }
    }
    
    SelectFundDialog(
        onDismiss = {},
        onFundSelected = {},
        apiService = mockApiService
    )
} 