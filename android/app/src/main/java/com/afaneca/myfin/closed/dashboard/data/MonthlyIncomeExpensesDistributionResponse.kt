package com.afaneca.myfin.closed.dashboard.data

import androidx.annotation.Keep
import com.afaneca.myfin.base.objects.MyFinCategory
import com.afaneca.myfin.data.network.BaseResponse
import com.google.gson.annotations.SerializedName

@Keep
class MonthlyIncomeExpensesDistributionResponse(
    @SerializedName("categories")
    val categories: List<MyFinCategory>
) : BaseResponse()
