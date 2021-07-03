package com.afaneca.myfin.closed.transactions.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.databinding.FragmentTransactionDetailsBottomSheetBinding
import com.afaneca.myfin.utils.formatMoney
import com.afaneca.myfin.utils.setupAmountStyle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by me on 03/07/2021
 */
class TransactionDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentTransactionDetailsBottomSheetBinding

    companion object {
        private final val BUNDLE_IN_TRX_OBJECT_TAG = "BUNDLE_IN_TRX_OBJECT_TAG"

        fun newInstance(trxObj: MyFinTransaction): TransactionDetailsBottomSheetFragment {
            val args = Bundle()
            args.putSerializable(BUNDLE_IN_TRX_OBJECT_TAG, trxObj)
            val fragment = TransactionDetailsBottomSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }


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
        if (arguments != null) {
            var trx =
                requireArguments().getSerializable(BUNDLE_IN_TRX_OBJECT_TAG) as MyFinTransaction
            binding.trxObj = trx
            binding.formattedAmount = formatMoney(trx.amount.toDoubleOrNull() ?: 0.00)
            setupAmountStyle(trx.type, binding.amountTv)
        }

        /*dialog?.setOnShowListener {
            var bottomSheet: FrameLayout =
                (dialog as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }*/
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