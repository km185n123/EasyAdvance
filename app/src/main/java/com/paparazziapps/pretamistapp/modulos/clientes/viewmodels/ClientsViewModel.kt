package com.paparazziapps.pretamistapp.modulos.clientes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Client
import com.paparazziapps.pretamistapp.modulos.clientes.providers.ClientProvider

open class ClientsViewModel private constructor() {

    var _message = MutableLiveData<String>()
    var _clients = MutableLiveData<List<Client>>()
    var clientProvider = ClientProvider()
    val clients : LiveData<List<Client>> =  _clients

    fun getMessage(): LiveData<String> {
        return _message
    }

    fun createClient(
        client: Client,
        idSucursal: Int,
        onComplete: (Boolean, String, String?, Boolean) -> Unit
    ) {
        var isCorrect = false
        try {
            clientProvider.create(client, idSucursal = idSucursal).addOnCompleteListener {
                if (it.isSuccessful) {
                    _message.value = "El cliente se registro correctamente"
                    isCorrect = true
                    onComplete(isCorrect, "El cliente se registro correctamente", "", false)
                }

                if (it.isCanceled) {
                    isCorrect = false
                    _message.value = "La solicitud de registro se canceló"
                    onComplete(isCorrect, "La solicitud de registro se canceló", "", false)
                }

            }.addOnFailureListener {

                var errorMessage = it.message.toString()

                _message.value = errorMessage

                isCorrect = false
                _message.value = "La solicitud no se pudo procesar, intentalo otra vez"
                onComplete(
                    isCorrect,
                    "La solicitud no se pudo procesar, intentalo otra vez",
                    "",
                    false
                )

            }

        } catch (t: Throwable) {
            var errorMessage = t.message.toString()
            println("Error : $errorMessage")

            _message.value = errorMessage

            isCorrect = false
            _message.value = "La solicitud no se proceso, contacte con soporte!"
            onComplete(isCorrect, "La solicitud no se proceso, contacte con soporte!", "", false)
        }
    }

    fun getClients(onComplete: (Boolean, String, Int?, Boolean) -> Unit)
    {
        var isCorrect = false
        var listClients = mutableListOf<Client>()

        try {

            clientProvider.getClients().addOnSuccessListener {


                isCorrect = true
                if(it.isEmpty)
                {
                    onComplete(isCorrect,"",0,false)
                }else
                {
                    it.forEach { document->
                        listClients.add(document.toObject<Client>())
                    }
                    _clients.value = listClients
                    onComplete(isCorrect,"",it.size(),false)
                }


            }.addOnFailureListener {
                println("Error: ${it.message}")
                isCorrect = false
                onComplete(isCorrect, "No se pudo obtener los clientes, porfavor comuníquese con soporte!", null, false)
            }

        }catch (t:Throwable)
        {
            println("Error throwable: ${t.message}")
            isCorrect = false
            onComplete(isCorrect, "No se pudo obtener los clientes de hoy, porfavor comuníquese con soporte!", null, false)
        }
    }

    companion object Singleton {
        private var instance: ClientsViewModel? = null

        fun getInstance(): ClientsViewModel =
            instance ?: ClientsViewModel(
                //local y remoto
            ).also { instance = it }

        fun destroyInstance() {
            instance = null
        }
    }
}
