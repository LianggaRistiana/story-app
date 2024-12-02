package com.dicoding.storyapp.ui.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.Result
import com.dicoding.storyapp.databinding.FragmentDetailBinding
import com.dicoding.storyapp.helper.factory.ViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storyId = arguments?.getString("id")

        val viewModel: DetailViewModel by viewModels {
            ViewModelFactory.getInstance(requireContext())
        }

        viewModel.getStoryById(storyId!!).observe(viewLifecycleOwner) {
            when(it){
                is Result.Error -> Toast.makeText(context, it.error, Toast.LENGTH_SHORT).show()
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    binding.tvIdName.text = it.data!!.name
                    binding.tvIdDescriptionContent.text = it.data.description
                    Glide.with(requireContext())
                        .load(it.data.photoUrl)
                        .placeholder(R.drawable.error_image)
                        .error(R.drawable.error_image)
                        .into(binding.ivItemPhoto)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}