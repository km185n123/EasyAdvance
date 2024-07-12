package com.paparazziapps.pretamistapp.modulos.collection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentFeePaymentBinding
import com.paparazziapps.pretamistapp.helper.HttpsTrustManager
import com.paparazziapps.pretamistapp.modulos.mesage.ChatwootViewModel
import com.paparazziapps.pretamistapp.modulos.network.ChatwootViewModelFactory


class FeePaymentFragment : Fragment(R.layout.fragment_fee_payment) {

    private var _binding: FragmentFeePaymentBinding? = null
    private val binding get() = _binding!!
    private  var chatwootViewModel: ChatwootViewModel?  = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeePaymentBinding.inflate(inflater, container, false)

        setupSendTiket()

        _binding?.btnPagar?.setOnClickListener {
            HttpsTrustManager.allowAllSSL();
            var phone = _binding?.edtErtilizer?.text.toString()
            var cuota = _binding?.edtDiasAPagar?.text.toString()
            // Ejemplo de uso para enviar un mensaje
            val phoneNumber = "+${phone}"
            val message = "\uD83D\uDCE2 **Notificación de Pago**\n" +
                    "---\n" +
                    "\uD83D\uDC64 Cliente: Juan Pérez\n" +
                    "\uD83D\uDCB5 Valor Pagado: \$${cuota}\n" +
                    "\uD83D\uDCC5 Número de Cuota: 3\n" +
                    "\uD83D\uDCC9 Cuotas Restantes: 7\n" +
                    "\uD83D\uDCCA Saldo Total de Crédito: $5000" +
                    "\uD83D\uDCB8 Saldo Restante del Crédito: $3600" +
                    "\uD83D\uDCC6 Fecha de Inicio del Crédito: 05/05/2024" +
                    "---\n" +
                    "¡Gracias por tu pago! Si tienes alguna pregunta, no dudes en contactarnos."


            var attachmentUrl = "https://apps.camaralima.org.pe/repositorioaps/0/0/par/cartamodelo/carta%20modelo.pdf"

            chatwootViewModel?.sendMessage(phoneNumber, message)
            chatwootViewModel?.sendMessageWithAttachment(phoneNumber, message, attachmentUrl)


        }

        return binding.root
    }

    private fun setupSendTiket() {
        chatwootViewModel = ViewModelProvider(this, ChatwootViewModelFactory(requireContext()))
            .get(ChatwootViewModel::class.java)

        chatwootViewModel?.messageSent?.observe(this) { messageSent ->
            if (messageSent) {
                Toast.makeText(requireContext(), "Message sent successfully!", Toast.LENGTH_SHORT)
                    .show()
                val navController =
                    requireActivity().findNavController(R.id.nav_host_collection_fragment)
                navController.navigate(R.id.action_feePaymentFragment_to_startCollectionFragment)
            } else {
                Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
