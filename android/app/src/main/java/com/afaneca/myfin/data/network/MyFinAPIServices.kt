package com.afaneca.myfin.data.network

import com.afaneca.myfin.data.model.AddTransactionStep0ResponseDto
import com.afaneca.myfin.data.model.AttemptLoginResponseDto
import com.afaneca.myfin.data.model.AutoCatResponseDto
import com.afaneca.myfin.data.model.FilteredResultsByPageDto
import com.afaneca.myfin.data.model.TransactionDto
import retrofit2.http.*

/**
 * Created by me on 09/12/2020
 */
interface MyFinApiServices {

    /*region Auth*/
    @FormUrlEncoded
    @POST("auth/")
    suspend fun attemptLogin(
        @Field("username")
        username: String,
        @Field("password")
        password: String,
    ): AttemptLoginResponseDto
    /*endregion*/

    /*region Transactions*/
    @GET("trxs/filteredByPage/{page}")
    suspend fun getTransactionsListByPage(
        @Path(value = "page", encoded = true) page: Int,
        @Query("page_size") pageSize: Int,
        @Query("query") query: String?,
    ): FilteredResultsByPageDto<TransactionDto>

    @POST("trxs/step0")
    suspend fun addTransactionStep0(): AddTransactionStep0ResponseDto

    @FormUrlEncoded
    @POST("trxs/step1")
    suspend fun createTransaction(
        @Field("amount") amount: String,
        @Field("type") type: String,
        @Field("description") description: String?,
        @Field("entity_id") entityId: Int?,
        @Field("account_from_id") accountFromId: Int?,
        @Field("account_to_id") accountToId: Int?,
        @Field("category_id") categoryId: Int?,
        @Field("date_timestamp") dateTimestamp: Long,
        @Field("is_essential") isEssential: Boolean,
    )

    @FormUrlEncoded
    @POST("trxs/auto-cat-trx")
    suspend fun autoCategorizeTransaction(
        @Field("description") description: String,
        @Field("amount") amount: String,
        @Field("account_from_id") accountFromId: Int,
        @Field("account_to_id") accountToId: Int,
    ): AutoCatResponseDto
    /*endregion*/

    // STATS
    /*@GET("stats/dashboard/month-expenses-income-distribution")
    suspend fun getMonthlyExpensesIncomeDistribution(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): MonthlyIncomeExpensesDistributionResponse

    // TRANSACTIONS
    @GET("trxs/filteredByPage/{page}")
    suspend fun getTransactionsListByPage(
        @Path(value = "page", encoded = true) page: Int,
        @Query("page_size") trxLimit: Int,
        @Query("query") query: String?
    ): FilteredResultsByPage<MyFinTransaction>

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
    ): Unit


    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "trxs/", hasBody = true)
    suspend fun deleteTransaction(
        @Field("transaction_id") transactionId: Int,
    ): Unit

    // BUDGETS
    @GET("budgets/filteredByPage/{page}")
    suspend fun getBudgetsList(
        @Path(value = "page", encoded = true) page: Int,
        @Query("page_size") trxLimit: Int,
    ): FilteredResultsByPage<MyFinBudget>

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
    suspend fun getAccountsList(): AccountsListResponse*/
}
