package com.afaneca.myfin.closed.budgets.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.budgets.data.BudgetsRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.databinding.FragmentBudgetsBinding

/**
 * Created by me on 07/08/2021
 */
class BudgetsFragment :
    BaseFragment<BudgetsViewModel, FragmentBudgetsBinding, BudgetsRepository>() {


    /**/
    override fun getViewModel(): Class<BudgetsViewModel> = BudgetsViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBudgetsBinding = FragmentBudgetsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): BudgetsRepository =
        BudgetsRepository(remoteDataSource.create(MyFinAPIServices::class.java), userData)

}