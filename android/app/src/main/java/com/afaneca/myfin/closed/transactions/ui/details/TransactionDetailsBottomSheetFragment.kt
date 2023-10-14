package com.afaneca.myfin.closed.transactions.ui.details

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.databinding.FragmentTransactionDetailsBottomSheetBinding
import com.afaneca.myfin.utils.formatMoney
import com.afaneca.myfin.utils.parseStringToBoolean
import com.afaneca.myfin.utils.safeNavigate
import com.afaneca.myfin.utils.setupAmountStyle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Created by me on 03/07/2021
 */

@AndroidEntryPoint
class TransactionDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTransactionDetailsBottomSheetBinding
    private val viewModel: TransactionDetailsViewModel by viewModels()
    private val args: TransactionDetailsBottomSheetFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionDetailsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.trx.let { trx ->
            viewModel.setTransactionId(trx.transactionId)
            binding.trxObj = trx
            binding.formattedAmount = formatMoney(trx.amount.toDoubleOrNull() ?: 0.00)
            setupAmountStyle(trx.type, binding.amountTv)
            binding.essentialInclude.root.isVisible = parseStringToBoolean(trx.isEssential)
            binding.editIv.setOnClickListener { viewModel.triggerEvent(TransactionDetailsContract.Event.EditTransactionClicked) }
            binding.deleteIv.setOnClickListener { showRemoveTransactionConfirmationDialog() }
        }

        observeEffects()
    }

    private fun observeEffects() {
        viewModel.effect.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { effect ->
                when (effect) {
                    is TransactionDetailsContract.Effect.NavigateToEditTransaction -> goToEditTransactionBottomSheet(
                        args.trx
                    )

                    is TransactionDetailsContract.Effect.NavigateToTransactionsList -> goToTransactionsList()

                    is TransactionDetailsContract.Effect.ShowError -> showError(effect.message)
                    else -> {}
                }
            }.launchIn(lifecycleScope)
    }

    private fun goToTransactionsList() {
        Toast.makeText(
            requireContext(),
            getString(R.string.transaction_deleted_success_message),
            Toast.LENGTH_LONG
        ).show()

        val action = TransactionDetailsBottomSheetFragmentDirections.actionTransactionDetailsBottomSheetFragmentToTransactionsFragment()
        findNavController().safeNavigate(action)
    }

    private fun showError(message: String?) {
        Toast.makeText(
            requireContext(),
            message ?: getString(R.string.default_error_msg),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun goToEditTransactionBottomSheet(trx: MyFinTransaction) {
        val destination =
            TransactionDetailsBottomSheetFragmentDirections.actionTransactionDetailsBottomSheetFragmentToAddTransactionBottomSheetFragment(
                trx
            )
        findNavController().safeNavigate(destination)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.also {
            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)

            binding.root.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    try {
                        binding.root.viewTreeObserver.removeGlobalOnLayoutListener(this)
                        behavior.peekHeight = binding.root.height
                        view?.requestLayout()
                    } catch (e: Exception) {
                    }
                }
            })
        }
    }

    private fun showRemoveTransactionConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.confirmation_transaction_delete))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.generic_yes)) { dialog, id ->
                viewModel.triggerEvent(
                    TransactionDetailsContract.Event.RemoveTransactionClicked
                )
            }
            .setNegativeButton(getString(R.string.generic_go_back)) { dialog, id -> dialog.dismiss() }
            .create()
            .show()
    }
}