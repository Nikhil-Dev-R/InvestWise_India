package com.investwise_india.model

enum class InvestmentType {
    FIXED_RETURN,
    ABSOLUTE_RETURN
}

data class InvestmentOption(
    val name: String,
    val type: InvestmentType,
    val description: String,
    val detailedDescription: String,
    val riskLevel: String,
    val returnPotential: String,
    val liquidityLevel: String,
    val taxBenefits: String,
    val minimumInvestment: String,
    val recommendedTimeHorizon: String
)

// Expanded sample data for investment options with comprehensive details.
object InvestmentData {

    val fixedReturnOptions = listOf(
        InvestmentOption(
            name = "Fixed Deposit (FD)",
            type = InvestmentType.FIXED_RETURN,
            description = "A secure deposit instrument by banks offering fixed interest over a term.",
            detailedDescription = "Fixed Deposits (FDs) are one of the safest investment options provided by banks and NBFCs. They allow you to lock in a lump sum for a predetermined period—from a few days to several years—with a guaranteed fixed interest rate. FDs are popular for their capital protection, predictable income, and flexibility in tenure selection. Most banks offer both cumulative and non-cumulative options, the latter providing periodic interest payouts. Although premature withdrawals are possible, they typically incur a penalty which may reduce the overall return. FDs can also be used as collateral for loans, offering liquidity without breaking the deposit. Note that interest earned is taxable as per your applicable tax slab.",
            riskLevel = "Low",
            returnPotential = "5-7% p.a. (varies by bank and tenure)",
            liquidityLevel = "Medium (premature withdrawal available with penalty)",
            taxBenefits = "No direct tax benefits; interest is taxable",
            minimumInvestment = "₹1,000",
            recommendedTimeHorizon = "6 months - 10 years"
        ),
        InvestmentOption(
            name = "Public Provident Fund (PPF)",
            type = InvestmentType.FIXED_RETURN,
            description = "A government-backed long-term savings scheme offering tax benefits and compounded returns.",
            detailedDescription = "The Public Provident Fund (PPF) is a long-term savings instrument backed by the Government of India. With a lock-in period of 15 years, it offers compounded annual returns that are revisited periodically by the government. PPF is popular among conservative investors due to its safety, tax-free interest (EEE status), and eligibility for deductions under Section 80C. Although partial withdrawals are allowed after the 7th year, the scheme is best suited for long-term wealth accumulation and retirement planning.",
            riskLevel = "Very Low",
            returnPotential = "7-8% p.a.",
            liquidityLevel = "Low (15-year lock-in with limited partial withdrawals)",
            taxBenefits = "Exempt-Exempt-Exempt (EEE) under Section 80C",
            minimumInvestment = "₹500 per year",
            recommendedTimeHorizon = "15 years"
        ),
        InvestmentOption(
            name = "Kisan Vikas Patra (KVP)",
            type = InvestmentType.FIXED_RETURN,
            description = "A government-backed savings certificate designed to double your investment over a fixed period.",
            detailedDescription = "Kisan Vikas Patra (KVP) is a small savings instrument offered by the Government of India, available at post offices and select banks. The scheme aims to double your investment over approximately 10 years and 4 months by offering a fixed interest rate that is periodically revised. KVP is ideal for conservative investors seeking predictable, government-guaranteed returns. However, it does not offer any tax benefits and the interest income is taxable. While the certificate is primarily meant to be held until maturity, there are limited provisions for premature withdrawal under exceptional circumstances.",
            riskLevel = "Very Low",
            returnPotential = "Approximately 6.9-7.5% p.a.",
            liquidityLevel = "Low to Medium (partial withdrawal possible after lock-in period)",
            taxBenefits = "No tax benefits; interest is taxable",
            minimumInvestment = "₹1,000",
            recommendedTimeHorizon = "10+ years"
        ),
        InvestmentOption(
            name = "Government Bonds",
            type = InvestmentType.FIXED_RETURN,
            description = "Debt securities issued by the government that offer steady income and capital preservation.",
            detailedDescription = "Government Bonds are fixed-income instruments issued by the central or state governments to fund public projects. They provide a regular stream of income through coupon payments and return the principal at maturity. With minimal credit risk due to sovereign backing, these bonds offer relatively low but stable returns (typically between 6% and 8% per annum). They can be held until maturity or traded in the secondary market for liquidity. While they are safe, the returns may be lower than those from market-linked investments, and the interest income is taxable.",
            riskLevel = "Low",
            returnPotential = "6-8% p.a.",
            liquidityLevel = "Medium (tradable in secondary markets)",
            taxBenefits = "Interest is taxable; some bonds may have specific tax provisions",
            minimumInvestment = "₹10,000 (varies by issuance)",
            recommendedTimeHorizon = "5-15 years"
        ),
        InvestmentOption(
            name = "National Savings Certificate (NSC)",
            type = InvestmentType.FIXED_RETURN,
            description = "A government-backed savings certificate offering fixed returns with tax benefits over a 5-year term.",
            detailedDescription = "The National Savings Certificate (NSC) is a popular savings scheme issued by the Indian government with a fixed tenure of 5 years. It is designed for conservative investors looking for a secure, low-risk investment option that also offers tax benefits under Section 80C. Interest on NSC is compounded annually but payable at maturity, providing a lump-sum return. The scheme is ideal for building a corpus for future financial needs, though its liquidity is limited since premature withdrawal is generally not allowed.",
            riskLevel = "Very Low",
            returnPotential = "Approximately 6.8-7.7% p.a.",
            liquidityLevel = "Low (locked for 5 years)",
            taxBenefits = "Eligible for tax deduction under Section 80C",
            minimumInvestment = "₹1,000 (in multiples of ₹100)",
            recommendedTimeHorizon = "5 years"
        ),
        InvestmentOption(
            name = "Gold & Silver",
            type = InvestmentType.FIXED_RETURN,
            description = "Precious metals that serve as a hedge against inflation and provide portfolio diversification.",
            detailedDescription = "Investing in Gold and Silver is a time-tested strategy for wealth preservation. These precious metals not only hedge against inflation and currency depreciation but also provide diversification to an investment portfolio. Investors can choose from various forms of investment: physical bullion, Gold ETFs, or Sovereign Gold Bonds (SGBs). While the returns on gold and silver can be volatile, the historical average has ranged between 8% and 12% per annum. The liquidity is generally high since these assets can be readily sold in the market; however, they do not typically offer periodic income. Tax treatment depends on the form of investment.",
            riskLevel = "Medium",
            returnPotential = "8-12% p.a. (historical average)",
            liquidityLevel = "High (readily marketable)",
            taxBenefits = "Long-term capital gains tax with indexation benefits",
            minimumInvestment = "Varies",
            recommendedTimeHorizon = "3-5 years"
        )
    )

    val absoluteReturnOptions = listOf(
        InvestmentOption(
            name = "Stocks",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Equity shares in publicly traded companies offering high growth potential with significant market risk.",
            detailedDescription = "Investing in Stocks means buying ownership shares of companies listed on stock exchanges. Stocks have the potential to generate high returns through capital appreciation and dividends, but they come with significant volatility and risk. The value of stocks is influenced by company performance, market sentiment, and macroeconomic factors. A well-diversified equity portfolio can help mitigate individual stock risks. Stocks are ideal for investors with a high risk tolerance who are seeking long-term wealth creation. However, it requires constant monitoring and a willingness to ride out market fluctuations.",
            riskLevel = "High",
            returnPotential = "12-15% p.a. (historical average)",
            liquidityLevel = "High (traded on stock exchanges)",
            taxBenefits = "Capital gains tax applies; short-term and long-term rates differ",
            minimumInvestment = "Varies (dependent on share price)",
            recommendedTimeHorizon = "5+ years"
        ),
        InvestmentOption(
            name = "Mutual Funds",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Pooled investment vehicles that provide diversification and professional management.",
            detailedDescription = "Mutual Funds pool funds from multiple investors to invest in a diversified portfolio of stocks, bonds, or other securities. Managed by professional fund managers, they aim to achieve specific financial goals based on the fund's investment strategy. Options include equity funds, debt funds, hybrid funds, and tax-saving ELSS funds. They are suitable for investors who prefer a hands-off approach, offering diversification and lower risk compared to individual stocks. However, fees and expense ratios can impact net returns, and the performance is inherently tied to market conditions.",
            riskLevel = "Low to High (varies by fund type)",
            returnPotential = "8-15% p.a. (depending on the fund’s focus)",
            liquidityLevel = "Medium to High (subject to exit loads)",
            taxBenefits = "ELSS funds offer tax benefits under Section 80C; otherwise taxed as per capital gains norms",
            minimumInvestment = "₹500 (especially via SIP)",
            recommendedTimeHorizon = "3-7+ years"
        ),
        InvestmentOption(
            name = "Cryptocurrency",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Digital currencies using blockchain technology, known for high volatility and potential exponential returns.",
            detailedDescription = "Cryptocurrencies represent a new asset class characterized by decentralization and high volatility. Popular digital currencies such as Bitcoin, Ethereum, and various altcoins operate on blockchain technology, ensuring secure, peer-to-peer transactions. While the potential for high returns exists, cryptocurrencies are extremely volatile and subject to regulatory uncertainties, market sentiment, and technological risks. They are traded 24/7 on various exchanges, offering high liquidity. Cryptocurrency investments are best suited for risk-tolerant investors who can handle rapid price fluctuations and are prepared for the possibility of significant losses.",
            riskLevel = "Very High",
            returnPotential = "Highly volatile; potential for exponential gains",
            liquidityLevel = "High (active 24/7 trading)",
            taxBenefits = "Taxed as per capital gains; no specific exemptions",
            minimumInvestment = "Varies (can start with a small amount)",
            recommendedTimeHorizon = "3-5+ years (for risk-tolerant investors)"
        ),
        InvestmentOption(
            name = "Exchange Traded Funds (ETFs)",
            type = InvestmentType.ABSOLUTE_RETURN,
            description = "Funds that track an index or asset class and trade like stocks on exchanges.",
            detailedDescription = "Exchange Traded Funds (ETFs) combine the diversification benefits of mutual funds with the flexibility of stock trading. They are designed to track the performance of a specific index, commodity, or basket of assets. ETFs offer lower expense ratios compared to traditional mutual funds and provide intraday liquidity as they trade on stock exchanges. Their structure makes them tax-efficient and cost-effective, appealing to investors who want exposure to a broad market segment with a passive investment strategy. ETFs are well-suited for both short-term trading and long-term investment, depending on the investor's goals and market conditions.",
            riskLevel = "Medium",
            returnPotential = "8-12% p.a. (depending on underlying assets)",
            liquidityLevel = "High (traded throughout the day)",
            taxBenefits = "Tax efficient; capital gains taxation similar to stocks",
            minimumInvestment = "Price of one ETF unit (varies by ETF)",
            recommendedTimeHorizon = "3-5+ years"
        )
    )

    val allOptions = fixedReturnOptions + absoluteReturnOptions
}
