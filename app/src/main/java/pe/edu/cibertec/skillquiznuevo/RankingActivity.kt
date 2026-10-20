package pe.edu.cibertec.skillquiznuevo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.edu.cibertec.skillquiznuevo.database.SkillQuizDbHelper
import pe.edu.cibertec.skillquiznuevo.network.ApiClient
import pe.edu.cibertec.skillquiznuevo.network.RankingItemDto

class RankingActivity : AppCompatActivity() {

    private lateinit var helper: PuntajesHelper
    private lateinit var dbHelper: SkillQuizDbHelper

    private var tabActual: Int = 0

    private lateinit var btnTabMisPartidas: TextView
    private lateinit var btnTabTopEstudiantes: TextView
    private lateinit var txtSubtituloSeccion: TextView
    private lateinit var progresoCarga: ProgressBar
    private lateinit var rvHistorial: RecyclerView
    private lateinit var txtVacio: TextView
    private lateinit var btnLimpiarHistorial: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        helper = PuntajesHelper(this)
        dbHelper = SkillQuizDbHelper(this)

        val jugar = findViewById<Button>(R.id.btnJugarRanking)
        val volver = findViewById<Button>(R.id.btnVolverRanking)

        btnTabMisPartidas = findViewById(R.id.btnTabMisPartidas)
        btnTabTopEstudiantes = findViewById(R.id.btnTabTopEstudiantes)
        txtSubtituloSeccion = findViewById(R.id.txtSubtituloSeccion)
        progresoCarga = findViewById(R.id.progresoCargaRanking)
        rvHistorial = findViewById(R.id.rvHistorialRanking)
        txtVacio = findViewById(R.id.txtVacioRanking)
        btnLimpiarHistorial = findViewById(R.id.btnLimpiarHistorial)

        rvHistorial.layoutManager = LinearLayoutManager(this)

        btnTabMisPartidas.setOnClickListener {
            if (tabActual != 0) {
                tabActual = 0
                actualizarEstiloTabs()
                mostrarPartidasLocales()
            }
        }

        btnTabTopEstudiantes.setOnClickListener {
            tabActual = 1
            actualizarEstiloTabs()
            cargarTopEstudiantes()
        }

        btnLimpiarHistorial.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Borrar historial local")
                .setMessage("¿Deseas eliminar todas las partidas guardadas en este dispositivo?")
                .setPositiveButton("Eliminar") { _, _ ->
                    helper.limpiarHistorialCompleto()
                    getSharedPreferences("SkillQuizRevision", MODE_PRIVATE).edit().clear().apply()
                    Toast.makeText(this, "Historial local eliminado definitivamente", Toast.LENGTH_SHORT).show()
                    mostrarEstadisticasGenerales()
                    mostrarPartidasLocales()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        jugar.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        volver.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        helper.sincronizarPendientes()
        mostrarEstadisticasGenerales()
        if (tabActual == 0) {
            mostrarPartidasLocales()
        } else {
            cargarTopEstudiantes()
        }
    }

    private fun actualizarEstiloTabs() {
        if (tabActual == 0) {
            btnTabMisPartidas.setBackgroundResource(R.drawable.fondo_banner)
            btnTabMisPartidas.setTextColor(Color.WHITE)
            btnTabTopEstudiantes.setBackgroundColor(Color.TRANSPARENT)
            btnTabTopEstudiantes.setTextColor(Color.parseColor("#64748B"))
            txtSubtituloSeccion.text = "Historial personal guardado en este dispositivo."
        } else {
            btnTabTopEstudiantes.setBackgroundResource(R.drawable.fondo_banner)
            btnTabTopEstudiantes.setTextColor(Color.WHITE)
            btnTabMisPartidas.setBackgroundColor(Color.TRANSPARENT)
            btnTabMisPartidas.setTextColor(Color.parseColor("#64748B"))
            txtSubtituloSeccion.text = "Clasificación general de estudiantes sincronizados."
        }
    }

    private fun mostrarEstadisticasGenerales() {
        val estadisticas = helper.obtenerEstadisticas()

        val nombre = getSharedPreferences(
            "SkillQuizDatos",
            MODE_PRIVATE
        ).getString("nombre", "Estudiante") ?: "Estudiante"

        findViewById<TextView>(R.id.txtNombreRanking).text = "Jugador: $nombre"
        findViewById<TextView>(R.id.txtPuntosAcumulados).text = "${estadisticas.puntos} puntos"
        findViewById<TextView>(R.id.txtPartidasJugadas).text = estadisticas.partidas.toString()
        findViewById<TextView>(R.id.txtMejorPartida).text = if (estadisticas.partidas > 0) {
            "${estadisticas.mejorPuntaje}/${estadisticas.mejorTotal}"
        } else {
            "0/10"
        }

        findViewById<TextView>(R.id.txtPromedioRanking).text = "${estadisticas.promedio}% de aciertos"
        findViewById<ProgressBar>(R.id.barraPromedioRanking).progress = estadisticas.promedio
    }

    private fun mostrarPartidasLocales() {
        progresoCarga.visibility = View.GONE
        val partidasSqlite = dbHelper.obtenerPartidas()

        if (partidasSqlite.isEmpty()) {
            txtVacio.text = "Todavía no tienes partidas guardadas."
            txtVacio.visibility = View.VISIBLE
            rvHistorial.visibility = View.GONE
            btnLimpiarHistorial.visibility = View.GONE
            return
        }

        txtVacio.visibility = View.GONE
        rvHistorial.visibility = View.VISIBLE
        btnLimpiarHistorial.visibility = View.VISIBLE

        val acumulados = IntArray(partidasSqlite.size)
        var suma = 0
        partidasSqlite.forEachIndexed { i, p ->
            suma += p.puntaje
            acumulados[i] = suma
        }

        val itemsRanking = partidasSqlite
            .withIndex()
            .toList()
            .takeLast(10)
            .reversed()
            .map { elemento ->
                val p = elemento.value
                val idx = elemento.index
                RankingAdapter.ItemRanking(
                    numeroPartida = idx + 1,
                    correctas = p.correctas,
                    total = p.total,
                    porcentaje = p.porcentaje,
                    puntosAcumulados = acumulados[idx],
                    sincronizado = p.sincronizado
                )
            }

        rvHistorial.adapter = RankingAdapter(itemsRanking)
    }

    private fun cargarTopEstudiantes() {
        btnLimpiarHistorial.visibility = View.GONE
        val nombreUsuarioActual = getSharedPreferences(
            "SkillQuizDatos",
            MODE_PRIVATE
        ).getString("nombre", "Estudiante") ?: "Estudiante"

        progresoCarga.visibility = View.VISIBLE
        txtVacio.visibility = View.GONE

        Log.d("RANKING_DEBUG", "Cargando top estudiantes desde URL: ${ApiClient.getBaseUrl()}")

        lifecycleScope.launch {
            var mensajeError: String? = null
            val lista = withContext(Dispatchers.IO) {
                when (val res = ApiClient.executeWithFallback { it.getRanking() }) {
                    is ApiClient.FallbackResult.Success -> {
                        Log.d("RANKING_DEBUG", "Ranking cargado con éxito desde: ${res.origin}")
                        res.data
                    }
                    is ApiClient.FallbackResult.Error -> {
                        Log.w("RANKING_DEBUG", "Fallback: ${res.message}. Cargando desde SQLite local.")
                        mensajeError = res.message
                        obtenerTopDesdeSqliteLocal()
                    }
                }
            }

            Log.d("RANKING_DEBUG", "Lista final a mostrar: ${lista.map { it.usuarioNombre }}")
            progresoCarga.visibility = View.GONE

            if (mensajeError != null && lista.isEmpty()) {
                Toast.makeText(
                    this@RankingActivity,
                    "Modo sin conexión: sin datos sincronizados",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (lista.isEmpty()) {
                txtVacio.text = "No se encontraron estudiantes en el ranking institucional."
                txtVacio.visibility = View.VISIBLE
                rvHistorial.visibility = View.GONE
            } else {
                txtVacio.visibility = View.GONE
                rvHistorial.visibility = View.VISIBLE
                rvHistorial.adapter = TopEstudiantesAdapter(lista, nombreUsuarioActual)
            }
        }
    }

    private fun obtenerTopDesdeSqliteLocal(): List<RankingItemDto> {
        val partidas = dbHelper.obtenerPartidas()
        if (partidas.isEmpty()) return emptyList()

        return partidas.groupBy { it.usuarioNombre }
            .map { (usuario, listaPartidas) ->
                val mejor = listaPartidas.maxOf { it.puntaje }
                val totalAciertos = listaPartidas.sumOf { it.correctas }
                val promedio = listaPartidas.map { it.porcentaje }.average()
                val correo = listaPartidas.firstOrNull()?.correo ?: ""
                RankingItemDto(
                    usuarioNombre = usuario,
                    correo = correo,
                    partidasJugadas = listaPartidas.size,
                    mejorPuntaje = mejor,
                    totalAciertos = totalAciertos,
                    promedioAciertos = promedio
                )
            }
            .sortedWith(compareByDescending<RankingItemDto> { it.mejorPuntaje }.thenByDescending { it.totalAciertos })
    }
}
