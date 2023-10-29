package com.afaneca.myfin.base.components.searchableSelector

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Filterable
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withResumed
import androidx.lifecycle.withStarted
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.afaneca.myfin.R
import com.afaneca.myfin.databinding.FragmentSearchableSelectorBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchableSelectorBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSearchableSelectorBottomSheetBinding
    private val args: SearchableSelectorBottomSheetFragmentArgs by navArgs()
    private val viewModel: SearchableSelectorViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogThemeNoFloating)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchableSelectorBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.list.isEmpty()) dismiss()
        binding.titleTv.text = getString(args.title)
        observeState()
        viewModel.triggerEvent(
            SearchableSelectorContract.Event.InitList(
                args.id,
                args.list.toList()
            )
        )
    }

    private fun observeState() {
        viewModel.subscribeToState()
            .onEach { state ->
                when (state) {
                    is SearchableSelectorContract.State.Init -> {
                        setupList(state.dataset)
                    }

                    is SearchableSelectorContract.State.ItemSelected -> {
                        dismiss()
                    }

                    null -> {}
                }

            }.launchIn(lifecycleScope)
    }

    private fun setupList(list: List<SearchableListItem>) {
        binding.listRv.adapter =
            SearchableListAdapter(list) { selectedItem -> onItemSelected(selectedItem) }
        binding.listRv.addItemDecoration(
            DividerItemDecoration(
                binding.listRv.context,
                DividerItemDecoration.VERTICAL
            ).apply {
                setDrawable(ColorDrawable(resources.getColor(R.color.colorBackground, null)))
            }
        )
        (binding.listRv.adapter as SearchableListAdapter).submitList(list)

        // to make the whole thing clickable
        binding.searchSv.setOnClickListener {
            binding.searchSv.isIconified = false
        }

        binding.searchSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (binding.listRv.adapter as Filterable).filter.filter(newText)
                return false
            }
        })
    }

    private fun onItemSelected(selectedItem: SearchableListItem) {
        viewModel.triggerEvent(SearchableSelectorContract.Event.Itemselected(selectedItem))
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