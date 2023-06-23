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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        observeState()
        observeEffect()
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

    private var isFormInitialized = false
    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                /* Loading */
                binding.loadingPb.isVisible = state.isLoading

                /* CTA Button */
                binding.addBtn.isEnabled = state.isAddButtonEnabled

                /* Essential */
                binding.essentialSwitch.isVisible = state.isEssentialToggleVisible

                /* Account From */
                binding.accountFromTil.isVisible = state.isAccountFromEnabled
                if (!state.isAccountFromEnabled) binding.accountFromEt.setText("", false)

                /* Account To */
                binding.accountToTil.isVisible = state.isAccountToEnabled
                if (!state.isAccountToEnabled) binding.accountToEt.setText("", false)

                /* Form */
                state.formData?.let { data ->
                    if (!isFormInitialized) {
                        isFormInitialized = true
                        setupForm(data)
                        state.trx?.let { trx ->
                            fillForm(trx)
                        }
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun observeEffect() {
        viewModel.effect.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { effect ->
                when (effect) {
                    is AddTransactionContract.Effect.NavigateToTransactionList -> navigateToTransactionList()
                    is AddTransactionContract.Effect.ShowError -> showErrorToast(effect.errorMessage)
                    else -> Unit
                }
            }.launchIn(lifecycleScope)
    }

    private fun navigateToTransactionList() {
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

    private fun showErrorToast(errorMessage: String?) {
        Toast.makeText(
            requireContext(),
            errorMessage ?: getString(R.string.default_error_msg),
            Toast.LENGTH_LONG
        ).show()
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
                viewModel.triggerEvent(
                    AddTransactionContract.Event.AccountToSelected(
                        trx.accountsAccountToId ?: ""
                    )
                )
            }

            MyFinConstants.MYFIN_TRX_TYPE.EXPENSE.value -> {
                // Expense
                binding.accountFromEt.setText(
                    Pair(
                        trx.accountsAccountFromId,
                        trx.accountFromName
                    ).toString(), false
                )
                viewModel.triggerEvent(
                    AddTransactionContract.Event.AccountFromSelected(
                        trx.accountsAccountFromId ?: ""
                    )
                )
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
                viewModel.triggerEvent(
                    AddTransactionContract.Event.AccountToSelected(
                        trx.accountsAccountToId ?: ""
                    )
                )
                viewModel.triggerEvent(
                    AddTransactionContract.Event.AccountFromSelected(
                        trx.accountsAccountFromId ?: ""
                    )
                )
            }
        }

        /* Category */
        if (trx.categoriesCategoryId != null && trx.categoryName != null) {
            binding.categoryEt.setText(
                Pair(trx.categoriesCategoryId, trx.categoryName).toString(),
                false
            )
            viewModel.triggerEvent(AddTransactionContract.Event.CategorySelected(trx.categoriesCategoryId))
        }

        /* Entity */
        if (trx.entityId != null && trx.entityName != null) {
            binding.entityEt.setText(Pair(trx.entityId, trx.entityName).toString(), false)
            viewModel.triggerEvent(AddTransactionContract.Event.EntitySelected(trx.entityId))
        }

        /* Description */
        if (!trx.description.isEmpty()) {
            binding.descriptionEt.setText(trx.description)
        }
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