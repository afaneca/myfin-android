package com.afaneca.myfin.closed.transactions.ui.addTransaction

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afaneca.myfin.R
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.closed.transact.AddTransactionViewModel
import com.afaneca.myfin.databinding.FragmentAddTransactionBottomSheetBinding
import com.afaneca.myfin.utils.MyFinConstants
import com.afaneca.myfin.utils.parseStringToBoolean
import com.afaneca.myfin.utils.safeNavigate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditTransactionBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddTransactionBottomSheetBinding
    private val viewModel: AddTransactionViewModel by viewModels()
    private val args: AddEditTransactionBottomSheetFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTransactionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, androidx.lifecycle.Observer(::observeState))
        viewModel.triggerEvent(AddTransactionContract.Event.InitForm(args.trx))
        if (args.trx != null) binding.addBtn.text = getString(R.string.edit_transaction)
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

    private fun observeState(state: AddTransactionContract.State?) {
        binding.loadingPb.isVisible = state is AddTransactionContract.State.Loading
        when (state) {
            is AddTransactionContract.State.InitForm -> {
                setupForm(state.formData)
                state.trx?.let { fillForm(it) }
            }

            is AddTransactionContract.State.ToggleAddButton -> binding.addBtn.isEnabled =
                state.isToEnable

            is AddTransactionContract.State.ToggleEssential -> binding.essentialSwitch.isVisible =
                state.isToShow

            AddTransactionContract.State.AddTransactionSuccess -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.transaction_added_success_message),
                    Toast.LENGTH_LONG
                ).show()
                val action =
                    AddEditTransactionBottomSheetFragmentDirections
                        .actionAddTransactionBottomSheetFragmentToTransactionsFragment()
                findNavController().safeNavigate(action)
            }

            AddTransactionContract.State.Failure -> Toast.makeText(
                requireContext(),
                getString(R.string.default_error_msg),
                Toast.LENGTH_LONG
            ).show()

            AddTransactionContract.State.Loading -> { /* no-op */
            }

            is AddTransactionContract.State.ToggleAccountFrom -> {
                if (!state.isToEnable) binding.accountFromEt.setText("", false)
                binding.accountFromTil.isVisible = state.isToEnable
            }

            is AddTransactionContract.State.ToggleAccountTo -> {
                if (!state.isToEnable) binding.accountToEt.setText("", false)
                binding.accountToTil.isVisible = state.isToEnable
            }

            null -> TODO()
        }
    }

    private fun fillForm(trx: MyFinTransaction) {
        /* Essential */
        val isEssential = parseStringToBoolean(trx.isEssential)
        binding.essentialSwitch.isChecked = isEssential

        /* Type */
        binding.typeBtg.check(
            when (trx.type) {
                MyFinConstants.MYFIN_TRX_TYPE.INCOME.value -> R.id.type_income_btn
                MyFinConstants.MYFIN_TRX_TYPE.EXPENSE.value -> R.id.type_expense_btn
                else -> R.id.type_transfer_btn
            }
        )

        viewModel.triggerEvent(
            AddTransactionContract.Event.TypeSelected(
                when (binding.typeBtg.checkedButtonId) {
                    R.id.type_expense_btn -> TrxType.EXPENSE.id
                    R.id.type_transfer_btn -> TrxType.TRANSFER.id
                    R.id.type_income_btn -> TrxType.INCOME.id
                    else -> {
                        ' '
                    }
                }
            )
        )

        /* Date */
        val date = Date(trx.dateTimestamp.toLong() * 1000)
        val printFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dateEt.setText(printFormat.format(date))
        viewModel.triggerEvent(AddTransactionContract.Event.DateSelected(date.time))

        /* Amount */
        binding.amountEt.setText(trx.amount)

        /* Origin account / Destination Account */
        when (trx.type) {
            MyFinConstants.MYFIN_TRX_TYPE.INCOME.value -> {
                // Income
                binding.accountToEt.setText(
                    Pair(
                        trx.accountsAccountToId,
                        trx.accountToName
                    ).toString(), false
                )
                viewModel.triggerEvent(AddTransactionContract.Event.AccountToSelected(binding.accountToEt.text.toString()))
            }

            MyFinConstants.MYFIN_TRX_TYPE.EXPENSE.value -> {
                // Expense
                binding.accountFromEt.setText(
                    Pair(
                        trx.accountsAccountFromId,
                        trx.accountFromName
                    ).toString(), false
                )
                viewModel.triggerEvent(AddTransactionContract.Event.AccountFromSelected(binding.accountFromEt.text.toString()))
            }

            else -> {
                // Transfer
                binding.accountFromEt.setText(
                    Pair(
                        trx.accountsAccountFromId,
                        trx.accountFromName
                    ).toString(), false
                )

                binding.accountToEt.setText(
                    Pair(
                        trx.accountsAccountToId,
                        trx.accountToName
                    ).toString(), false
                )
                viewModel.triggerEvent(AddTransactionContract.Event.AccountToSelected(binding.accountToEt.text.toString()))
                viewModel.triggerEvent(AddTransactionContract.Event.AccountFromSelected(binding.accountFromEt.text.toString()))
            }
        }

        /* Category */
        binding.categoryEt.setText(
            Pair(trx.categoriesCategoryId, trx.categoryName).toString(),
            false
        )
        viewModel.triggerEvent(AddTransactionContract.Event.CategorySelected(binding.categoryEt.text.toString()))

        /* Entity */
        binding.entityEt.setText(Pair(trx.entityId, trx.entityName).toString(), false)
        viewModel.triggerEvent(AddTransactionContract.Event.EntitySelected(binding.entityEt.text.toString()))

        /* Description */
        binding.descriptionEt.setText(trx.description)
    }

    @Suppress("UNCHECKED_CAST")
    private fun setupForm(formData: AddTransactionContract.AddTransactionInitialFormData) {
        // essential switch
        binding.essentialSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.triggerEvent(AddTransactionContract.Event.EssentialToggled(isChecked))
        }

        // date picker
        setupDatePicker()

        // amount
        binding.amountEt.addTextChangedListener { text ->
            viewModel.triggerEvent(
                AddTransactionContract.Event.AmountInserted(text.toString())
            )
        }

        // account inputs
        val accountsStringList =
            formData.accounts.map { acc -> Pair(acc.accountId, acc.name).apply { } }
        binding.accountFromEt.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    accountsStringList
                )
            )
            onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    (adapter.getItem(position) as? Pair<String, String>)?.first?.let {
                        viewModel.triggerEvent(AddTransactionContract.Event.AccountFromSelected(it))
                    }
                }
        }
        binding.accountToEt.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    accountsStringList
                )
            )

            onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    (adapter.getItem(position) as? Pair<String, String>)?.first?.let {
                        viewModel.triggerEvent(AddTransactionContract.Event.AccountToSelected(it))
                    }
                }
        }

        // category input
        val categoriesStringList = formData.categories.map { cat -> Pair(cat.categoryId, cat.name) }
        binding.categoryEt.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    categoriesStringList
                )
            )
            onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    (adapter.getItem(position) as? Pair<String, String>)?.first?.let {
                        viewModel.triggerEvent(AddTransactionContract.Event.CategorySelected(it))
                    }
                }
        }

        // entity input
        val entitiesStringList =
            formData.entities.map { entity -> Pair(entity.entityId, entity.name) }
        binding.entityEt.apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    entitiesStringList
                )
            )

            onItemClickListener =
                OnItemClickListener { _, _, position, _ ->
                    (adapter.getItem(position) as? Pair<String, String>)?.first?.let {
                        viewModel.triggerEvent(AddTransactionContract.Event.EntitySelected(it))
                    }
                }
        }

        // description
        binding.descriptionEt.addTextChangedListener { text ->
            viewModel.triggerEvent(
                AddTransactionContract.Event.DescriptionChanged(text.toString())
            )
        }

        // type
        setupTypeSelector(formData.types)

        // add btn
        binding.addBtn.setOnClickListener { viewModel.triggerEvent(AddTransactionContract.Event.AddEditTransactionClick) }
    }

    private fun setupTypeSelector(types: List<AddTransactionContract.Type>) {
        // for now, we are ignoring dynamic types
        binding.typeBtg.apply {
            addOnButtonCheckedListener { group, checkedId, isChecked ->
                viewModel.triggerEvent(
                    AddTransactionContract.Event.TypeSelected(
                        when (group.checkedButtonId) {
                            R.id.type_expense_btn -> TrxType.EXPENSE.id
                            R.id.type_transfer_btn -> TrxType.TRANSFER.id
                            R.id.type_income_btn -> TrxType.INCOME.id
                            else -> {
                                ' '
                            }
                        }
                    )
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDatePicker() {
        binding.dateEt.inputType = InputType.TYPE_NULL
        binding.dateEt.keyListener = null
        binding.dateEt.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> showDatePicker()
                else -> Unit
            }

            v?.onTouchEvent(event) ?: true
        }
    }


    private fun showDatePicker() {
        MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
            .also { picker ->
                picker.show(parentFragmentManager, "DATE_PICKER")
                picker.addOnPositiveButtonClickListener {
                    // set edittext input
                    val date = Date(it)
                    val printFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.dateEt.setText(printFormat.format(date))
                    viewModel.triggerEvent(AddTransactionContract.Event.DateSelected(date.time))
                }
            }
    }
}