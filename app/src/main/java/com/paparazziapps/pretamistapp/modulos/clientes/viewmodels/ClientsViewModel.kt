import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Client
import com.paparazziapps.pretamistapp.modulos.clientes.remote.providers.firebase.ClientProviderFirebase

class ClientsViewModel private constructor(private val clientProvider: ClientProviderFirebase) {

    private val _message = MutableLiveData<String>()
    private val _clients = MutableLiveData<List<Client>>()

    val message: LiveData<String>
        get() = _message

    val clients: LiveData<List<Client>>
        get() = _clients

    fun createClient(client: Client, idBranch: Int) {
        try {
            clientProvider.create(client, idSucursal = idBranch).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _message.value = "Client registered successfully"
                } else if (task.isCanceled) {
                    _message.value = "Registration request canceled"
                }
            }.addOnFailureListener { exception ->
                val errorMessage = exception.message ?: "Unknown error"
                _message.value = errorMessage
            }
        } catch (t: Throwable) {
            val errorMessage = t.message ?: "Unknown error"
            _message.value = errorMessage
        }
    }

    fun getClients() {
        try {
            clientProvider.getClients().addOnSuccessListener { querySnapshot ->
                val listClients = mutableListOf<Client>()
                querySnapshot.forEach { document ->
                    listClients.add(document.toObject())
                }
                _clients.value = listClients
            }.addOnFailureListener { exception ->
                val errorMessage = exception.message ?: "Unknown error"
                _message.value = errorMessage
            }
        } catch (t: Throwable) {
            val errorMessage = t.message ?: "Unknown error"
            _message.value = errorMessage
        }
    }

    companion object {
        private var instance: ClientsViewModel? = null

        fun getInstance(clientProvider: ClientProviderFirebase): ClientsViewModel =
            instance ?: ClientsViewModel(clientProvider).also { instance = it }

        fun destroyInstance() {
            instance = null
        }
    }
}