package com.afaneca.myfin.open.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.afaneca.myfin.base.BaseFragment
import com.afaneca.myfin.closed.PrivateActivity
import com.afaneca.myfin.data.network.MyFinAPIServices
import com.afaneca.myfin.data.network.Resource
import com.afaneca.myfin.databinding.FragmentLoginBinding
import com.afaneca.myfin.open.login.data.LoginRepository
import com.afaneca.myfin.utils.BiometricsHelper
import com.afaneca.myfin.utils.enable
import com.afaneca.myfin.utils.startNewActivity
import com.afaneca.myfin.utils.visible


class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding, LoginRepository>() {
    private val biometricsHelper by lazy { BiometricsHelper(this) }

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
            binding.loadingPb.root.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    // TODO -  This should be decided by the viewmodel!
                    goToPrivateActivity()
                }

                is Resource.Failure -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.usernameInput.observe(viewLifecycleOwner, {
            if (!it.isNullOrBlank()) {
                // set default username
                binding.usernameEt.setText(it)
            }
        })

        viewModel.triggerBiometricAuth.observe(viewLifecycleOwner, {
            if (it) {
                checkBiometrics()
            }
        })
    }

    private fun goToPrivateActivity() {
        requireActivity().startNewActivity(PrivateActivity::class.java)
        activity?.finish()
    }

    private fun bindListeners() {
        binding.loginBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            val keepSession = binding.rememberMeCb.isChecked

            // TODO - add validations
            viewModel.attemptLogin(username, password, keepSession, requireContext())
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


    // BIOMETRICS
    private fun checkBiometrics() {
        biometricsHelper.showBiometricPrompt {
            //  SUCCESS
            viewModel.attemptBiometricLogin(requireContext())
        }
    }


    override fun getViewModel() = LoginViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        LoginRepository(
            remoteDataSource.create(MyFinAPIServices::class.java),
            userData,
            db
        )
}