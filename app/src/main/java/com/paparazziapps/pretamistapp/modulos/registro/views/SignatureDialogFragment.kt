package com.paparazziapps.pretamistapp.modulos.registro.views

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentSignatureDialogBinding
import com.paparazziapps.pretamistapp.helper.SignatureView

class SignatureDialogFragment : DialogFragment() {

    interface OnDismissListener {
        fun onDismiss(signatureBitmap: Bitmap?)
    }

    var onDismissListener: OnDismissListener? = null

    var signatureBitmap: Bitmap? = null
        private set

    private lateinit var signatureView: SignatureView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignatureDialogBinding.inflate(inflater, container, false)
        signatureView = binding.signatureView

        binding.btnClear.setOnClickListener {
            signatureView.clear()
        }

        binding.btnSave.setOnClickListener {
            signatureBitmap = signatureView.getSignatureBitmap()
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.FullScreenDialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(signatureBitmap)
    }
}


