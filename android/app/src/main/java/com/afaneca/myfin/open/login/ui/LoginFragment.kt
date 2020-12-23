package com.afaneca.myfin.open.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.databinding.FragmentLoginBinding
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import kotlinx.coroutines.launch


class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding, LoginRepository>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        bindObservers()
        bindListeners()
        return rootView;
    }

    private fun bindObservers() {
        viewModel.isLoginButtonEnabled.observe(viewLifecycleOwner, Observer {
            binding.loginBtn.isEnabled = it
        })
        binding.loginBtn.isEnabled = viewModel.isLoginButtonEnabled.value!!

        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.loadingPb.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_LONG).show()
                    lifecycleScope.launch {
                        userData.saveSessionKey(it.data.sessionkey!!)
                        Toast.makeText(
                            requireContext(),
                            "Token: " + userData.sessionKey,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun bindListeners() {
        binding.loginBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()

            // TODO add validations
            viewModel.attemptLogin(username, password)
        }
    }

    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        LoginRepository(remoteDataSource.buildApi(MyFinAPIServices::class.java))
}