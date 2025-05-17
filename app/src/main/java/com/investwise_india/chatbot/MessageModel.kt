package com.investwise_india.chatbot

import android.util.Log
import com.investwise_india.model.MutualFundCategories

data class MessageModel(
    val message: String,
    val role: String,
)

fun generateGeminiPrompt(fundMetaList: List<FundMeta>): String {
    val builder = StringBuilder()

    // Intro
    builder.append("You are a financial assistant. Use the following data to answer user queries clearly, accurately, and responsibly.\n\n")

    // Section 1: Investment Options
    builder.append("=== Fixed and Absolute Return Investment Options ===\n\n")

    InvestmentData.fixedReturnOptions.forEach {
        builder.append("• ${it.name} (${it.type}): ${it.description}\n")
        builder.append("  → Risk: ${it.riskLevel}\n")
        builder.append("  → Return Potential: ${it.returnPotential}\n")
        builder.append("  → Liquidity: ${it.liquidityLevel}\n")
        builder.append("  → Tax Benefits: ${it.taxBenefits}\n")
        builder.append("  → Minimum Investment: ${it.minimumInvestment}\n")
        builder.append("  → Time Horizon: ${it.recommendedTimeHorizon}\n")
        builder.append("  → Details: ${it.detailedDescription}\n\n")
    }

    InvestmentData.absoluteReturnOptions.forEach {
        builder.append("• ${it.name} (${it.type}): ${it.description}\n")
        builder.append("  → Risk: ${it.riskLevel}\n")
        builder.append("  → Return Potential: ${it.returnPotential}\n")
        builder.append("  → Liquidity: ${it.liquidityLevel}\n")
        builder.append("  → Tax Benefits: ${it.taxBenefits}\n")
        builder.append("  → Minimum Investment: ${it.minimumInvestment}\n")
        builder.append("  → Time Horizon: ${it.recommendedTimeHorizon}\n")
        builder.append("  → Details: ${it.detailedDescription}\n\n")
    }

    // Section 2: Mutual Fund Categories
    builder.append("=== Mutual Fund Categories ===\n\n")
    MutualFundCategories.categories.forEach {
        builder.append("=== Top Mutual Fund in this Categories which is {${it.name}} is the TOP Preforming mutual fund that you have you return if someone asked about this ${it.name} ===\n\n")
        builder.append("List all the mutual fund details according to their code W.R.T ${it.name}")
        mutualFundCategory(
            category = it,
            selectedSubcategory = null
        ).forEach { code ->
            builder.append("Mutual Fund Scheme_code = $code")
            builder.append("Mutual fund scheme_name: ${fundMetaList.find { it.scheme_code == code }?.scheme_name}")
            Log.d("Scheme name", fundMetaList.find { it.scheme_code == code }?.scheme_name.toString())
        }
    }
    builder.append("Give me scheme_name for each mutual fund in your result.")

    // Section 3: Debt Fund Subcategories
    builder.append("\n=== Debt Fund Subcategories ===\n\n")
    DebtFundSubcategories.subcategories.forEach {
        builder.append(
            "• ${it.name}: ${it.description} (Scheme Codes: ${
                it.schemeCodes.joinToString(
                    ", "
                )
            })\n"
        )
    }

    return builder.toString()
}
