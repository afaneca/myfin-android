package com.afaneca.myfin.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.afaneca.myfin.data.UserDataManager
import com.afaneca.myfin.data.db.MyFinDatabase
import com.afaneca.myfin.data.network.BaseRepository
import org.koin.android.ext.android.inject
import retrofit2.Retrofit

abstract class BaseFragment<VM : ViewModel, B : ViewBinding, R : BaseRepository> : Fragment() {
    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    protected val userData: UserDataManager by inject()
    protected val db: MyFinDatabase by inject()
    protected val remoteDataSource: Retrofit by inject() // = RetrofitClient()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*userData = get()*/
        binding = getFragmentBinding(inflater, container)

        val factory = BaseViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    protected fun setActionBarTitle(title: String) {
        activity?.title = title
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getFragmentRepository(): R
}