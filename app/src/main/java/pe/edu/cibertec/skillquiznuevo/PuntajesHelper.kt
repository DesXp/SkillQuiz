package pe.edu.cibertec.skillquiznuevo

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import pe.edu.cibertec.skillquiznuevo.database.SkillQuizDbHelper
import pe.edu.cibertec.skillquiznuevo.network.ApiClient
import pe.edu.cibertec.skillquiznuevo.network.PartidaRequest

class PuntajesHelper(private val context: Context) {

    private val dbHelper = SkillQuizDbHelper(context)

    private val preferencias = context.getSharedPreferences(
        "SkillQuizPuntajes",
        Context.MODE_PRIVATE
    )

    data class Resultado(
        val nombre: String,
        val correctas: Int,
        val total: Int
    ) {
        val porcentaje: Int
            get() = if (total > 0) {
                correctas * 100 / total
            } else {
                0
            }
    }

    data class Estadisticas(
        val puntos: Int,
        val partidas: Int,
        val mejorPuntaje: Int,
        val mejorTotal: Int,
        val respuestasCorrectas: Int,
        val preguntasRespondidas: Int,
        val promedio: Int
    )

    init {
        migrarDesdePreferenciasSiEsNecesario()
    }

    fun guardarResultado(
        nombre: String,
        correctas: Int,
        total: Int
    ) {
        if (total <= 0 || correctas !in 0..total) {
            return
        }

        val datosUsuario = context.getSharedPreferences("SkillQuizDatos", Context.MODE_PRIVATE)
        val correo = datosUsuario.getString("correo", "") ?: ""

        val puntaje = correctas * 10
        val incorrectas = total - correctas
        val porcentaje = (correctas * 100) / total

        val partidaId = dbHelper.insertarPartida(
            usuarioNombre = nombre,
            correo = correo,
            puntaje = puntaje,
            correctas = correctas,
            incorrectas = incorrectas,
            total = total,
            porcentaje = porcentaje,
            sincronizado = false
        )

        guardarEnPreferencias(nombre, correctas, total)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = PartidaRequest(
                    nombre = nombre,
                    correo = correo,
                    correctas = correctas,
                    incorrectas = incorrectas,
                    total = total,
                    puntaje = puntaje
                )
                val res = ApiClient.executeWithFallback { it.sincronizarPartida(request) }
                if (res is ApiClient.FallbackResult.Success && res.data.success) {
                    if (partidaId > 0) {
                        dbHelper.marcarPartidaComoSincronizada(partidaId)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun obtenerResultados(): List<Resultado> {
        val partidasSqlite = dbHelper.obtenerPartidas()
        if (partidasSqlite.isNotEmpty()) {
            return partidasSqlite.map {
                Resultado(
                    nombre = it.usuarioNombre,
                    correctas = it.correctas,
                    total = it.total
                )
            }
        }
        return obtenerResultadosDePreferencias()
    }

    fun obtenerUltimoResultado(): Resultado? {
        return obtenerResultados().lastOrNull()
    }

    fun obtenerPuntosAcumulados(): Int {
        return obtenerResultados().sumOf { it.correctas }
    }

    fun obtenerPartidasJugadas(): Int {
        return obtenerResultados().size
    }

    fun obtenerEstadisticas(): Estadisticas {
        return dbHelper.obtenerEstadisticas()
    }

    private fun guardarEnPreferencias(nombre: String, correctas: Int, total: Int) {
        val listaPref = JSONArray(preferencias.getString("historial", "[]") ?: "[]")
        listaPref.put(
            JSONObject().apply {
                put("nombre", nombre)
                put("correctas", correctas)
                put("total", total)
            }
        )
        preferencias.edit().putString("historial", listaPref.toString()).apply()
    }

    private fun obtenerResultadosDePreferencias(): List<Resultado> {
        val historial = preferencias.getString("historial", "[]") ?: "[]"
        val lista = try { JSONArray(historial) } catch (_: Exception) { JSONArray() }
        val resultados = mutableListOf<Resultado>()
        for (i in 0 until lista.length()) {
            val dato = lista.optJSONObject(i) ?: continue
            val correctas = dato.optInt("correctas", 0)
            val total = dato.optInt("total", 10)
            if (total > 0 && correctas in 0..total) {
                resultados.add(
                    Resultado(
                        nombre = dato.optString("nombre", "Estudiante"),
                        correctas = correctas,
                        total = total
                    )
                )
            }
        }
        return resultados
    }

    private fun migrarDesdePreferenciasSiEsNecesario() {
        val partidasSqlite = dbHelper.obtenerPartidas()
        if (partidasSqlite.isEmpty()) {
            val partidasPref = obtenerResultadosDePreferencias()
            partidasPref.forEach {
                val porcentaje = if (it.total > 0) it.correctas * 100 / it.total else 0
                dbHelper.insertarPartida(
                    usuarioNombre = it.nombre,
                    correo = "",
                    puntaje = it.correctas * 10,
                    correctas = it.correctas,
                    incorrectas = it.total - it.correctas,
                    total = it.total,
                    porcentaje = porcentaje,
                    sincronizado = false
                )
            }
        }
    }
}
