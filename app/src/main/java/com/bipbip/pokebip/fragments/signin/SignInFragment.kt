package com.bipbip.pokebip.fragments.signin

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
import com.bipbip.pokebip.R
import com.bipbip.pokebip.databinding.FragmentLoginBinding
import com.bipbip.pokebip.databinding.FragmentSignInBinding
import com.bipbip.pokebip.fragments.login.LoginViewModel
import com.bipbip.pokebip.fragments.login.LoginViewModelState

private const val TAG = "SignInFragment"

class SignInFragment : Fragment() {

    private val model: SignInViewModel by viewModels()
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.getState().observe(viewLifecycleOwner, Observer { updateUi(it!!) })

        binding.signInButton.setOnClickListener {
            model.signin(binding.username.text.toString(),binding.email.text.toString(), binding.password.text.toString())
        }

        binding.email.doAfterTextChanged {
            model.UpdateSignIn(
                binding.username.text.toString(),
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.password.doAfterTextChanged {
            model.UpdateSignIn(
                binding.username.text.toString(),
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }

        model.UpdateSignIn(
            binding.username.text.toString(),
            binding.email.text.toString(),
            binding.password.text.toString()
        )
    }

    private fun updateUi(state: SignInViewModelState) {
        when (state) {
            is SignInViewModelState.Success -> {
                binding.signInButton.isEnabled = state.signInButtonEnable
                Log.d(TAG, "Token " + state.token)
                Toast.makeText(context, "GOOD", Toast.LENGTH_SHORT).show()
            }
            is SignInViewModelState.Failure -> {
                binding.signInButton.isEnabled = state.signInButtonEnable
                Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
            }
            is SignInViewModelState.UpdateSignIn -> {
                binding.signInButton.isEnabled = state.signInButtonEnable
            }
        }
    }
}