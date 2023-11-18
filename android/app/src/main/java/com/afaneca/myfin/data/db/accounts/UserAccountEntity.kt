package com.afaneca.myfin.data.db.accounts

import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by me on 14/06/2021
 */
@Keep
@Entity
data class UserAccountEntity(

    @PrimaryKey
    @ColumnInfo(name = "account_id")
    @SerializedName("account_id")
    val accountId: Int,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String?,

    @ColumnInfo(name = "type")
    @SerializedName("type")
    val type: String?,

    @ColumnInfo(name = "description")
    @SerializedName("description")
    val description: String?,

    @ColumnInfo(name = "status")
    @SerializedName("status")
    val status: String?,

    @ColumnInfo(name = "color_gradient")
    @SerializedName("color_gradient")
    val color_gradient: String?,

    @ColumnInfo(name = "exclude_from_budgets")
    @SerializedName("exclude_from_budgets")
    val isToExcludeFromBudgets: Boolean?,

    @ColumnInfo(name = "balance")
    @SerializedName("balance")
    val balance: String?,

    @ColumnInfo(name = "users_user_id")
    @SerializedName("users_user_id")
    val usersUserId: Int?
) : Serializable