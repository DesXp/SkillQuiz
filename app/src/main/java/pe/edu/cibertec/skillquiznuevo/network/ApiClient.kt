package pe.edu.cibertec.skillquiznuevo.network

import android.os.Build
import android.util.Log
import okhttp3.OkHttpClient
import pe.edu.cibertec.skillquiznuevo.BuildConfig
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val cloudClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val localClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val cloudRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(sanitizeUrl(BuildConfig.CLOUD_API_URL))
            .client(cloudClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val localRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(sanitizeUrl(getLocalBaseUrl()))
            .client(localClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val cloudService: QuizApiService by lazy { cloudRetrofit.create(QuizApiService::class.java) }
    val localService: QuizApiService by lazy { localRetrofit.create(QuizApiService::class.java) }

    val service: QuizApiService
        get() = cloudService

    sealed class FallbackResult<out T> {
        data class Success<out T>(val data: T, val origin: String) : FallbackResult<T>()
        data class Error(val message: String) : FallbackResult<Nothing>()
    }

    suspend fun <T> executeWithFallback(
        call: suspend (QuizApiService) -> Response<T>
    ): FallbackResult<T> {
        try {
            val response = call(cloudService)
            if (response.isSuccessful && response.body() != null) {
                return FallbackResult.Success(response.body()!!, "Nube")
            }
        } catch (e: Exception) {
            Log.w("API_CLIENT", "Nube no disponible: ${e.localizedMessage ?: e.message}")
        }

        try {
            val response = call(localService)
            if (response.isSuccessful && response.body() != null) {
                return FallbackResult.Success(response.body()!!, "Servidor Local")
            }
        } catch (e: Exception) {
            Log.w("API_CLIENT", "Servidor Local no disponible: ${e.localizedMessage ?: e.message}")
        }

        return FallbackResult.Error("No se pudo conectar ni a la Nube ni al Servidor Local")
    }

    fun getLocalBaseUrl(): String {
        return if (isEmulator()) {
            "http://10.0.2.2:3000/api/"
        } else {
            BuildConfig.LOCAL_API_URL
        }
    }

    fun getBaseUrl(): String = BuildConfig.CLOUD_API_URL

    private fun sanitizeUrl(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
    }
}
