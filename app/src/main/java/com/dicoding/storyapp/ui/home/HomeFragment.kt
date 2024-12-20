package com.dicoding.storyapp.ui.home

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentHomeBinding
import com.dicoding.storyapp.helper.adaptor.ListStoryAdapter
import com.dicoding.storyapp.helper.adaptor.LoadingStateAdapter
import com.dicoding.storyapp.helper.factory.ViewModelFactory
import com.dicoding.storyapp.ui.SessionViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var adapter: ListStoryAdapter? = null

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


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

                R.id.menu_map -> {
                    view.findNavController().navigate(R.id.action_homeFragment_to_mapsActivity)
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

        adapter = ListStoryAdapter { storyId ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(storyId)
            view.findNavController().navigate(action)
        }

        binding.rvIdStory.adapter = adapter!!
            .withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter!!.retry()
                }
            )

        viewModel.story.observe(viewLifecycleOwner) {
            adapter!!.submitData(lifecycle, it)
        }

        binding.rvIdStory.apply {
            setHasFixedSize(true)
            setLayoutManager(layoutManager)
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

}