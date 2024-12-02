package com.dicoding.storyapp.ui.register

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dicoding.storyapp.R
import androidx.navigation.findNavController
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.storyapp.helper.factory.ViewModelFactory

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: RegisterViewModel by viewModels{
            ViewModelFactory.getInstance(requireContext())
        }

        with(binding.edRegisterName) {
            setText(viewModel.email.value)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    if (!viewModel.setEmail(p0.toString())) {
                        setError("Username harus minimal 6 karakter.", null)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
        }

        with(binding.edRegisterEmail) {
            setText(viewModel.email.value)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    if (!viewModel.setEmail(p0.toString())) {
                        setError("Username harus minimal 6 karakter.", null)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
        }

        with(binding.edRegisterPassword) {
            setText(viewModel.password.value)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (!viewModel.setPassword(p0.toString())) {
                        setError("Password harus minimal 8 karakter.", null)
                    }
                }
                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }

        viewModel.formValid.observe(viewLifecycleOwner) {
            binding.registerButton.isEnabled = it
        }

        binding.registerButton.setOnClickListener {
            viewModel.register()
        }

        binding.loginButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


        viewModel.registerProcessState.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT)
                        .show()
                }

                Result.Loading -> showLoading(true)

                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Register Success", Toast.LENGTH_SHORT).show()
                    viewModel.clearRegisterProcessState()
                    viewModel.login()
                }
                else -> {}
            }
        }

        viewModel.loginProcessState.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT)
                        .show()
                }

                Result.Loading -> {
                    Toast.makeText(requireContext(), "Waiting for Login", Toast.LENGTH_SHORT).show()
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Login Success", Toast.LENGTH_SHORT).show()
                    viewModel.clearLoginProcessState()
                    view.findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                }
                else -> {}
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}