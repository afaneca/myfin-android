package com.afaneca.myfin.data.network

import com.afaneca.myfin.closed.dashboard.data.MonthlyIncomeExpensesDistributionResponse
import com.afaneca.myfin.open.login.data.AttemptLoginResponse
import retrofit2.http.*

/**
 * Created by me on 09/12/2020
 */
interface MyFinAPIServices {

    // AUTH
    @FormUrlEncoded
    @POST("auth/")
    suspend fun attemptLogin(
        @Field("username")
        username: String,
        @Field("password")
        password: String
    ): AttemptLoginResponse

    // STATS
    @GET("stats/dashboard/month-expenses-income-distribution")
    suspend fun getMonthlyExpensesIncomeDistribution(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): MonthlyIncomeExpensesDistributionResponse
}