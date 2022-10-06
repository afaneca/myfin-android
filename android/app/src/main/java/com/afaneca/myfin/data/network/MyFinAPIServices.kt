package com.afaneca.myfin.data.network

import com.afaneca.myfin.closed.accounts.data.AccountsListResponse
import com.afaneca.myfin.closed.budgets.data.BudgetsListResponse
import com.afaneca.myfin.closed.dashboard.data.MonthlyIncomeExpensesDistributionResponse
import com.afaneca.myfin.closed.transactions.data.BudgetDetailsResponse
import com.afaneca.myfin.closed.transactions.data.LatestTransactionsListResponse
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

    // TRANSACTIONS
    @GET("trxs/")
    suspend fun getLatestTransactionsList(
        @Query("trx_limit") trxLimit: Int,
    ): LatestTransactionsListResponse

    @GET("trxs/page/{page}")
    suspend fun getTransactionsListByPage(
        @Path(value = "page", encoded = true) page: Int,
        @Query("page_size") trxLimit: Int,
    ): LatestTransactionsListResponse

    // BUDGETS
    @GET("budgets/")
    suspend fun getBudgetsList(): BudgetsListResponse

    @GET("budgets/{id}")
    suspend fun getBudgetDetails(
        @Path(value = "id", encoded = true) id: String
    ): BudgetDetailsResponse

    @FormUrlEncoded
    @PUT("budgets/{id}")
    suspend fun updateBudgetCategoryPlannedAmounts(
        @Path(value = "id", encoded = true) budgetId: String,
        @Field("category_id") catId: String,
        @Field("planned_expense") plannedExpense: String,
        @Field("planned_income") plannedIncome: String,
    ): Unit

    // ACCOUNTS
    @GET("accounts/")
    suspend fun getAccountsList(): AccountsListResponse
}