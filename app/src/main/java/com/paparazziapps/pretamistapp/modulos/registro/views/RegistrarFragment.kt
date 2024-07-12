package com.paparazziapps.pretamistapp.modulos.registro.views

import ClientsViewModel
import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentRegistrarBinding
import com.paparazziapps.pretamistapp.helper.getDoubleWithTwoDecimals
import com.paparazziapps.pretamistapp.helper.getDoubleWithTwoDecimalsReturnDouble
import com.paparazziapps.pretamistapp.helper.hideKeyboardFrom
import com.paparazziapps.pretamistapp.helper.showMessageAboveBottomMenuGlobal
import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Client
import com.paparazziapps.pretamistapp.modulos.clientes.providers.ClientProviderFirebase
import com.paparazziapps.pretamistapp.modulos.clientes.views.adapter.ClientAdapter
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Credit
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister
import kotlin.math.ceil

class RegistrarFragment : Fragment() {

    private var _binding: FragmentRegistrarBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy { ViewModelRegister.getInstance() }
    private val clientViewModel by lazy { ClientsViewModel.getInstance(ClientProviderFirebase()) }

    // Variables
    private var isValidInteres = false
    private var isValidInteresP = false
    private var isValidMeses = false
    private var isValidMesesP = false
    private var isValidCapitalPrestado = false

    private var mesesEntero: Int = 0
    private var capitalEntero: Int = 0
    private var interesEntero: Int = 0
    private var montoDiarioAPagar: Double = 0.0
    private var montoTotalAPagar: Double = 0.0
    private var credit = Credit()
    val listClients = arrayListOf<Client>()

    // Layout
    private val listaIntereses = arrayListOf("8%", "10%", "20%", "30%", "40%", "50%")
    private val listaPlazos = arrayListOf("30 dias", "60 dias", "90 dias", "120 dias", "180 dias")
    private val listmode = arrayListOf(M_STANDAR, M_PERSONALIZADO)

    // Result for activity
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val mensaje = it.data?.getStringExtra("mensaje")
                showMessage(mensaje ?: "")
            } else {
                Log.d("RegistrarFragment", "Resultado de actividad: null")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrarBinding.inflate(inflater, container, false)
        setupSpinners()
        setupObservers()
        setupValidations()
        setupContinueButton()
        setupRoundButton()
        return binding.root
    }

    private fun setupRoundButton() {
        binding.cardViewDailyAmount.setOnClickListener {
            val newMontoDiario = ceil(montoDiarioAPagar)
            binding.textViewDailyAmount.text = getString(
                R.string.formatted_amount,
                getString(R.string.tipo_moneda),
                getDoubleWithTwoDecimals(newMontoDiario)
            )
            montoDiarioAPagar = getDoubleWithTwoDecimalsReturnDouble(newMontoDiario) ?: 0.0
            montoTotalAPagar =
                getDoubleWithTwoDecimalsReturnDouble(newMontoDiario * mesesEntero) ?: 0.0
            binding.textViewTotalAmount.text = getString(
                R.string.formatted_amount,
                getString(R.string.tipo_moneda),
                getDoubleWithTwoDecimals(newMontoDiario * mesesEntero)
            )
        }
    }

    private fun setupContinueButton() {
        binding.buttonContinue.setOnClickListener {
            with(credit) {
                capital = capitalEntero.toLong()
                creditInterest = interesEntero.toString()
                creditTerm = mesesEntero.toString()
                dailyAmountPay = montoDiarioAPagar.toString()
                amountTotalPay = montoTotalAPagar.toString()
            }
            val prestamoJson = Gson().toJson(credit)
            startForResult.launch(
                Intent(
                    context,
                    RegistrarActivity::class.java
                ).putExtra("prestamoJson", prestamoJson)
            )
        }
    }

    private fun setupValidations() {
        with(binding) {
            autoCompleteInterest.doAfterTextChanged { validateInterestSP() }
            autoCompleteTerm.doAfterTextChanged { validateTerms() }
            editTextBorrowedCapital.doAfterTextChanged { validateBorrowedCapital() }
            editTextInterestCustom.doAfterTextChanged { validateInterestCustom() }
            editTextTermCustom.doAfterTextChanged { validateTermsCustom() }
            autoCompleteMode.doAfterTextChanged { validateMode() }
        }
    }

    private fun validateInterestSP() {
        isValidInteres = !binding.autoCompleteInterest.text.isNullOrBlank()
        validateFields()
    }

    private fun validateTerms() {
        isValidMeses = !binding.autoCompleteTerm.text.isNullOrBlank()
        validateFields()
    }

    private fun validateBorrowedCapital() {
        isValidCapitalPrestado = binding.editTextBorrowedCapital.text?.length in 1..8
        validateFields()
    }

    private fun validateInterestCustom() {
        isValidInteresP = !binding.editTextInterestCustom.text.isNullOrBlank()
        validateFields()
    }

    private fun validateTermsCustom() {
        isValidMesesP = !binding.editTextTermCustom.text.isNullOrBlank()
        validateFields()
    }

    private fun validateMode() {
        when (binding.autoCompleteMode.text.toString()) {
            M_STANDAR -> {
                binding.layoutDefault.isVisible = true
                binding.layoutCustom.isVisible = false
                clearData()
            }

            M_PERSONALIZADO -> {
                binding.layoutDefault.isVisible = false
                binding.layoutCustom.isVisible = true
                clearData()
            }

            else -> showMessage("Error cambiando de modo")
        }
        validateFields()
    }

    private fun setupSpinners() {
        with(binding) {
            val adapterIntereses =
                ArrayAdapter(requireContext(), R.layout.select_items, listaIntereses)
            val adapterPlazos = ArrayAdapter(requireContext(), R.layout.select_items, listaPlazos)
            val adapterModos = ArrayAdapter(requireContext(), R.layout.select_items, listmode)

            // set del adapte
            clientViewModel.getClients()
            clientViewModel.clients.observe(requireActivity()) {
                if (it.isNotEmpty()) {
                    it.forEach {
                        listClients.add(it)
                    }
                }
            }

            val adapterClient = ClientAdapter(requireContext(), listClients)

            autoCompleteInterest.setAdapter(adapterIntereses)
            autoCompleteTerm.setAdapter(adapterPlazos)
            autoCompleteMode.setAdapter(adapterModos)
            autoCompleteClient.setAdapter(adapterClient)

            layoutDefault.setOnClickListener { autoCompleteInterest.showDropDown() }
            autoCompleteInterest.setOnClickListener { autoCompleteInterest.showDropDown() }
            layoutTerm.setEndIconOnClickListener { autoCompleteTerm.showDropDown() }
            autoCompleteTerm.setOnClickListener { autoCompleteTerm.showDropDown() }
            layoutMode.setEndIconOnClickListener { autoCompleteMode.showDropDown() }
            autoCompleteMode.setOnClickListener { autoCompleteMode.showDropDown() }
            autoCompleteClient.setOnClickListener { autoCompleteClient.showDropDown() }

            // listener cuando lo selecciona
            autoCompleteClient.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val selectedItem = parent.getItemAtPosition(position) as Client
                    credit.dni = selectedItem.dni
                    validateFields()
                }
        }
    }

    private fun validateFields() {
        when (binding.autoCompleteMode.text.toString()) {
            M_STANDAR -> {
                if (isValidInteres && isValidMeses && isValidCapitalPrestado) {
                    calculateAll(M_STANDAR)
                    activateContinue(true)
                } else {
                    activateContinue(false)
                }
            }

            M_PERSONALIZADO -> {
                if (isValidInteresP && isValidMesesP && isValidCapitalPrestado) {
                    calculateAll(M_PERSONALIZADO)
                    activateContinue(true)
                } else {
                    activateContinue(false)
                }
            }

            else -> showMessage("Error validando datos")
        }
    }

    private fun activateContinue(isValidEverything: Boolean) {
        with(binding.buttonContinue) {
            isEnabled = isValidEverything
            backgroundTintMode =
                if (isValidEverything) PorterDuff.Mode.SCREEN else PorterDuff.Mode.MULTIPLY
            backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (isValidEverything) R.color.colorPrimary else R.color.color_input_text
            )
            setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isValidEverything) R.color.white else R.color.color_input_text
                )
            )
        }
    }

    private fun calculateAll(mode: String) {
        with(binding) {
            when (mode) {
                M_STANDAR -> {
                    capitalEntero = editTextBorrowedCapital.text.toString().trim().toInt()
                    interesEntero = autoCompleteInterest.text.dropLast(1).toString().toInt()
                    mesesEntero = autoCompleteTerm.text.dropLast(5).toString().toInt()
                }

                M_PERSONALIZADO -> {
                    capitalEntero = editTextBorrowedCapital.text.toString().trim().toInt()
                    interesEntero = editTextInterestCustom.text.toString().toInt()
                    mesesEntero = editTextTermCustom.text.toString().toInt()
                }

                else -> showMessage("No se pudo procesar tu solicitud")
            }
            viewModel.calculateDailyAmount(capitalEntero, interesEntero, mesesEntero)
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            getMessage().observe(viewLifecycleOwner) { message ->
                if (message != null) showMessage(message)
            }

            getDailyAmount().observe(viewLifecycleOwner) { dailyAmount ->
                if (dailyAmount != null) {
                    Log.d("RegistrarFragment", "Monto Diario: $dailyAmount")
                    montoDiarioAPagar = getDoubleWithTwoDecimalsReturnDouble(dailyAmount) ?: 0.0
                    montoTotalAPagar =
                        getDoubleWithTwoDecimalsReturnDouble(dailyAmount * mesesEntero) ?: 0.0
                    binding.textViewTotalAmount.text = getString(
                        R.string.formatted_amount,
                        getString(R.string.tipo_moneda),
                        getDoubleWithTwoDecimals(dailyAmount * mesesEntero)
                    )
                } else {
                    Log.d("RegistrarFragment", "Monto diario es 0")
                }
            }
        }
    }

    private fun clearData() {
        with(binding) {
            editTextTermCustom.setText("")
            editTextInterestCustom.setText("")
            textViewDailyAmount.text = getString(R.string.tipo_moneda_default_zero)
            textViewTotalAmount.text = getString(R.string.tipo_moneda_default_zero)
        }
    }

    private fun showMessage(message: String) {
        showMessageAboveBottomMenuGlobal(message, binding.root)
        // Snackbar.make(requireActivity().findViewById(R.id.nav_view), "$message", Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        binding.root.hideKeyboardFrom()
    }

    override fun onDestroy() {
        ViewModelRegister.destroyInstance()
        _binding = null
        super.onDestroy()
    }

    companion object {
        const val M_PERSONALIZADO = "Personalizado"
        const val M_STANDAR = "Estándar"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrarFragment().apply {
                arguments = Bundle().apply {
                    // Aquí puedes agregar parámetros si es necesario
                }
            }
    }
}

