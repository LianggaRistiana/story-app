package com.dicoding.storyapp.ui.login

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
import com.dicoding.storyapp.databinding.FragmentLoginBinding
import com.dicoding.storyapp.helper.factory.ViewModelFactory
import com.dicoding.storyapp.ui.SessionViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: LoginViewModel by viewModels {
            ViewModelFactory.getInstance(requireContext())
        }

        val sessionViewModel: SessionViewModel by viewModels {
            ViewModelFactory.getInstance(requireContext())
        }

        sessionViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                view.findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        playAnimation()

        with(binding.edLoginEmail) {
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

        with(binding.edLoginPassword) {
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

        binding.loginButton.setOnClickListener {
            viewModel.login()
        }

        binding.registerButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        viewModel.loginProcessState.observe(viewLifecycleOwner) {
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
                        getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.clearLoginProcessState()
                }

                else -> {}
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.loginImage, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val loginText =
            ObjectAnimator.ofFloat(binding.loginText, View.ALPHA, 0f, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 0f, 1f).setDuration(500)
        val signup =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 0f, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 0f, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 0f, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(loginText, email, password, together)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
