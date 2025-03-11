package com.investwise_india.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investwise_india.ui.theme.InvestWise_IndiaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Header(
    userName: String = "Investor",
    showGreeting: Boolean = true,
    modifier: Modifier = Modifier
) {
    val currentTime = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
    val greeting = when {
        currentTime < 12 -> "Good Morning"
        currentTime < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (showGreeting) {
                Text(
                    text = "$greeting, $userName!",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Text(
                text = "Discover the best investment options in India",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    InvestWise_IndiaTheme {
        Header(userName = "Investor")
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderWithoutGreetingPreview() {
    InvestWise_IndiaTheme {
        Header(userName = "Investor", showGreeting = false)
    }
} 