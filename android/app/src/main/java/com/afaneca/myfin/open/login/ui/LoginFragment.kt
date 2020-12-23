package com.afaneca.myfin.open.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.PrivateActivity
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.databinding.FragmentLoginBinding
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.utils.enable
import com.afaneca.myfin.utils.startNewActivity
import com.afaneca.myfin.utils.visible


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
        viewModel.shouldLoginButtonBeEnabled.observe(viewLifecycleOwner, Observer {
            binding.loginBtn.enable(it)
        })
        /*binding.loginBtn.enable(viewModel.shouldLoginButtonBeEnabled.value!!)*/

        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            //binding.loadingPb.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
            binding.loadingPb.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    viewModel.saveSessionToken(it.data.sessionkey!!)
                    requireActivity().startNewActivity(PrivateActivity::class.java)
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

        binding.passwordEt.addTextChangedListener {
            val username = binding.usernameEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            checkIfLoginButtonShouldBeEnabled(username, password)
        }

        binding.usernameEt.addTextChangedListener {
            val username = binding.usernameEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            checkIfLoginButtonShouldBeEnabled(username, password)
        }
    }

    private fun checkIfLoginButtonShouldBeEnabled(usernameInput: String, passwordInput: String) {
        viewModel.shouldLoginButtonBeEnabled.value =
            ((usernameInput.isNotEmpty() && passwordInput.isNotEmpty()))
    }

    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        LoginRepository(remoteDataSource.buildApi(MyFinAPIServices::class.java), userData)
}