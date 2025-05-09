package com.investwise_india.model

data class MutualFund(
    val schemeCode: Int,
    val schemeName: String,
    val isinGrowth: String?,
    val isinDivReinvestment: String?
)

data class MutualFundCategory(
    val id: Int,
    val name: String,
    val description: String,
    val iconResId: Int,
    val colorResId: Int
)

data class DebtFundSubcategory(
    val id: Int,
    val name: String,
    val description: String,
    val schemeCodes: List<Int>
)

// Define fund categories
object MutualFundCategories {
    val categories = listOf(
        MutualFundCategory(
            1,
            "Large Cap",
            "Top 100 companies by market capitalization",
            com.investwise_india.R.drawable.ic_large_cap,
            com.investwise_india.R.color.large_cap_color
        ),
        MutualFundCategory(
            2,
            "Mid Cap",
            "101-250th companies by market capitalization",
            com.investwise_india.R.drawable.ic_mid_cap,
            com.investwise_india.R.color.mid_cap_color
        ),
        MutualFundCategory(
            3,
            "Small Cap",
            "251st company onwards by market capitalization",
            com.investwise_india.R.drawable.ic_small_cap,
            com.investwise_india.R.color.small_cap_color
        ),
        MutualFundCategory(
            4,
            "Hybrid Fund",
            "Mix of equity and debt investments",
            com.investwise_india.R.drawable.ic_hybrid,
            com.investwise_india.R.color.hybrid_color
        ),
        MutualFundCategory(
            5,
            "Debt Fund",
            "Fixed income securities investments",
            com.investwise_india.R.drawable.ic_debt,
            com.investwise_india.R.color.debt_color
        ),
        MutualFundCategory(
            6,
            "Tax Saving (ELSS)",
            "Equity funds with tax benefits under 80C",
            com.investwise_india.R.drawable.ic_tax_saving,
            com.investwise_india.R.color.tax_saving_color
        ),
        MutualFundCategory(
            7,
            "Flexicap",
            "Flexible market capitalization allocation",
            com.investwise_india.R.drawable.ic_flexicap,
            com.investwise_india.R.color.flexicap_color
        ),
        MutualFundCategory(
            8,
            "Thematic Fund",
            "Sector or theme specific investments",
            com.investwise_india.R.drawable.ic_thematic,
            com.investwise_india.R.color.thematic_color
        ),
        MutualFundCategory(
            9,
            "Index Fund",
            "Tracks market indices like Nifty or Sensex",
            com.investwise_india.R.drawable.ic_index,
            com.investwise_india.R.color.index_color
        ),
        MutualFundCategory(
            10,
            "International",
            "Investments in foreign markets",
            com.investwise_india.R.drawable.ic_international,
            com.investwise_india.R.color.international_color
        )
    )
}

// Define debt fund subcategories
object DebtFundSubcategories {
    val subcategories = listOf(
        DebtFundSubcategory(
            1,
            "Low Duration",
            "Funds with portfolio duration between 6 months to 1 year",
            listOf(143612, 120398, 119523, 118942, 133810, 120513, 118709) // Scheme codes
        ),
        DebtFundSubcategory(
            2,
            "Overnight Fund",
            "Funds investing in overnight securities",
            listOf(145810, 147951, 146675) //scheme codes
        ),
        DebtFundSubcategory(
            3,
            "Liquid Fund",
            "Funds investing in money market instruments",
            listOf(120837, 118701, 139538, 119568, 120197, 119766) //scheme codes
        ),
        DebtFundSubcategory(
            4,
            "Ultra Short Duration",
            "Funds with portfolio duration between 3-6 months",
            listOf(120746, 143494, 119205) // Scheme codes
        ),
        DebtFundSubcategory(
            5,
            "Floating Rate",
            "Funds investing in floating rate instruments",
            listOf(120425, 149049) //Scheme codes
        )
    )
} 