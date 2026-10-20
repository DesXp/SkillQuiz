
package pe.edu.cibertec.skillquiznuevo

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class QuizActivity : AppCompatActivity() {

    private val banco = BancoPreguntas.preguntas
    private val cantidad = 10
    private val tiempoMaximo = 30000L

    private lateinit var indices: IntArray
    private lateinit var ordenes: Array<IntArray>

    private var actual = 0
    private var correctas = 0
    private var tiempoRestante = tiempoMaximo
    private var fechaLimite = 0L
    private var terminado = false

    private var temporizador: CountDownTimer? = null

    private lateinit var historial: JSONArray

    private lateinit var numero: TextView
    private lateinit var pregunta: TextView
    private lateinit var progreso: ProgressBar
    private lateinit var grupo: RadioGroup
    private lateinit var siguiente: Button
    private lateinit var opciones: List<RadioButton>
    private lateinit var txtTiempo: TextView
    private lateinit var barraTiempo: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        numero = findViewById(R.id.txtNumeroPregunta)
        pregunta = findViewById(R.id.txtPregunta)
        progreso = findViewById(R.id.progresoQuiz)
        grupo = findViewById(R.id.grupoRespuestas)
        siguiente = findViewById(R.id.btnSiguiente)
        txtTiempo = findViewById(R.id.txtTiempo)
        barraTiempo = findViewById(R.id.barraTiempo)

        opciones = listOf(
            findViewById(R.id.opcion1),
            findViewById(R.id.opcion2),
            findViewById(R.id.opcion3),
            findViewById(R.id.opcion4)
        )

        if (savedInstanceState == null) {
            prepararPartida()
            historial = JSONArray()
        } else {
            indices = savedInstanceState.getIntArray(
                "indices"
            ) ?: intArrayOf()

            val plano = savedInstanceState.getIntArray(
                "ordenes"
            ) ?: intArrayOf()

            if (indices.size != cantidad ||
                plano.size != cantidad * 4) {
                prepararPartida()
                historial = JSONArray()
            } else {
                ordenes = Array(cantidad) { i ->
                    plano.copyOfRange(i * 4, i * 4 + 4)
                }

                actual = savedInstanceState.getInt("actual")
                correctas = savedInstanceState.getInt("correctas")

                tiempoRestante = savedInstanceState.getLong(
                    "tiempoRestante",
                    tiempoMaximo
                )

                historial = JSONArray(
                    savedInstanceState.getString(
                        "historial",
                        "[]"
                    ) ?: "[]"
                )
            }
        }

        mostrarPregunta()

        if (savedInstanceState != null) {
            val seleccion = savedInstanceState.getInt(
                "seleccion",
                -1
            )

            if (seleccion in opciones.indices) {
                opciones[seleccion].isChecked = true
            }
        }

        siguiente.setOnClickListener {
            val seleccion = opciones.indexOfFirst {
                it.isChecked
            }

            if (seleccion == -1) {
                Toast.makeText(
                    this,
                    "Selecciona una respuesta",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            responder(seleccion)
        }

        iniciarTemporizador()
    }

    private fun prepararPartida() {
        val preferencias = getSharedPreferences(
            "SkillQuizPreguntas",
            MODE_PRIVATE
        )

        val recientes = preferencias.getString(
            "recientes",
            ""
        ).orEmpty()
            .split(",")
            .mapNotNull { it.toIntOrNull() }

        val disponibles = banco.indices
            .filter { it !in recientes.toSet() }
            .shuffled()

        val elegidas = if (disponibles.size >= cantidad) {
            disponibles.take(cantidad)
        } else {
            banco.indices.shuffled().take(cantidad)
        }

        indices = elegidas.toIntArray()

        ordenes = Array(cantidad) {
            (0..3).shuffled().toIntArray()
        }

        val nuevosRecientes = (
                recientes + elegidas
                ).takeLast(cantidad * 2)

        preferencias.edit()
            .putString(
                "recientes",
                nuevosRecientes.joinToString(",")
            )
            .apply()

        actual = 0
        correctas = 0
        tiempoRestante = tiempoMaximo
    }

    private fun mostrarPregunta() {
        val dato = banco[indices[actual]]

        numero.text = "Pregunta ${actual + 1} de $cantidad"
        pregunta.text = dato.texto

        grupo.clearCheck()

        for (i in opciones.indices) {
            opciones[i].text = dato.opciones[ordenes[actual][i]]
        }

        progreso.max = cantidad
        progreso.progress = actual + 1

        siguiente.text = if (actual == cantidad - 1) {
            "Finalizar cuestionario"
        } else {
            "Siguiente pregunta"
        }
    }

    private fun iniciarTemporizador() {
        temporizador?.cancel()

        if (terminado) return

        tiempoRestante = tiempoRestante.coerceAtLeast(0L)
        fechaLimite = SystemClock.elapsedRealtime() +
                tiempoRestante

        if (tiempoRestante == 0L) {
            responder(-1)
            return
        }

        temporizador = object : CountDownTimer(
            tiempoRestante,
            100L
        ) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = (
                        fechaLimite - SystemClock.elapsedRealtime()
                        ).coerceAtLeast(0L)

                actualizarTiempo()
            }

            override fun onFinish() {
                tiempoRestante = 0L
                actualizarTiempo()
                responder(-1)
            }
        }.start()

        actualizarTiempo()
    }

    private fun actualizarTiempo() {
        val segundos = (
                (tiempoRestante + 999L) / 1000L
                ).toInt()

        txtTiempo.text = String.format(
            java.util.Locale.getDefault(),
            "00:%02d",
            segundos
        )

        txtTiempo.setTextColor(
            android.graphics.Color.parseColor(
                if (segundos <= 10) "#C45B50" else "#2956A3"
            )
        )

        barraTiempo.max = tiempoMaximo.toInt()
        barraTiempo.progress = tiempoRestante.toInt()
    }

    private fun responder(seleccion: Int) {
        if (terminado) return

        val agotado = SystemClock.elapsedRealtime() >= fechaLimite
        val seleccionFinal = if (agotado) -1 else seleccion

        temporizador?.cancel()

        val dato = banco[indices[actual]]

        val respuestaOriginal =
            if (seleccionFinal in opciones.indices) {
                ordenes[actual][seleccionFinal]
            } else {
                -1
            }

        val esCorrecta = respuestaOriginal == dato.correcta

        if (esCorrecta) {
            correctas++
        }

        val registro = JSONObject().apply {
            put("pregunta", dato.texto)
            put(
                "respuestaUsuario",
                if (respuestaOriginal >= 0) {
                    dato.opciones[respuestaOriginal]
                } else {
                    "Sin responder"
                }
            )
            put(
                "respuestaCorrecta",
                dato.opciones[dato.correcta]
            )
            put("correcta", esCorrecta)
            put("indicePregunta", indices[actual])
        }

        historial.put(registro)

        actual++

        if (actual < cantidad) {
            tiempoRestante = tiempoMaximo
            mostrarPregunta()
            iniciarTemporizador()
        } else {
            terminado = true

            val intent = Intent(
                this,
                ResultActivity::class.java
            )

            intent.putExtra("correctas", correctas)
            intent.putExtra("total", cantidad)
            intent.putExtra(
                "historialRespuestas",
                historial.toString()
            )

            startActivity(intent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (!terminado) {
            tiempoRestante = (
                    fechaLimite - SystemClock.elapsedRealtime()
                    ).coerceAtLeast(0L)
        }

        outState.putIntArray("indices", indices)
        outState.putIntArray(
            "ordenes",
            ordenes.flatMap { it.toList() }.toIntArray()
        )
        outState.putInt("actual", actual)
        outState.putInt("correctas", correctas)
        outState.putLong("tiempoRestante", tiempoRestante)
        outState.putString("historial", historial.toString())
        outState.putInt(
            "seleccion",
            opciones.indexOfFirst { it.isChecked }
        )
    }

    override fun onDestroy() {
        temporizador?.cancel()
        super.onDestroy()
    }
}
