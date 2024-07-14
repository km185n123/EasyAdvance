import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Credit

class CustomInfoWindowAdapter(private val inflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        // Usa getInfoContents en lugar de getInfoWindow si quieres usar solo el contenido del layout
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        // Inflar el layout personalizado
        val view = inflater.inflate(R.layout.custom_info_window, null)


        // Referencias a los componentes del layout
       // val imageView = view.findViewById<ImageView>(R.id.info_window_image)
        val titleView = view.findViewById<TextView>(R.id.info_window_title)
        val snippetView = view.findViewById<TextView>(R.id.info_window_snippet)



        // Configurar los valores de la burbuja


        val client = Gson().fromJson(marker.title, Credit::class.java)
       /* titleView.text = "${client.nombres} ${client.apellidos}"
        snippetView.text = client.fechaUltimoPago*/
        // Aquí puedes configurar la imagen como prefieras, por ejemplo, usando una URL con una librería como Glide
        // Glide.with(view.context).load(url).into(imageView)

        return view
    }
}
