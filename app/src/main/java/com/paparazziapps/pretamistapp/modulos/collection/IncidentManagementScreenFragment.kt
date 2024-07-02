package com.paparazziapps.pretamistapp.modulos.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.paparazziapps.pretamistapp.databinding.FragmentIncidentManagementScreenBinding


class IncidentManagementScreenFragment : Fragment() {

    private var _binding: FragmentIncidentManagementScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncidentManagementScreenBinding.inflate(inflater, container, false)
        setupViewPager()

        /* _binding?.btnPay?.setOnClickListener {
             findNavController().navigate(R.id.action_incidentManagementScreenFragment_to_feePaymentFragment)
         }*/
        return binding.root
    }

    private fun setupViewPager() {

        val fragments = listOf(FeePaymentFragment(), IncidentFragment())
        val adapter =
            activity?.supportFragmentManager?.let {
                CollectionViewPagerAdapter(
                    fragments,
                    it,
                    lifecycle
                )
            }
        _binding?.viewPager?.adapter = adapter

        _binding?.tabLayout?.let {
            _binding?.viewPager?.let { it1 ->
                TabLayoutMediator(it, it1) { tab, position ->
                    tab.text = when (position) {
                        0 -> "Pagar"
                        1 -> "Insidente"
                        else -> ""
                    }
                }.attach()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
