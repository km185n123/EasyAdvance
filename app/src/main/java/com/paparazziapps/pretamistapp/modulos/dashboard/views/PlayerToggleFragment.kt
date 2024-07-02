package com.paparazziapps.pretamistapp.modulos.dashboard.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paparazziapps.pretamistapp.databinding.FragmentClientToggleBinding

class PlayerToggleFragment : Fragment() {

    private var _binding: FragmentClientToggleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla el layout para este fragmento usando View Binding
        _binding = FragmentClientToggleBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
