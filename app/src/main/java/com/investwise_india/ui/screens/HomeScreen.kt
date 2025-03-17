package com.investwise_india.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.investwise_india.data.DataModule
import com.investwise_india.data.repository.MarketIndexData
import com.investwise_india.model.InvestmentData
import com.investwise_india.model.InvestmentOption
import com.investwise_india.model.InvestmentType
import com.investwise_india.ui.components.Header
import com.investwise_india.ui.components.InvestmentDetail
import com.investwise_india.ui.components.TabOption
import com.investwise_india.ui.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onInvestmentClick: (InvestmentOption) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(TabOption.ALL) }
    
    // Animation states
    val animatedVisibleState = remember { mutableStateOf(true) }
    
    // Simulate a portfolio value
    val portfolioValue = remember { 128500.0 + Random.nextDouble(0.0, 1500.0) }
    val portfolioGrowth = remember { 8.2 + Random.nextDouble(0.0, 0.4) }
    
    LaunchedEffect(Unit) {
        // Small delay before starting animations
        kotlinx.coroutines.delay(20)
        animatedVisibleState.value = true
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Modern header with gradient
            PortfolioHeader(portfolioValue, portfolioGrowth)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Quick action buttons
            AnimatedVisibility(
                visible = animatedVisibleState.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 20, delayMillis = 25)) +
                        slideInVertically(animationSpec = tween(durationMillis = 20, delayMillis = 25))
                        { fullHeight -> fullHeight / 5 }
            ) {
                QuickActionRow()
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Market overview card
            AnimatedVisibility(
                visible = animatedVisibleState.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = 50)) +
                        slideInVertically(animationSpec = tween(durationMillis = 200, delayMillis = 100))
                        { fullHeight -> fullHeight / 5 }
            ) {
                MarketOverviewCard()
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Section title
            AnimatedVisibility(
                visible = animatedVisibleState.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = 100)) +
                        slideInVertically(animationSpec = tween(durationMillis = 200, delayMillis = 100))
                        { fullHeight -> fullHeight / 5 }
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Explore Investment Options",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Custom segmented control
                    InvestmentTypeSegmentedControl(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Investment options
            val filteredInvestments = when (selectedTab) {
                TabOption.ALL -> InvestmentData.allOptions
                TabOption.FIXED_RETURN -> InvestmentData.fixedReturnOptions
                TabOption.ABSOLUTE_RETURN -> InvestmentData.absoluteReturnOptions
            }
            
            AnimatedVisibility(
                visible = animatedVisibleState.value,
                enter = fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = 150)) +
                        slideInVertically(animationSpec = tween(durationMillis = 200, delayMillis = 150))
                        { fullHeight -> fullHeight / 5 }
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    filteredInvestments.forEach { investment ->
                        InvestmentCard(
                            investment = investment,
                            onCardClick = onInvestmentClick
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            // Spacer at the bottom to avoid content being cut off by navigation
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PortfolioHeader(portfolioValue: Double, portfolioGrowth: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Greeting text
                Column(modifier = Modifier.weight(1f)) {
                    val currentTime = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
                    val greeting = when {
                        currentTime < 12 -> "Good Morning"
                        currentTime < 17 -> "Good Afternoon"
                        else -> "Good Evening"
                    }
                    
                    Text(
                        text = "$greeting, Investor!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Discover the best investment options in India",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }

                // User profile circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Portfolio value section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Your Portfolio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${NumberFormat.getNumberInstance(Locale.US).format(portfolioValue)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    
                    // Growth rate
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = "Trending Up",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = "+${String.format("%.2f", portfolioGrowth)}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF8F1B1B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickActionButton(
            icon = Icons.Default.AddCircle,
            text = "Invest",
            color = MaterialTheme.colorScheme.primary
        )

        QuickActionButton(
            icon = Icons.Default.Inventory,
            text = "My Funds",
            color = MaterialTheme.colorScheme.secondary
        )

        QuickActionButton(
            icon = Icons.Default.Compare,
            text = "Compare",
            color = MaterialTheme.colorScheme.tertiary
        )

        QuickActionButton(
            icon = Icons.Default.PieChart,
            text = "Analysis",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, text: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { /* Handle click */ }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = color.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MarketOverviewCard() {
    // Get repository instance
    val marketRepository = DataModule.marketDataRepository
    
    // State for market indices data
    var indicesData by remember { mutableStateOf(marketRepository.getCachedOrDefaultIndices()) }
    var isLoading by remember { mutableStateOf(false) }
    var lastUpdated by remember { mutableStateOf("") }
    var initialLoadDone by remember { mutableStateOf(false) }
    
    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()
    
    // Function to load market data
    val loadMarketData = {
        coroutineScope.launch {
            isLoading = true
            try {
                // Fetch the latest data
                val newData = marketRepository.fetchMarketIndices(forceRefresh = true)
                if (newData.isNotEmpty()) {
                    indicesData = newData
                }
                
                // Update the last updated time
                val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                lastUpdated = "Last updated: ${dateFormat.format(Date())}"
                initialLoadDone = true
            } catch (e: Exception) {
                // If there's an error, we still have default values
                println("Error loading market data: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
    
    // Only check for cached data on first composition, don't fetch from network
    LaunchedEffect(Unit) {
        // Just use cached data if available, don't make network request
        val cachedData = marketRepository.getCachedOrDefaultIndices()
        if (cachedData.isNotEmpty()) {
            indicesData = cachedData
            
            // If we have real data (not defaults), set the last updated time
            if (marketRepository.marketIndices.value.isNotEmpty()) {
                val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                lastUpdated = "Last updated: ${dateFormat.format(Date())}"
                initialLoadDone = true
            }
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = "Market Trends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    
                    Text(
                        text = if (lastUpdated.isNotEmpty()) lastUpdated else "Not updated yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show loading state or market data
            if (isLoading && indicesData.isEmpty()) {
                // Only show loading indicator if we don't have any data
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Get market data for each index
                    val niftyData = indicesData["^NSEI"]
                    val sensexData = indicesData["^BSESN"]
                    val bankNiftyData = indicesData["^NSEBANK"]
                    
                    // Display the market data
                    if (niftyData != null) {
                        MarketIndexItem(
                            name = niftyData.name,
                            value = niftyData.value,
                            change = niftyData.change,
                            isPositive = niftyData.isPositive
                        )
                    }
                    
                    if (sensexData != null) {
                        MarketIndexItem(
                            name = sensexData.name,
                            value = sensexData.value,
                            change = sensexData.change,
                            isPositive = sensexData.isPositive
                        )
                    }
                    
                    if (bankNiftyData != null) {
                        MarketIndexItem(
                            name = bankNiftyData.name,
                            value = bankNiftyData.value,
                            change = bankNiftyData.change,
                            isPositive = bankNiftyData.isPositive
                        )
                    }
                }
            }
            
            // Refresh button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = { loadMarketData() },
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (!initialLoadDone) "Load Data" else "Refresh",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun MarketIndexItem(name: String, value: String, change: String, isPositive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Text(
            text = change,
            style = MaterialTheme.typography.bodySmall,
            color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InvestmentTypeSegmentedControl(
    selectedTab: TabOption,
    onTabSelected: (TabOption) -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        TabOption.values().forEach { tab ->
            val isSelected = tab == selectedTab
            val buttonColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Transparent
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(buttonColor)
                    .clickable { onTabSelected(tab) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (tab) {
                        TabOption.ALL -> "All"
                        TabOption.FIXED_RETURN -> "Fixed"
                        TabOption.ABSOLUTE_RETURN -> "Variable"
                    },
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun InvestmentCard(
    investment: InvestmentOption,
    onCardClick: (InvestmentOption) -> Unit = {}
) {
    val typeColor = when (investment.type) {
        InvestmentType.FIXED_RETURN -> FixedReturnColor
        InvestmentType.ABSOLUTE_RETURN -> AbsoluteReturnColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onCardClick(investment) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with investment name and type tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Investment name - constrained width
                Text(
                    text = investment.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Type tag - fixed size, won't move
                Text(
                    text = when (investment.type) {
                        InvestmentType.FIXED_RETURN -> "FIXED"
                        InvestmentType.ABSOLUTE_RETURN -> "VARIABLE"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = typeColor,
                    modifier = Modifier
                        .background(
                            color = typeColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description - fixed height
            Text(
                text = investment.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(36.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom attributes row - fixed layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Each attribute in a fixed-width column
                Box(modifier = Modifier.weight(1f)) {
                    InvestmentAttribute("Risk", investment.riskLevel)
                }
                Box(modifier = Modifier.weight(1f)) {
                    InvestmentAttribute("Return", investment.returnPotential)
                }
                Box(modifier = Modifier.weight(1f)) {
                    InvestmentAttribute("Min", investment.minimumInvestment)
                }
            }
        }
    }
}

@Composable
fun InvestmentAttribute(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth() // Fill the parent Box width
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis, // Add text truncation
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    InvestWise_IndiaTheme {
        Box {
            Row {
                 HomeScreen(onInvestmentClick = {})
                // QuickActionRow()
                // MarketOverviewCard()
                // InvestmentTypeSegmentedControl(selectedTab = TabOption.ALL, onTabSelected = {})
//                InvestmentCard(
//                    investment = InvestmentData.fixedReturnOptions[0],
//                    onCardClick = {}
//                )
            }
        }
    }
}