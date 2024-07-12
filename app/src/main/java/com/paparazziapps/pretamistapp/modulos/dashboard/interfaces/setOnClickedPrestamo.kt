package com.paparazziapps.pretamistapp.modulos.dashboard.interfaces

import com.paparazziapps.pretamistapp.modulos.registro.pojo.Credit

interface setOnClickedPrestamo {

    fun actualizarPagoPrestamo(prestamo:Credit, needUpdate: Boolean, montoTotalAPagar:Double, adapterPosition:Int, diasRestrasado:String)
    fun openDialogoActualizarPrestamo(prestamo:Credit, montoTotalAPagar:Double, adapterPosition:Int, diasRestantesPorPagar:Int, diasPagados:Int, isClosed:Boolean)
}