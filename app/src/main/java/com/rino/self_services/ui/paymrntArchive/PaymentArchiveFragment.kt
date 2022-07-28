package com.rino.self_services.ui.paymrntArchive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rino.self_services.R
import com.rino.self_services.databinding.FragmentPaymentArchiveBinding
import com.rino.self_services.ui.paymentProcessHome.NavSeeAll
import com.rino.self_services.ui.paymentProcessHome.NavToDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentArchiveFragment : Fragment() {
    val viewModel: PaymentArchiveViewModel by viewModels()
    private lateinit var binding: FragmentPaymentArchiveBinding
    private lateinit var amountChangelogAdapter: AmountChangelogAdapter
    private  lateinit var navToDetails: NavToDetails
    private  lateinit var seeAll:NavSeeAll

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navToDetails = arguments?.get("nav_to_pp_details") as NavToDetails
            seeAll = arguments?.get("nav_to_see_all_payment") as NavSeeAll
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentArchiveBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setupUI()
        observeData()
        handleBack()
    }

    private fun observeData() {
        observeAmountChangelog()
        observeLoading()
        observeShowError()
    }


    private fun observeAmountChangelog() {
        viewModel.getAmountChangelog(navToDetails.id)
        viewModel.amountChangelogResponse.observe(viewLifecycleOwner) {
            it?.let {
                if(it.data.size == 0)
                {
                    binding.noDataLayout.visibility = View.VISIBLE
                    binding.paymentArchiveRecycle.visibility = View.GONE

                }else {
                    binding.noDataLayout.visibility = View.GONE
                    binding.paymentArchiveRecycle.visibility = View.VISIBLE
                    amountChangelogAdapter.updateItems(it.data)

                }
            }
        }
    }


    private fun observeLoading() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let {
     //           binding.progress.visibility = it
                if(it == View.VISIBLE)
                {
                    binding.shimmer.visibility = View.VISIBLE
                    binding.shimmer.startShimmer()
                    binding.paymentArchiveRecycle.visibility = View.GONE
                }
                else if(it == View.GONE){
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.paymentArchiveRecycle.visibility = View.VISIBLE

                }
            }
        }
    }
    private fun observeShowError() {
        viewModel.setError.observe(viewLifecycleOwner) {
            it?.let {
                if(it.contains("Time out")){
                    binding.noDataLayout.visibility = View.VISIBLE
                    binding.noDataAnim.setAnimation(R.raw.rino_timeout)
                    binding.textNoData.text = getString(R.string.timeout_msg)
                    binding.paymentArchiveRecycle.visibility = View.GONE
                }
                else if(it.contains("server is down"))
                {
                    binding.noDataLayout.visibility = View.VISIBLE
                    binding.noDataAnim.setAnimation(R.raw.rino_server_error2)
                    binding.textNoData.text = getString(R.string.server_error_msg)
                    binding.paymentArchiveRecycle.visibility = View.GONE
                }
                else{
                    showMsg(it)
                }
            }
        }
    }

    private fun showMsg(it: String) {
        Snackbar.make(requireView(), it, Snackbar.LENGTH_INDEFINITE)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).setBackgroundTint(
                resources.getColor(
                    R.color.color_orange
                )
            )
            .setActionTextColor(resources.getColor(R.color.white)).setAction(getString(R.string.dismiss))
            {
            }.show()
    }
    private fun setupUI() {
        binding.paymentArchiveRecycle.visibility = View.GONE
        amountChangelogAdapter = AmountChangelogAdapter(arrayListOf())
        binding.paymentArchiveRecycle.visibility = View.VISIBLE
        binding.paymentArchiveRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = amountChangelogAdapter
        }
    }
    private fun handleBack() {
        binding.backbtn.setOnClickListener {
            val action =
                PaymentArchiveFragmentDirections.actionPaymentArchiveFragmentToPaymentProcessDetailsFragment(navToDetails,seeAll)
            findNavController().navigate(action)

        }
    }

}