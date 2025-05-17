package com.investwise_india.chatbot

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface MutualFundApi {
    @GET("mf/{code}")
    suspend fun getLatestData(@Path("code") code: Int): MutualFundData
}

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mfapi.in/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: MutualFundApi by lazy {
        retrofit.create(MutualFundApi::class.java)
    }
}

@Serializable
data class MutualFundData(
    val data: List<FundEntry>,
    val meta: FundMeta
)

@Serializable
data class FundMeta(
    val fund_house: String,
    val scheme_type: String,
    val scheme_category: String,
    val scheme_code: Int,
    val scheme_name: String,
    val isin_growth: String,
    val isin_div_reinvestment: String?
)

@Serializable
data class FundEntry(
    val date: String,
    val nav: String
)
