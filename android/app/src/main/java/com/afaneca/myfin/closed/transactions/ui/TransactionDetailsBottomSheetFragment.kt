package com.afaneca.myfin.closed.transactions.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.databinding.FragmentTransactionDetailsBottomSheetBinding
import com.afaneca.myfin.utils.formatMoney
import com.afaneca.myfin.utils.parseStringToBoolean
import com.afaneca.myfin.utils.setupAmountStyle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by me on 03/07/2021
 */
class TransactionDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTransactionDetailsBottomSheetBinding
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
            binding.trxObj = trx
            binding.formattedAmount = formatMoney(trx.amount.toDoubleOrNull() ?: 0.00)
            setupAmountStyle(trx.type, binding.amountTv)
            binding.essentialInclude.isVisible = parseStringToBoolean(trx.isEssential)
        }
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
}