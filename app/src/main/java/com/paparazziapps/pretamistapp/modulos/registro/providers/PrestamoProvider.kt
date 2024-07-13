package com.paparazziapps.pretamistapp.modulos.registro.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Credit
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences

class PrestamoProvider(private val preferences: MyPreferences) {

    private val mCollectionPrestamo: CollectionReference by lazy {
        FirebaseFirestore.getInstance().collection("Prestamos")
    }


    fun create(prestamo: Credit, idSucursal: Int): Task<Void> {
        prestamo.sucursalId = if (preferences.isSuperAdmin) idSucursal else preferences.sucursalId
        prestamo.id = mCollectionPrestamo.document().id
        return mCollectionPrestamo.document(prestamo.id!!).set(prestamo)
    }

    fun getPrestamos(): Task<QuerySnapshot> {
        val query = if (preferences.isSuperAdmin) {
            mCollectionPrestamo.whereEqualTo("state", "ABIERTO")
        } else {
            mCollectionPrestamo.whereEqualTo("state", "ABIERTO")
                .whereEqualTo("sucursalId", preferences.sucursalId)
        }
        return query.get()
    }

    fun setLastPayment(id: String, fecha: String, diasRestantesPorPagar: Int, diasPagados: Int): Task<Void> {
        val map = mapOf(
            "fechaUltimoPago" to fecha,
            "dias_restantes_por_pagar" to diasRestantesPorPagar,
            "diasPagados" to diasPagados
        )
        return mCollectionPrestamo.document(id).update(map)
    }

    fun cerrarPrestamo(id: String): Task<Void> {
        val map = mapOf("state" to "CERRADO")
        return mCollectionPrestamo.document(id).update(map)
    }

    fun getCurrentLoan(): Task<QuerySnapshot> {
        return mCollectionPrestamo
            .whereEqualTo("actual", true)
            .whereEqualTo("sucursalId", preferences.sucursalId)
            .get()
    }
}
