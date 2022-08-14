package com.rino.self_services.ui.myProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentComplaintsBinding
import com.rino.self_services.databinding.FragmentMyProfileBinding
import com.rino.self_services.model.pojo.profile.ProfileResponse
import com.rino.self_services.ui.complaints.ComplaintsFragmentDirections
import com.rino.self_services.ui.complaints.ComplaintsViewModel
import com.rino.self_services.utils.dateToArabic
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileFragment : Fragment() {
    val viewModel: MyProfileViewModel by viewModels()
    private lateinit var binding: FragmentMyProfileBinding
    private var from_where = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from_where = arguments?.get("from_where").toString()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        viewModel.getProfileData()
        observeData()
        handleBack()

    }

    private fun observeData() {
        observeProfileData()
        observeLoading()
        observeShowError()
    }

    private fun observeProfileData() {
        viewModel.profileData.observe(viewLifecycleOwner) {
            setupUI(it)
        }
    }

    private fun setupUI(profileData: ProfileResponse?) {
        binding.departmentValue.text = profileData?.departmentArabic
        binding.phoneNumValue.text = profileData?.phoneNumber ?: " لا يوجد ".dateToArabic()
        binding.emailValue.text = profileData?.email
        binding.nameTxt.text = profileData?.arabicName

    }

    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
                binding.progress.visibility = it
            }
        }
    }

    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                showMessage(it)
            }
        }
    }


    private fun showMessage(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_orange
                )
            ).setActionTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            ).setAction(getString(R.string.dismiss))
            {
            }.show()
    }

    private fun handleBack() {
        binding.backbtn.setOnClickListener {
            val action =
                MyProfileFragmentDirections.actionMyProfileFragmentToProfileFragment(from_where)
            findNavController().navigate(action)

        }
    }

}