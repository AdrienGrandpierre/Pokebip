package com.bipbip.pokebip.fragments.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bipbip.pokebip.databinding.FragmentLoginBinding

private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {

    private val model: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getState().observe(viewLifecycleOwner, Observer { updateUi(it!!) })


        binding.loginButton.setOnClickListener {
            model.login(binding.email.text.toString(), binding.password.text.toString())
        }

        binding.email.doAfterTextChanged {
            model.UpdateLogin(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.password.doAfterTextChanged {
            model.UpdateLogin(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }

        model.UpdateLogin(
            binding.email.text.toString(),
            binding.password.text.toString()
        )
    }

    private fun updateUi(state: LoginViewModelState) {
        when (state) {
            is LoginViewModelState.Success -> {
                binding.loginButton.isEnabled = state.loginButtonEnable
                Log.d(TAG, "Token " + state.token)
                Toast.makeText(context, "GOOD", Toast.LENGTH_SHORT).show()
            }
            is LoginViewModelState.Failure -> {
                binding.loginButton.isEnabled = state.loginButtonEnable
                Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
            }
            is LoginViewModelState.UpdateLogin -> {
                binding.loginButton.isEnabled = state.loginButtonEnable
            }
        }
    }
}