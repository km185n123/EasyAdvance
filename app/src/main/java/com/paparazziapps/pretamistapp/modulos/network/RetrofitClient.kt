import com.google.gson.GsonBuilder
import com.paparazziapps.pretamistapp.helper.views.MyTrustManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object RetrofitClient {
    private const val BASE_URL = "http://138.197.55.241:3001/"
    val client: Retrofit
        get() {
            // Configurar un TrustManager personalizado
            val trustManager: X509TrustManager = MyTrustManager()
            return try {
                // Crear un SSLContext con el TrustManager personalizado
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, arrayOf<TrustManager>(trustManager), null)

                // Configurar OkHttpClient con el SSLContext y el TrustManager
                val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.socketFactory, trustManager)
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                // Configurar Retrofit con OkHttpClient personalizado
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Error configuring Retrofit", e)
            } catch (e: KeyManagementException) {
                throw RuntimeException("Error configuring Retrofit", e)
            }
        }
}
