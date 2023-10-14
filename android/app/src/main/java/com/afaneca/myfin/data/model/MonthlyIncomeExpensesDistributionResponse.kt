package com.afaneca.myfin.data.model

import androidx.annotation.Keep
import com.afaneca.myfin.base.objects.MyFinCategory
import com.afaneca.myfin.data.network.BaseResponse
import com.google.gson.annotations.SerializedName

@Keep
class MonthlyIncomeExpensesDistributionResponse(
    @SerializedName("categories")
    val categories: List<MyFinCategory>,
    @SerializedName("last_update_timestamp")
    val lastUpdateTimestamp: Long
) : BaseResponse() 
