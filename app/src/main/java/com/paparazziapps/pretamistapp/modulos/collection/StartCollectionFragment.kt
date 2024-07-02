package com.paparazziapps.pretamistapp.modulos.collection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentStartCollectionBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class StartCollectionFragment : Fragment() {

    private var _binding: FragmentStartCollectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartCollectionBinding.inflate(inflater, container, false)

        binding.btnStartCollection.setOnClickListener {
          //  viewModel.iniciarFlujo()
            findNavController().navigate(R.id.action_startCollectionFragment_to_inProgressCollectionFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StartCollectionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
