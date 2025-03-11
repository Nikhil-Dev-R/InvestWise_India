//package com.investwise_india
package com.investwise_india.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.investwise_india.model.InvestmentData
import com.investwise_india.model.InvestmentOption
import com.investwise_india.ui.components.Header
import com.investwise_india.ui.components.InvestmentDetail
import com.investwise_india.ui.components.InvestmentTablesScreen
import com.investwise_india.ui.components.InvestmentTypeTabs
import com.investwise_india.ui.components.TabOption
import com.investwise_india.ui.theme.InvestWise_IndiaTheme

enum class ViewMode {
    TABLE, CARDS
}

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(TabOption.ALL) }
    var viewMode by remember { mutableStateOf(ViewMode.TABLE) }

    // Filter investments based on selected tab
    val filteredInvestments = when (selectedTab) {
        TabOption.ALL -> InvestmentData.allOptions
        TabOption.FIXED_RETURN -> InvestmentData.fixedReturnOptions
        TabOption.ABSOLUTE_RETURN -> InvestmentData.absoluteReturnOptions
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        // Wrap everything in a scrollable column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // Header
            Header(
                userName = "Investor",
                showGreeting = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Investment type tabs
            InvestmentTypeTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            )
            
            // View mode tabs
            ViewModeTabs(
                selectedViewMode = viewMode,
                onViewModeSelected = { viewMode = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content section
            when (viewMode) {
                ViewMode.TABLE -> {
                    // For table view
                    InvestmentTablesScreen(
                        selectedTab = selectedTab,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                ViewMode.CARDS -> {
                    // For card view, we use a regular Column instead of LazyColumn
                    // since everything is already in a scrollable container
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        filteredInvestments.forEach { investment ->
                            InvestmentDetail(investment = investment)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            
            // Add extra space at the bottom to avoid content being cut off by navigation
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ViewModeTabs(
    selectedViewMode: ViewMode,
    onViewModeSelected: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedViewMode.ordinal,
        modifier = modifier
    ) {
        Tab(
            selected = selectedViewMode == ViewMode.TABLE,
            onClick = { onViewModeSelected(ViewMode.TABLE) },
            text = { Text("Table View") }
        )
        Tab(
            selected = selectedViewMode == ViewMode.CARDS,
            onClick = { onViewModeSelected(ViewMode.CARDS) },
            text = { Text("Card View") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    InvestWise_IndiaTheme {
        HomeScreen()
    }
} 