package com.investwise_india.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.investwise_india.model.InvestmentType
import com.investwise_india.ui.theme.AbsoluteReturnColor
import com.investwise_india.ui.theme.FixedReturnColor
import com.investwise_india.ui.theme.InvestWise_IndiaTheme

enum class TabOption {
    ALL, FIXED_RETURN, ABSOLUTE_RETURN
}

@Composable
fun InvestmentTypeTabs(
    selectedTab: TabOption,
    onTabSelected: (TabOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TabItem(
            text = "All",
            isSelected = selectedTab == TabOption.ALL,
            onClick = { onTabSelected(TabOption.ALL) }
        )
        
        TabItem(
            text = "Fixed Return",
            isSelected = selectedTab == TabOption.FIXED_RETURN,
            color = FixedReturnColor,
            onClick = { onTabSelected(TabOption.FIXED_RETURN) }
        )
        
        TabItem(
            text = "Absolute Return",
            isSelected = selectedTab == TabOption.ABSOLUTE_RETURN,
            color = AbsoluteReturnColor,
            onClick = { onTabSelected(TabOption.ABSOLUTE_RETURN) }
        )
    }
}

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
    val textColor = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = fontWeight
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InvestmentTypeTabsPreview() {
    InvestWise_IndiaTheme {
        InvestmentTypeTabs(
            selectedTab = TabOption.ALL,
            onTabSelected = {}
        )
    }
} 