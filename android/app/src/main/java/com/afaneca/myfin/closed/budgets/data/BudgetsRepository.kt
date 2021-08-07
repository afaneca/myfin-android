package com.afaneca.myfin.closed.budgets.data

import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.network.BaseRepository
import com.afaneca.myfin.data.network.MyFinAPIServices

class BudgetsRepository(
    private val api: MyFinAPIServices,
    private val userData: UserDataManager
) : BaseRepository() {

}
