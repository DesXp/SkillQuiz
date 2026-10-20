package pe.edu.cibertec.skillquiznuevo.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface QuizApiService {

    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): Response<AuthResponse>

    @GET("preguntas")
    suspend fun getPreguntas(
        @Query("limite") limite: Int? = 10,
        @Query("categoria_id") categoriaId: Long? = null
    ): Response<List<PreguntaDto>>

    @POST("partidas")
    suspend fun sincronizarPartida(
        @Body body: PartidaRequest
    ): Response<PartidaResponse>

    @GET("ranking")
    suspend fun getRanking(): Response<List<RankingItemDto>>
}
