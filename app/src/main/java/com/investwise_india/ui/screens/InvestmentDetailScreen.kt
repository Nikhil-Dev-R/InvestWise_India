package com.investwise_india.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investwise_india.model.InvestmentData
import com.investwise_india.model.InvestmentOption
import com.investwise_india.model.InvestmentType
import com.investwise_india.ui.theme.AbsoluteReturnColor
import com.investwise_india.ui.theme.FixedReturnColor
import com.investwise_india.ui.theme.InvestWise_IndiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentDetailScreen(
    investment: InvestmentOption,
    onBackPressed: () -> Unit
) {
    val typeColor = when (investment.type) {
        InvestmentType.FIXED_RETURN -> FixedReturnColor
        InvestmentType.ABSOLUTE_RETURN -> AbsoluteReturnColor
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Investment Details") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Investment Name
            Text(
                text = investment.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Investment Type Badge
            Text(
                text = when (investment.type) {
                    InvestmentType.FIXED_RETURN -> "FIXED RETURN"
                    InvestmentType.ABSOLUTE_RETURN -> "VARIABLE RETURN"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = typeColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(
                        color = typeColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Section Heading: Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            StyledDetailedDescription(investment.detailedDescription)

            Spacer(modifier = Modifier.height(32.dp))

            // Investment Details Section
            Text(
                text = "Investment Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            DetailItem("Risk Level", investment.riskLevel)
            DetailItem("Return Potential", investment.returnPotential)
            DetailItem("Liquidity", investment.liquidityLevel)
            DetailItem("Tax Benefits", investment.taxBenefits)
            DetailItem("Minimum Investment", investment.minimumInvestment)
            DetailItem("Recommended Time Horizon", investment.recommendedTimeHorizon)

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Information Section
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Key Investment Considerations",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Always consider your financial goals before investing\n" +
                                "• Understand the risks involved with this investment type\n" +
                                "• Review how this fits into your overall portfolio strategy\n" +
                                "• Consider consulting with a financial advisor",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StyledDetailedDescription(description: String) {
    // Wrap the description in a Card for better visual grouping.
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Split the description by double newlines to separate paragraphs/sections.
            val paragraphs = description.split("\n\n")
            paragraphs.forEach { paragraph ->
                when {
                    paragraph.startsWith("Investment Overview:") ||
                            paragraph.startsWith("Overview:") -> {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    paragraph.startsWith("Risk Analysis:") -> {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    paragraph.startsWith("Liquidity Analysis:") -> {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    paragraph.startsWith("Tax Implications:") -> {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    paragraph.startsWith("Key Features:") -> {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    paragraph.startsWith("Note:") -> {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.error,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    else -> {
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Divider(
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}
