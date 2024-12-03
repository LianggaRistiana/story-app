package com.dicoding.storyapp.ui.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.databinding.FragmentHomeBinding
import com.dicoding.storyapp.helper.adaptor.ListStoryAdapter
import com.dicoding.storyapp.helper.factory.ViewModelFactory
import com.dicoding.storyapp.ui.SessionViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionViewModel: SessionViewModel by viewModels {
            ViewModelFactory.getInstance(requireContext())
        }

        val viewModel: HomeViewModel by viewModels {
            ViewModelFactory.getInstance(requireContext())
        }

        binding.topAppBar.title = getString(R.string.app_name)
        binding.topAppBar.inflateMenu(R.menu.menu_top_bar)
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                R.id.menu_log_out -> {
                    sessionViewModel.logOut()
                    true
                }

                else -> false
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }


        val layoutManager =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(requireContext(), 2)
            } else {
                LinearLayoutManager(requireContext())
            }

        val adapter = ListStoryAdapter { storyId ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(storyId)
            view.findNavController().navigate(action)
        }

        binding.rvIdStory.apply {
            setHasFixedSize(true)
            setLayoutManager(layoutManager)
        }

        viewModel.getStories().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Error -> {
                    showLoading(false)
                    binding.tvNotStoryFound.visibility = View.VISIBLE
                    Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                }

                Result.Loading -> {
                    showLoading(true)
                    binding.tvNotStoryFound.visibility = View.GONE
                }

                is Result.Success -> {
                    showLoading(false)
                    binding.tvNotStoryFound.visibility =
                        if (it.data.isEmpty()) View.VISIBLE else View.GONE
                    adapter.submitList(it.data)
                    binding.rvIdStory.adapter = adapter
                }
            }
        }


        binding.btnIdAdd.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_addStoryFragment)
        }

        sessionViewModel.logOutProcess.observe(viewLifecycleOwner) {
            if (it) {
                sessionViewModel.resetProcess()
                view.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}