package com.afaneca.myfin.closed.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afaneca.myfin.databinding.FragmentTransactionDetailsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by me on 03/07/2021
 */
class TransactionDetailsBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentTransactionDetailsBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionDetailsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
}