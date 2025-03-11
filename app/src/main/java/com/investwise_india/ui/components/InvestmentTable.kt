package com.investwise_india.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investwise_india.model.InvestmentData
import com.investwise_india.model.InvestmentOption
import com.investwise_india.model.InvestmentType
import com.investwise_india.ui.theme.AbsoluteReturnColor
import com.investwise_india.ui.theme.FixedReturnColor
import com.investwise_india.ui.theme.InvestWise_IndiaTheme
import androidx.compose.foundation.verticalScroll

@Composable
fun InvestmentTablesScreen(
    modifier: Modifier = Modifier,
    selectedTab: TabOption = TabOption.ALL
) {
    // Use a scrollable column for the entire content
    Column(
        modifier = modifier
            .fillMaxWidth()
            // .verticalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        when (selectedTab) {
            TabOption.ALL -> {
                // Show both tables
                Text(
                    text = "Fixed Return Investments",
                    style = MaterialTheme.typography.titleMedium,
                    color = FixedReturnColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                FixedReturnsTable(
                    investmentOptions = InvestmentData.fixedReturnOptions,
                    modifier = Modifier
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Absolute Return Investments",
                    style = MaterialTheme.typography.titleMedium,
                    color = AbsoluteReturnColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                AbsoluteReturnsTable(
                    investmentOptions = InvestmentData.absoluteReturnOptions,
                    modifier = Modifier
                )
            }
            TabOption.FIXED_RETURN -> {
                // Show only fixed returns table
                Text(
                    text = "Fixed Return Investments",
                    style = MaterialTheme.typography.titleMedium,
                    color = FixedReturnColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                FixedReturnsTable(
                    investmentOptions = InvestmentData.fixedReturnOptions,
                    modifier = Modifier
                )
            }
            TabOption.ABSOLUTE_RETURN -> {
                // Show only absolute returns table
                Text(
                    text = "Absolute Return Investments",
                    style = MaterialTheme.typography.titleMedium,
                    color = AbsoluteReturnColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                AbsoluteReturnsTable(
                    investmentOptions = InvestmentData.absoluteReturnOptions,
                    modifier = Modifier
                )
            }
        }
        
        // Instructions for scrolling
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Swipe horizontally to see more details",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun FixedReturnsTable(
    investmentOptions: List<InvestmentOption>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 500.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 900.dp)
                    .padding(8.dp)
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .background(FixedReturnColor.copy(alpha = 0.1f))
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = "Scheme Name", cellWidth = 180.dp, isHeader = true)
                    TableCell(text = "Returns", cellWidth = 100.dp, isHeader = true)
                    TableCell(text = "Risk", cellWidth = 100.dp, isHeader = true)
                    TableCell(text = "Money Doubles", cellWidth = 120.dp, isHeader = true)
                    TableCell(text = "Lock-in Period", cellWidth = 120.dp, isHeader = true)
                    TableCell(text = "Tax Benefits", cellWidth = 150.dp, isHeader = true)
                    TableCell(text = "Min Amount", cellWidth = 100.dp, isHeader = true)
                }
                
                // Table Content
                Column {
                    investmentOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .background(FixedReturnColor.copy(alpha = 0.05f))
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = option.name, cellWidth = 180.dp)
                            TableCell(text = option.returnPotential, cellWidth = 100.dp)
                            TableCell(text = option.riskLevel, cellWidth = 100.dp)
                            // Calculate money doubling time based on returns (approximate)
                            val doublingTime = when {
                                option.returnPotential.contains("5-7%") -> "10-14 years"
                                option.returnPotential.contains("6-8%") -> "9-12 years"
                                option.returnPotential.contains("7-8%") -> "9-10 years"
                                option.returnPotential.contains("6.9%") -> "~10 years"
                                option.returnPotential.contains("6.8%") -> "~10.5 years"
                                option.returnPotential.contains("8-12%") -> "6-9 years"
                                else -> "Varies"
                            }
                            TableCell(text = doublingTime, cellWidth = 120.dp)
                            // Extract lock-in period from liquidity
                            val lockInPeriod = when {
                                option.liquidityLevel.contains("lock-in") -> {
                                    val parts = option.liquidityLevel.split("(", ")")
                                    if (parts.size > 1) parts[1] else "Varies"
                                }
                                option.liquidityLevel.contains("encashed") -> "2.5 years"
                                else -> "None"
                            }
                            TableCell(text = lockInPeriod, cellWidth = 120.dp)
                            TableCell(text = option.taxBenefits, cellWidth = 150.dp)
                            TableCell(text = option.minimumInvestment, cellWidth = 100.dp)
                        }
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }
}

@Composable
fun AbsoluteReturnsTable(
    investmentOptions: List<InvestmentOption>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 500.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 1200.dp)
                    .padding(8.dp)
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .background(AbsoluteReturnColor.copy(alpha = 0.1f))
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = "Scheme Name", cellWidth = 150.dp, isHeader = true)
                    TableCell(text = "1Y Returns", cellWidth = 90.dp, isHeader = true)
                    TableCell(text = "3Y Returns", cellWidth = 90.dp, isHeader = true)
                    TableCell(text = "5Y Returns", cellWidth = 90.dp, isHeader = true)
                    TableCell(text = "Money Doubles", cellWidth = 120.dp, isHeader = true)
                    TableCell(text = "Expense Ratio", cellWidth = 110.dp, isHeader = true)
                    TableCell(text = "Lock-in Period", cellWidth = 110.dp, isHeader = true)
                    TableCell(text = "Liquidity", cellWidth = 120.dp, isHeader = true)
                    TableCell(text = "Tax Benefits", cellWidth = 120.dp, isHeader = true)
                    TableCell(text = "Min Amount", cellWidth = 100.dp, isHeader = true)
                    TableCell(text = "Risk", cellWidth = 90.dp, isHeader = true)
                }
                
                // Table Content
                Column {
                    investmentOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .background(AbsoluteReturnColor.copy(alpha = 0.05f))
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCell(text = option.name, cellWidth = 150.dp)
                            // Generate sample return data for different time periods
                            val oneYearReturn = when(option.name) {
                                "Stocks" -> "8-12%"
                                "Mutual Funds" -> "7-15%"
                                "Cryptocurrency" -> "±30%"
                                "Exchange Traded Funds (ETFs)" -> "6-10%"
                                "Commodities" -> "4-8%"
                                else -> "Varies"
                            }
                            val threeYearReturn = when(option.name) {
                                "Stocks" -> "25-40%"
                                "Mutual Funds" -> "20-45%"
                                "Cryptocurrency" -> "±100%"
                                "Exchange Traded Funds (ETFs)" -> "18-30%"
                                "Commodities" -> "12-25%"
                                else -> "Varies"
                            }
                            val fiveYearReturn = when(option.name) {
                                "Stocks" -> "40-80%"
                                "Mutual Funds" -> "35-100%"
                                "Cryptocurrency" -> "±200%"
                                "Exchange Traded Funds (ETFs)" -> "30-60%"
                                "Commodities" -> "20-50%"
                                else -> "Varies"
                            }
                            val expenseRatio = when(option.name) {
                                "Stocks" -> "0.1-0.5%"
                                "Mutual Funds" -> "0.5-2.5%"
                                "Cryptocurrency" -> "0.1-1%"
                                "Exchange Traded Funds (ETFs)" -> "0.1-0.5%"
                                "Commodities" -> "0.2-1%"
                                else -> "Varies"
                            }
                            
                            TableCell(text = oneYearReturn, cellWidth = 90.dp)
                            TableCell(text = threeYearReturn, cellWidth = 90.dp)
                            TableCell(text = fiveYearReturn, cellWidth = 90.dp)
                            
                            // Calculate money doubling time based on 5-year returns
                            val doublingTime = when {
                                option.name == "Stocks" -> "5-8 years"
                                option.name == "Mutual Funds" -> "4-8 years"
                                option.name == "Cryptocurrency" -> "1-4 years*"
                                option.name == "Exchange Traded Funds (ETFs)" -> "6-9 years"
                                option.name == "Commodities" -> "7-12 years"
                                else -> "Varies"
                            }
                            TableCell(text = doublingTime, cellWidth = 120.dp)
                            
                            TableCell(text = expenseRatio, cellWidth = 110.dp)
                            
                            // Extract lock-in period from liquidityLevel
                            val lockInPeriod = when {
                                option.name.contains("ELSS") -> "3 years"
                                else -> "None"
                            }
                            TableCell(text = lockInPeriod, cellWidth = 110.dp)
                            
                            TableCell(text = option.liquidityLevel, cellWidth = 120.dp)
                            TableCell(text = option.taxBenefits, cellWidth = 120.dp)
                            TableCell(text = option.minimumInvestment, cellWidth = 100.dp)
                            TableCell(text = option.riskLevel, cellWidth = 90.dp)
                        }
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    }
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    cellWidth: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(cellWidth)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isHeader) 14.sp else 12.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InvestmentTablesPreview() {
    InvestWise_IndiaTheme {
        InvestmentTablesScreen()
    }
} 