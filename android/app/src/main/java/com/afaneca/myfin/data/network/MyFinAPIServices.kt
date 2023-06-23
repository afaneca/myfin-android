package com.afaneca.myfin.data.network

import com.afaneca.myfin.closed.accounts.data.AccountsListResponse
import com.afaneca.myfin.closed.budgets.data.BudgetsListResponse
import com.afaneca.myfin.closed.dashboard.data.MonthlyIncomeExpensesDistributionResponse
import com.afaneca.myfin.closed.transactions.data.AddTransactionStep0Response
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

    @POST("trxs/step0")
    suspend fun addTransactionStep0(): AddTransactionStep0Response

    @FormUrlEncoded
    @POST("trxs/step1")
    suspend fun addTransactionStep1(
        @Field("amount") amount: String,
        @Field("type") type: Char,
        @Field("description") description: String? = null,
        @Field("entity_id") entityId: String? = null,
        @Field("account_from_id") accountFromId: String? = null,
        @Field("account_to_id") accountToId: String? = null,
        @Field("category_id") categoryId: String? = null,
        @Field("date_timestamp") dateTimestamp: Long,
        @Field("is_essential") isEssential: Boolean,
    ): Unit

    @FormUrlEncoded
    @PUT("trxs/")
    suspend fun updateTransaction(
        @Field("transaction_id") transactionId: Int,
        @Field("new_amount") amount: String,
        @Field("new_type") type: Char,
        @Field("new_description") description: String? = null,
        @Field("new_entity_id") entityId: String? = null,
        @Field("new_account_from_id") accountFromId: String? = null,
        @Field("new_account_to_id") accountToId: String? = null,
        @Field("new_category_id") categoryId: String? = null,
        @Field("new_date_timestamp") dateTimestamp: Long,
        @Field("new_is_essential") isEssential: Boolean,
    ) : Unit

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