package com.afaneca.myfin.base.objects


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class MyFinAccount(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("balance")
    val balance: String,
    @SerializedName("color_gradient")
    val colorGradient: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("exclude_from_budgets")
    val excludeFromBudgets: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("users_user_id")
    val usersUserId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyFinAccount

        if (accountId != other.accountId) return false
        if (balance != other.balance) return false
        if (colorGradient != other.colorGradient) return false
        if (description != other.description) return false
        if (excludeFromBudgets != other.excludeFromBudgets) return false
        if (name != other.name) return false
        if (status != other.status) return false
        if (type != other.type) return false
        if (usersUserId != other.usersUserId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = accountId.hashCode()
        result = 31 * result + balance.hashCode()
        result = 31 * result + colorGradient.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + excludeFromBudgets.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + usersUserId.hashCode()
        return result
    }

    fun isActive() = this.status == "Ativa"
}