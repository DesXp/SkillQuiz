
package pe.edu.cibertec.skillquiznuevo

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class ReviewActivity : AppCompatActivity() {

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }

    private fun crearTexto(
        texto: String,
        tamano: Float,
        color: String,
        negrita: Boolean = false
    ): TextView {
        return TextView(this).apply {
            this.text = texto
            setTextSize(TypedValue.COMPLEX_UNIT_SP, tamano)
            setTextColor(Color.parseColor(color))

            if (negrita) {
                setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
                )
            }

            setPadding(0, dp(5), 0, dp(5))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val contenedor = findViewById<LinearLayout>(
            R.id.contenedorRespuestas
        )

        val volver = findViewById<Button>(
            R.id.btnVolverResultado
        )

        val datos = intent.getStringExtra(
            "historialRespuestas"
        ) ?: "[]"

        val soloErrores = intent.getBooleanExtra(
            "soloErrores",
            false
        )

        val historial = try {
            JSONArray(datos)
        } catch (e: Exception) {
            JSONArray()
        }

        var erroresEncontrados = 0

        for (i in 0 until historial.length()) {

            val dato = historial.getJSONObject(i)

            val pregunta = dato.optString("pregunta")
            val usuario = dato.optString("respuestaUsuario")
            val correcta = dato.optString("respuestaCorrecta")
            val acierto = dato.optBoolean("correcta")
            val indice = dato.optInt("indicePregunta", -1)

            if (soloErrores && acierto) {
                continue
            }

            if (!acierto) {
                erroresEncontrados++
            }

            val tarjeta = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(18), dp(17), dp(18), dp(17))
                setBackgroundResource(R.drawable.fondo_tarjeta)
            }

            val parametros = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(15)
            }

            val estado = when {
                acierto -> "CORRECTA"
                usuario == "Sin responder" -> "TIEMPO AGOTADO"
                else -> "INCORRECTA"
            }

            tarjeta.addView(
                crearTexto(
                    "PREGUNTA ${i + 1}  •  $estado",
                    13f,
                    if (acierto) "#248578" else "#C45B50",
                    true
                )
            )

            tarjeta.addView(
                crearTexto(
                    pregunta,
                    18f,
                    "#183153",
                    true
                )
            )

            tarjeta.addView(
                crearTexto(
                    "Tu respuesta: $usuario",
                    15f,
                    if (acierto) "#248578" else "#C45B50"
                )
            )

            tarjeta.addView(
                crearTexto(
                    "Respuesta correcta: $correcta",
                    15f,
                    "#248578",
                    true
                )
            )

            val explicacionTexto = dato.optString("explicacion").ifEmpty {
                ExplicacionesPreguntas.obtener(indice)
            }

            tarjeta.addView(
                crearTexto(
                    "Explicación: $explicacionTexto",
                    14f,
                    "#64748B"
                )
            )

            contenedor.addView(tarjeta, parametros)
        }

        if (historial.length() == 0) {
            contenedor.addView(
                crearTexto(
                    "Todavía no hay respuestas para revisar.",
                    16f,
                    "#64748B"
                )
            )
        } else if (soloErrores && erroresEncontrados == 0) {
            contenedor.addView(
                crearTexto(
                    "¡Excelente! No tuviste errores en tu último cuestionario.",
                    17f,
                    "#248578",
                    true
                )
            )
        }

        volver.text = if (soloErrores) {
            "Volver a Mi progreso"
        } else {
            "Volver a resultados"
        }

        volver.setOnClickListener {
            finish()
        }
    }
}
