package pe.edu.cibertec.skillquiznuevo.network

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String,
    @SerializedName("rol") val rol: String = "estudiante"
)

data class UsuarioDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("rol") val rol: String?
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("usuario") val usuario: UsuarioDto? = null,
    @SerializedName("error") val error: String? = null
)

data class PreguntaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("texto") val texto: String,
    @SerializedName("opciones") val opciones: List<String>,
    @SerializedName("correcta") val correcta: Int,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("categoria_id") val categoriaId: Long?,
    @SerializedName("explicacion") val explicacion: String?,
    @SerializedName("dificultad") val dificultad: String?
)

data class PartidaRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("correctas") val correctas: Int,
    @SerializedName("incorrectas") val incorrectas: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("puntaje") val puntaje: Int
)

data class PartidaResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("partida_id") val partidaId: Long? = null,
    @SerializedName("puntaje") val puntaje: Int? = null,
    @SerializedName("porcentaje") val porcentaje: Int? = null,
    @SerializedName("error") val error: String? = null
)

data class RankingItemDto(
    @SerializedName("usuario_nombre") val usuarioNombre: String,
    @SerializedName("correo") val correo: String?,
    @SerializedName("partidas_jugadas") val partidasJugadas: Int,
    @SerializedName("mejor_puntaje") val mejorPuntaje: Int,
    @SerializedName("total_aciertos") val totalAciertos: Int,
    @SerializedName("promedio_aciertos") val promedioAciertos: Double
)

data class HealthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("service") val service: String,
    @SerializedName("version") val version: String,
    @SerializedName("timestamp") val timestamp: String
)
