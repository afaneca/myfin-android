package com.afaneca.myfin.closed.budgets.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.budgets.data.BudgetsRepository
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.databinding.FragmentBudgetDetailsBinding

/**
 * Created by me on 07/08/2021
 */
class BudgetDetailsFragment :
    BaseFragment<BudgetDetailsViewModel, FragmentBudgetDetailsBinding, BudgetsRepository>() {
    private val args: BudgetDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObservers()
        getBudgetDetails()
    }

    private fun bindObservers() {

    }

    private fun getBudgetDetails() {

    }

    /**/
    override fun getViewModel(): Class<BudgetDetailsViewModel> = BudgetDetailsViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBudgetDetailsBinding =
        FragmentBudgetDetailsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): BudgetsRepository =
        BudgetsRepository(remoteDataSource.create(MyFinAPIServices::class.java), userData)
}