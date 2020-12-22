package com.afaneca.myfin.open.login.data

import androidx.annotation.Keep
import com.afaneca.myfin.network.BaseResponse

/**
 * Created by me on 21/12/2020
 */
@Keep
data class AttemptLoginResponse(
    val sessionkey: String?,
    val username: String?,
    val trustlimit: Int?
) : BaseResponse()