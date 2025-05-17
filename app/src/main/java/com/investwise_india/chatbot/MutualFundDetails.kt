package com.investwise_india.chatbot

import com.investwise_india.model.MutualFundCategory

data class MutualFund(
    val schemeCode: Int,
    val schemeName: String,
    val isinGrowth: String?,
    val isinDivReinvestment: String?
)

//data class MutualFundCategory(
//    val id: Int,
//    val name: String,
//    val description: String,
//)

data class DebtFundSubcategory(
    val id: Int,
    val name: String,
    val description: String,
    val schemeCodes: List<Int>
)

// Define fund categories
//object MutualFundCategories {
//    val categories = listOf(
//        MutualFundCategory(
//            1,
//            "Large Cap",
//            "Top 100 companies by market capitalization",
//        ),
//        MutualFundCategory(
//            2,
//            "Mid Cap",
//            "101-250th companies by market capitalization",
//        ),
//        MutualFundCategory(
//            3,
//            "Small Cap",
//            "251st company onwards by market capitalization",
//        ),
//        MutualFundCategory(
//            4,
//            "Hybrid Fund",
//            "Mix of equity and debt investments",
//        ),
//        MutualFundCategory(
//            5,
//            "Debt Fund",
//            "Fixed income securities investments",
//
//            ),
//        MutualFundCategory(
//            6,
//            "Tax Saving (ELSS)",
//            "Equity funds with tax benefits under 80C",
//
//            ),
//        MutualFundCategory(
//            7,
//            "Flexicap",
//            "Flexible market capitalization allocation",
//        ),
//        MutualFundCategory(
//            8,
//            "Thematic Fund",
//            "Sector or theme specific investments",
//        ),
//        MutualFundCategory(
//            9,
//            "Index Fund",
//            "Tracks market indices like Nifty or Sensex",
//        ),
//        MutualFundCategory(
//            10,
//            "International",
//            "Investments in foreign markets",
//        )
//    )
//}

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

fun mutualFundCategory(
    category: MutualFundCategory,
    selectedSubcategory: DebtFundSubcategory? = null
): List<Int> {
    return when (category.id) {
        5 -> { // Debt Fund
            selectedSubcategory?.schemeCodes ?: emptyList()
        }

        1 -> listOf(118632, 120586, 119598, 119250) // Large Cap
        2 -> listOf(127042, 118989, 118668, 125307, 119775, 120841) // Mid Cap
        3 -> listOf(147946, 118778, 120828, 148618, 125354, 130503) // Small Cap
        4 -> listOf(120484, 120251, 118624, 120674, 143537, 120819, 119019) // Hybrid
        6 -> listOf(120847, 120270, 119723, 133386, 119242) // ELSS
        7 -> listOf(120843, 122639, 118955, 129046, 120492) // Flexicap
        8 -> listOf(120700, 148747, 152064) // Thematic
        9 -> listOf(147622, 148555, 148807, 148726, 149892, 149389) // Index
        10 -> listOf(118545, 118849, 120698, 125504, 118582) // International
        else -> emptyList()
    }
}

// https://api.mfapi.in/mf/code/latest
