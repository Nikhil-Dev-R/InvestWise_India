package com.investwise_india.network

import com.investwise_india.model.MutualFund
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MutualFundApiService {
    @GET("mf")
    suspend fun getAllMutualFunds(): Response<List<MutualFund>>
    
    @GET("mf/{schemeCode}")
    suspend fun getMutualFundDetails(
        @Path("schemeCode") schemeCode: Int
    ): Response<MutualFundDetails>
}

data class MutualFundDetails(
    val meta: MutualFundMeta,
    val data: List<MutualFundData>
)

data class MutualFundMeta(
    val fund_house: String,
    val scheme_type: String,
    val scheme_category: String,
    val scheme_code: Int,
    val scheme_name: String,
    val isin_growth: String? = null,
    val isin_div_reinvestment: String? = null,
    val expense_ratio: Double? = null,
    val sharpe_ratio: Double? = null,
    val beta: Double? = null,
    val alpha: Double? = null,
    val standard_deviation: Double? = null,
    val sortino_ratio: Double? = null,
    val information_ratio: Double? = null,
    val tracking_error: Double? = null,
    val pe_ratio: Double? = null,
    val pb_ratio: Double? = null
)

data class MutualFundData(
    val date: String,
    val nav: String
) 