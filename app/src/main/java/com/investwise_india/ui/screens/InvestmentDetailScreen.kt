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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            // Investment name
            Text(
                text = investment.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Investment type badge
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
            
            // Description section
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = investment.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Investment details section
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
            
            // Additional information section
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