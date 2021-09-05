package com.afaneca.myfin.base

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    override fun onDestroy() {
        super.onDestroy()
    }

    protected fun setActionBarTitle(title: String) {
        activity?.title = title
    }
}