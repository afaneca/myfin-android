package com.afaneca.myfin.data.model

import androidx.annotation.Keep
import com.afaneca.myfin.data.db.accounts.UserAccountEntity
import com.afaneca.myfin.data.network.BaseResponse

/**
 * Created by me on 21/12/2020
 */
@Keep
data class AttemptLoginResponse(
    val sessionkey: String?,
    val username: String?,
    val trustlimit: Int?,
    val accounts: List<UserAccountEntity>?
) : BaseResponse()