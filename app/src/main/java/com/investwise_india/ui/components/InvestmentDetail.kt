package com.investwise_india.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investwise_india.model.InvestmentData
import com.investwise_india.model.InvestmentOption
import com.investwise_india.model.InvestmentType
import com.investwise_india.ui.theme.AbsoluteReturnColor
import com.investwise_india.ui.theme.FixedReturnColor
import com.investwise_india.ui.theme.InvestWise_IndiaTheme

@Composable
fun InvestmentDetail(
    investment: InvestmentOption,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (investment.type) {
        InvestmentType.FIXED_RETURN -> FixedReturnColor.copy(alpha = 0.1f)
        InvestmentType.ABSOLUTE_RETURN -> AbsoluteReturnColor.copy(alpha = 0.1f)
    }
    
    val typeColor = when (investment.type) {
        InvestmentType.FIXED_RETURN -> FixedReturnColor
        InvestmentType.ABSOLUTE_RETURN -> AbsoluteReturnColor
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Investment Name and Type
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = investment.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = investment.type.name.replace("_", " "),
                    fontSize = 10.sp,
                    color = typeColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(
                            color = typeColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = investment.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Investment Details
            DetailItem(label = "Risk Level", value = investment.riskLevel)
            DetailItem(label = "Return Potential", value = investment.returnPotential)
            DetailItem(label = "Liquidity", value = investment.liquidityLevel)
            DetailItem(label = "Tax Benefits", value = investment.taxBenefits)
            DetailItem(label = "Minimum Investment", value = investment.minimumInvestment)
            DetailItem(label = "Recommended Time Horizon", value = investment.recommendedTimeHorizon)
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.4f)
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InvestmentDetailPreview() {
    InvestWise_IndiaTheme {
        InvestmentDetail(investment = InvestmentData.fixedReturnOptions.first())
    }
} 