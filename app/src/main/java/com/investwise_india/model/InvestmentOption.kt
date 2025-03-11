package com.investwise_india.model

enum class InvestmentType {
    FIXED_RETURN,
    ABSOLUTE_RETURN
}

data class InvestmentOption(
    val name: String,
    val type: InvestmentType,
    val description: String,
    val riskLevel: String,
    val returnPotential: String,
    val liquidityLevel: String,
    val taxBenefits: String,
    val minimumInvestment: String,
    val recommendedTimeHorizon: String
)

// Sample data for investment options
object InvestmentData {
    val fixedReturnOptions = listOf(
        InvestmentOption(
            name = "Fixed Deposit (FD)",
            type = InvestmentType.FIXED_RETURN,
            description = "A financial instrument provided by banks which provides investors a higher rate of interest than a regular savings account.",
            riskLevel = "Low",
            returnPotential = "5-7% p.a.",
            liquidityLevel = "Medium (Premature withdrawal available with penalty)",
            taxBenefits = "No tax benefits, interest is taxable",
            minimumInvestment = "₹1,000",
            recommendedTimeHorizon = "6 months - 10 years"
        ),
        InvestmentOption(
            name = "Public Provident Fund (PPF)",
            type = InvestmentType.FIXED_RETURN,
            description = "A long-term savings scheme backed by the Government of India that offers safety with attractive interest rates.",
            riskLevel = "Very Low",
            returnPotential = "7-8% p.a.",
            liquidityLevel = "Low (15-year lock-in period, partial withdrawal allowed after 7 years)",
            taxBenefits = "EEE (Exempt-Exempt-Exempt) status under Section 80C",
            minimumInvestment = "₹500 per year",
            recommendedTimeHorizon = "15 years"
        ),
        InvestmentOption(
            name = "Kisan Vikas Patra (KVP)",
            type = InvestmentType.FIXED_RETURN,
            description = "A small savings certificate scheme that doubles your investment in about 10 years.",
            riskLevel = "Very Low",
            returnPotential = "6.9% p.a. (doubles in ~10 years)",
            liquidityLevel = "Medium (Can be encashed after 2.5 years)",
            taxBenefits = "No tax benefits, interest is taxable",
            minimumInvestment = "₹1,000",
            recommendedTimeHorizon = "10 years"
        ),
        InvestmentOption(
            name = "Government Bonds",
            type = InvestmentType.FIXED_RETURN,
            description = "Debt securities issued by the government to support government spending and obligations.",
            riskLevel = "Low",
            returnPotential = "6-8% p.a.",
            liquidityLevel = "Medium (Can be traded in secondary market)",
            taxBenefits = "Interest is taxable",
            minimumInvestment = "₹10,000",
            recommendedTimeHorizon = "5-15 years"
        ),
        InvestmentOption(
            name = "(NSC)",
            type = InvestmentType.FIXED_RETURN,
            description = "Nsc known as National Savings Certificate. A fixed income investment scheme to promote small savings with guaranteed returns.",
            riskLevel = "Very Low",
            returnPotential = "6.8% p.a.",
            liquidityLevel = "Low (5-year lock-in period)",
            taxBenefits = "Investment qualifies for tax deduction under Section 80C",
            minimumInvestment = "₹1,000",
            recommendedTimeHorizon = "5 years"
        ),
        InvestmentOption(
            name = "Gold & Silver",
            type = InvestmentType.FIXED_RETURN,
            description = "Precious metals that serve as a hedge against inflation and economic uncertainty.",
            riskLevel = "Medium",
            returnPotential = "8-12% p.a. (historical average)",
            liquidityLevel = "High (Can be sold easily)",
            taxBenefits = "Long-term capital gains tax with indexation benefits",
            minimumInvestment = "Varies",
            recommendedTimeHorizon = "3-5 years"
        )
    )

    val absoluteReturnOptions = listOf(
        InvestmentOption(
            name = "Stocks",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Ownership shares in publicly traded companies that can provide capital appreciation and dividends.",
            riskLevel = "High",
            returnPotential = "12-15% p.a. (historical average)",
            liquidityLevel = "High (Can be sold during market hours)",
            taxBenefits = "STCG (15%), LTCG (10% above ₹1 lakh)",
            minimumInvestment = "Varies",
            recommendedTimeHorizon = "5+ years"
        ),
        InvestmentOption(
            name = "Mutual Funds",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Investment vehicles that pool money from many investors to purchase securities.",
            riskLevel = "Low to High (depends on fund type)",
            returnPotential = "8-15% p.a. (depends on fund type)",
            liquidityLevel = "Medium to High (depends on fund type)",
            taxBenefits = "ELSS funds qualify for tax deduction under Section 80C",
            minimumInvestment = "₹500 (SIP)",
            recommendedTimeHorizon = "3-7+ years"
        ),
        InvestmentOption(
            name = "Cryptocurrency",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Digital or virtual currencies that use cryptography for security and operate on decentralized networks.",
            riskLevel = "Very High",
            returnPotential = "Highly volatile, potentially high returns",
            liquidityLevel = "High (Can be traded 24/7)",
            taxBenefits = "Taxed as per income tax slab (30% flat tax + 1% TDS)",
            minimumInvestment = "₹100",
            recommendedTimeHorizon = "3-5+ years"
        ),
        InvestmentOption(
            name = "Exchange Traded Funds (ETFs)",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Investment funds traded on stock exchanges that hold assets like stocks, commodities, or bonds.",
            riskLevel = "Medium",
            returnPotential = "8-12% p.a. (depends on underlying assets)",
            liquidityLevel = "High (Can be sold during market hours)",
            taxBenefits = "Similar to stocks or mutual funds depending on the ETF type",
            minimumInvestment = "Price of 1 unit (varies)",
            recommendedTimeHorizon = "3-5+ years"
        ),
        InvestmentOption(
            name = "Commodities",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Raw materials or primary agricultural products that can be bought and sold, such as gold, silver, oil, and agricultural products.",
            riskLevel = "High",
            returnPotential = "Variable (depends on market conditions)",
            liquidityLevel = "Medium to High",
            taxBenefits = "Taxed as per income tax slab",
            minimumInvestment = "Varies by commodity",
            recommendedTimeHorizon = "1-5+ years"
        )
    )

    val allOptions = fixedReturnOptions + absoluteReturnOptions
} 