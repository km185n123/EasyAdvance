package com.paparazziapps.pretamistapp.modulos.collection


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentIncidentBinding

class IncidentFragment : Fragment() {

    private var _binding: FragmentIncidentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncidentBinding.inflate(inflater, container, false)

        val reasons = arrayOf("Enfermo", "Ausente", "No tiene el dinero")
        _binding?.btnSubmit?.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.nav_host_collection_fragment)
            navController.navigate(R.id.action_feePaymentFragment_to_startCollectionFragment)
        }
        setupSpinner(reasons)
        return binding.root
    }

    private fun setupSpinner(reasons: Array<String>) {
        val adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, reasons)
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        _binding?.spinnerReasons?.adapter = adapter
        _binding?.spinnerReasons?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedReason = parent?.getItemAtPosition(position).toString()
                _binding?.btnCall?.visibility = if (selectedReason == "Ausente") View.VISIBLE else View.GONE

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
