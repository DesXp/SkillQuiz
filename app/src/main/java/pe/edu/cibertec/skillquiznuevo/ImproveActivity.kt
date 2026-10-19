package pe.edu.cibertec.skillquiznuevo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class ImproveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_improve)

        val puntaje = findViewById<TextView>(
            R.id.txtUltimoPuntaje
        )

        val porcentaje = findViewById<TextView>(
            R.id.txtPorcentajeMejora
        )

        val correctas = findViewById<TextView>(
            R.id.txtCorrectasMejora
        )

        val incorrectas = findViewById<TextView>(
            R.id.txtIncorrectasMejora
        )

        val progreso = findViewById<ProgressBar>(
            R.id.progresoMejora
        )

        val consejos = findViewById<TextView>(
            R.id.txtConsejos
        )

        val verErrores = findViewById<Button>(
            R.id.btnVerErrores
        )

        val practicar = findViewById<Button>(
            R.id.btnPracticarOtraVez
        )

        val volver = findViewById<Button>(
            R.id.btnVolverMejora
        )

        val helper = PuntajesHelper(this)
        val ultimo = helper.obtenerUltimoResultado()

        val revision = getSharedPreferences(
            "SkillQuizRevision",
            MODE_PRIVATE
        )

        val historial = revision.getString(
            "ultimoHistorial",
            "[]"
        ) ?: "[]"

        val cantidadRevisiones = try {
            JSONArray(historial).length()
        } catch (e: Exception) {
            0
        }

        verErrores.isEnabled = cantidadRevisiones > 0

        if (ultimo == null) {

            puntaje.text = "Sin resultados"
            porcentaje.text = "Completa tu primer cuestionario"
            correctas.text = "0"
            incorrectas.text = "0"
            progreso.progress = 0

            consejos.text =
                "Todavía no has completado un cuestionario.\n\n" +
                        "Empieza a practicar para conocer tu nivel."

        } else {

            val errores = ultimo.total - ultimo.correctas

            puntaje.text =
                "${ultimo.correctas} / ${ultimo.total}"

            porcentaje.text =
                "${ultimo.porcentaje}% de respuestas correctas"

            correctas.text = ultimo.correctas.toString()
            incorrectas.text = errores.toString()

            progreso.progress = ultimo.porcentaje

            consejos.text = when {
                ultimo.porcentaje >= 90 -> {
                    "¡Excelente resultado!\n\n" +
                            "• Mantén tu ritmo de práctica.\n" +
                            "• Revisa las preguntas que fallaste.\n" +
                            "• Intenta alcanzar el 100%."
                }

                ultimo.porcentaje >= 70 -> {
                    "¡Buen trabajo!\n\n" +
                            "• Revisa tus respuestas incorrectas.\n" +
                            "• Lee las explicaciones para aprender.\n" +
                            "• Vuelve a practicar para mejorar."
                }

                ultimo.porcentaje >= 50 -> {
                    "Vas por buen camino.\n\n" +
                            "• Identifica las preguntas que fallaste.\n" +
                            "• Repasa sus respuestas correctas.\n" +
                            "• Realiza otro cuestionario."
                }

                else -> {
                    "No te desanimes.\n\n" +
                            "• Revisa tus errores uno por uno.\n" +
                            "• Estudia las explicaciones.\n" +
                            "• Practica nuevamente."
                }
            }
        }

        verErrores.setOnClickListener {
            val intent = Intent(
                this,
                ReviewActivity::class.java
            )

            intent.putExtra(
                "historialRespuestas",
                historial
            )

            intent.putExtra("soloErrores", true)

            startActivity(intent)
        }

        practicar.setOnClickListener {
            startActivity(
                Intent(this, QuizActivity::class.java)
            )
            finish()
        }

        volver.setOnClickListener {
            finish()
        }
    }
}