import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Credit
import com.paparazziapps.pretamistapp.modulos.registro.providers.PrestamoProvider

class LoanViewModel(private val provider: PrestamoProvider) : ViewModel() {

    // LiveData to hold the current loan data
    private val _currentLoan = MutableLiveData<Credit?>()
    val currentLoan: MutableLiveData<Credit?>
        get() = _currentLoan

    fun loadCurrentLoan() {
        try {
            var listPrestamos = mutableListOf<Credit>()
            provider.getCurrentLoan().addOnSuccessListener { prestamosFirebase ->
                if(prestamosFirebase.isEmpty) {
                    println(" lista prestamos esta vacia")
                }
                prestamosFirebase.forEach { document->
                    listPrestamos.add(document.toObject())
                    println(" lista prestamos ${listPrestamos.size}")
                }
               // _currentLoan.value = listPrestamos.find { it.actual == true }
            }

        }catch (t:Throwable)
        {
            println("Error: ${t.message}")
        }

    }
}
