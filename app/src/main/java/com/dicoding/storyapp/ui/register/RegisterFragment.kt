package com.dicoding.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.dicoding.storyapp.R
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

        val viewModel: RegisterViewModel by viewModels {
            ViewModelFactory.getInstance(requireContext())
        }

        playAnimation()


        with(binding.edRegisterName) {
            setText(viewModel.email.value)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewModel.setName(p0.toString())
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
                    viewModel.setEmail(p0.toString())
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
                    viewModel.setPassword(p0.toString())
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
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
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.clearRegisterProcessState()
                }

                else -> {}
            }
        }
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.registerImage, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val registerText =
            ObjectAnimator.ofFloat(binding.registerText, View.ALPHA, 0f, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 0f, 1f).setDuration(500)
        val signup =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 0f, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 0f, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 0f, 1f).setDuration(500)
        val name =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 0f, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(registerText, name, email, password, together)
            start()
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}