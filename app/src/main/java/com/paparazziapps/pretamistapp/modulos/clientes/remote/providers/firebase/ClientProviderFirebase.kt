package com.paparazziapps.pretamistapp.modulos.clientes.remote.providers.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Client
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences

class ClientProviderFirebase {

    var preferences = MyPreferences()

    companion object {
        private lateinit var mCollectionClient: CollectionReference
    }

    //Constructor
    init {
        mCollectionClient = FirebaseFirestore.getInstance().collection("Clientes")

        var settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = settings
    }

    //Super admin -- implemented

    fun create(client: Client, idSucursal: Int): Task<Void> {
        client.sucursalId = if (preferences.isSuperAdmin) idSucursal else preferences.sucursalId
        client.id = mCollectionClient.document().id
        return mCollectionClient.document(client.id!!).set(client)
    }

    //Super admin -- implemented
    fun getClients(): Task<QuerySnapshot> {
        return mCollectionClient
                .whereEqualTo("sucursalId", preferences.sucursalId)
                .get()
    }

    fun getClient(dni: String): Task<QuerySnapshot> {
        return if (preferences.isSuperAdmin) {
            println("Super admin -- getPrestamos")
            mCollectionClient
                .whereEqualTo("state", "ABIERTO")
                .get()
        } else {
            println("Sucursal -- getPrestamos")
            mCollectionClient
                .whereEqualTo("dni", dni)
                .whereEqualTo("sucursalId", preferences.sucursalId)
                .get()
        }
    }

}