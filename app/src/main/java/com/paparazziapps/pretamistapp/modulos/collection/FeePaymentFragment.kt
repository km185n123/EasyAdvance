package com.paparazziapps.pretamistapp.modulos.collection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentFeePaymentBinding


class FeePaymentFragment : Fragment(R.layout.fragment_fee_payment) {

    private var _binding: FragmentFeePaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeePaymentBinding.inflate(inflater, container, false)

        _binding?.btnPagar?.setOnClickListener {
            val navController =
                requireActivity().findNavController(R.id.nav_host_collection_fragment)
            navController.navigate(R.id.action_feePaymentFragment_to_startCollectionFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
