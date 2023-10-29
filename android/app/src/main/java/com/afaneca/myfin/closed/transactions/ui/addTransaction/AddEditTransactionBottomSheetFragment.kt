package com.afaneca.myfin.closed.transactions.ui.addTransaction

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afaneca.myfin.R
import com.afaneca.myfin.base.components.searchableSelector.SearchableListItem
import com.afaneca.myfin.base.components.searchableSelector.SearchableSelectorContract
import com.afaneca.myfin.base.components.searchableSelector.SearchableSelectorViewModel
import com.afaneca.myfin.base.objects.MyFinTransaction
import com.afaneca.myfin.databinding.FragmentAddTransactionBottomSheetBinding
import com.afaneca.myfin.utils.MyFinConstants
import com.afaneca.myfin.utils.parseStringToBoolean
import com.afaneca.myfin.utils.safeNavigate
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.take
import java.text.SimpleDateFormat
import java.util.*


const val BOTTOM_SHEET_CATEGORY_SELECT_ID = "BOTTOM_SHEET_CATEGORY_SELECT_ID"
const val BOTTOM_SHEET_ENTITY_SELECT_ID = "BOTTOM_SHEET_ENTITY_SELECT_ID"
const val BOTTOM_SHEET_ACCOUNT_FROM_SELECT_ID = "BOTTOM_SHEET_ACCOUNT_FROM_SELECT_ID"
const val BOTTOM_SHEET_ACCOUNT_TO_SELECT_ID = "BOTTOM_SHEET_ACCOUNT_TO_SELECT_ID"

@AndroidEntryPoint
class AddEditTransactionBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddTransactionBottomSheetBinding
    private val viewModel: AddTransactionViewModel by viewModels()
    private val selectorViewModel: SearchableSelectorViewModel by activityViewModels()
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
        observeSelectorState()
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

    private var isFormInitialized = false
    private fun observeState() {
        viewModel.state.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                /* Loading */
                binding.loadingPb.root.isVisible = state.isLoading

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

    private fun observeSelectorState() {
        selectorViewModel.subscribeToState().flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { selectorState ->
                when (selectorState) {
                    is SearchableSelectorContract.State.ItemSelected -> {
                        when (selectorState.instanceId) {
                            BOTTOM_SHEET_CATEGORY_SELECT_ID -> {
                                viewModel.triggerEvent(
                                    AddTransactionContract.Event.CategorySelected(selectorState.item.id)
                                )
                                binding.categoryEt.setText(selectorState.item.name, false)
                            }

                            BOTTOM_SHEET_ENTITY_SELECT_ID -> {
                                viewModel.triggerEvent(
                                    AddTransactionContract.Event.EntitySelected(selectorState.item.id)
                                )
                                binding.entityEt.setText(selectorState.item.name, false)
                            }

                            BOTTOM_SHEET_ACCOUNT_FROM_SELECT_ID -> {
                                viewModel.triggerEvent(
                                    AddTransactionContract.Event.AccountFromSelected(selectorState.item.id)
                                )
                                binding.accountFromEt.setText(selectorState.item.name, false)
                            }

                            BOTTOM_SHEET_ACCOUNT_TO_SELECT_ID -> {
                                viewModel.triggerEvent(
                                    AddTransactionContract.Event.AccountToSelected(selectorState.item.id)
                                )
                                binding.accountToEt.setText(selectorState.item.name, false)
                            }
                        }
                    }

                    is SearchableSelectorContract.State.Init, null -> {}
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun observeEffect() {
        viewModel.effect.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { effect ->
                when (effect) {
                    is AddTransactionContract.Effect.NavigateToTransactionList -> navigateToTransactionList(
                        effect.isEditing
                    )

                    is AddTransactionContract.Effect.ShowError -> showErrorToast(effect.errorMessage)
                    else -> Unit
                }
            }.launchIn(lifecycleScope)
    }

    private fun navigateToTransactionList(isEditing: Boolean) {
        Toast.makeText(
            requireContext(),
            if (isEditing) getString(R.string.transaction_updated_success_message)
            else getString(R.string.transaction_added_success_message),
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
                    IdLabelPair(
                        trx.accountsAccountToId ?: "",
                        trx.accountToName ?: ""
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
                    IdLabelPair(
                        trx.accountsAccountFromId ?: "",
                        trx.accountFromName ?: ""
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
                    IdLabelPair(
                        trx.accountsAccountFromId ?: "",
                        trx.accountFromName ?: ""
                    ).toString(), false
                )

                binding.accountToEt.setText(
                    IdLabelPair(
                        trx.accountsAccountToId ?: "",
                        trx.accountToName ?: ""
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
                IdLabelPair(trx.categoriesCategoryId, trx.categoryName).toString(),
                false
            )
            viewModel.triggerEvent(AddTransactionContract.Event.CategorySelected(trx.categoriesCategoryId))
        }

        /* Entity */
        if (trx.entityId != null && trx.entityName != null) {
            binding.entityEt.setText(IdLabelPair(trx.entityId, trx.entityName).toString(), false)
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
        binding.accountFromEt.setOnClickListener {
            val action = AddEditTransactionBottomSheetFragmentDirections
                .actionAddTransactionBottomSheetFragmentToSearchableSelectorBottomSheetFragment(
                    list =
                    formData.accounts.map {
                        SearchableListItem(
                            id = it.accountId,
                            name = it.name,
                            description = it.type
                        )
                    }.toTypedArray(), R.string.account_from_label, BOTTOM_SHEET_ACCOUNT_FROM_SELECT_ID
                )
            findNavController().safeNavigate(action)
        }

        binding.accountToEt.setOnClickListener {
            val action = AddEditTransactionBottomSheetFragmentDirections
                .actionAddTransactionBottomSheetFragmentToSearchableSelectorBottomSheetFragment(
                    list =
                    formData.accounts.map {
                        SearchableListItem(
                            id = it.accountId,
                            name = it.name,
                            description = it.type
                        )
                    }.toTypedArray(), R.string.account_to_label, BOTTOM_SHEET_ACCOUNT_TO_SELECT_ID
                )
            findNavController().safeNavigate(action)
        }

        // category input
        binding.categoryEt.setOnClickListener {
            val action = AddEditTransactionBottomSheetFragmentDirections
                .actionAddTransactionBottomSheetFragmentToSearchableSelectorBottomSheetFragment(
                    list =
                    formData.categories.map {
                        SearchableListItem(
                            id = it.categoryId,
                            name = it.name,
                            description = it.description
                        )
                    }.toTypedArray(), R.string.category_label, BOTTOM_SHEET_CATEGORY_SELECT_ID
                )
            findNavController().safeNavigate(action)
        }

        // entity input
        binding.entityEt.setOnClickListener {
            val action = AddEditTransactionBottomSheetFragmentDirections
                .actionAddTransactionBottomSheetFragmentToSearchableSelectorBottomSheetFragment(
                    list =
                    formData.entities.map {
                        SearchableListItem(
                            id = it.entityId,
                            name = it.name,
                        )
                    }.toTypedArray(), R.string.entity_label, BOTTOM_SHEET_ENTITY_SELECT_ID
                )
            findNavController().safeNavigate(action)
        }

        // description
        binding.descriptionEt.addTextChangedListener { text ->
            viewModel.triggerEvent(
                AddTransactionContract.Event.DescriptionChanged(text.toString())
            )
        }

        // type
        setupTypeSelector()

        // add btn
        binding.addBtn.setOnClickListener { viewModel.triggerEvent(AddTransactionContract.Event.AddEditTransactionClick) }
    }

    private fun setupTypeSelector() {
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