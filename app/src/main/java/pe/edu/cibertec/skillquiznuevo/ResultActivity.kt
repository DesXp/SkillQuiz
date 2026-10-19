
package pe.edu.cibertec.skillquiznuevo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val puntaje = findViewById<TextView>(R.id.txtPuntaje)
        val porcentaje = findViewById<TextView>(R.id.txtPorcentaje)
        val correctasTexto = findViewById<TextView>(R.id.txtCorrectas)
        val incorrectasTexto = findViewById<TextView>(R.id.txtIncorrectas)
        val mensaje = findViewById<TextView>(R.id.txtMensaje)
        val barra = findViewById<ProgressBar>(R.id.barraResultado)

        val revisar = findViewById<Button>(
            R.id.btnRevisarRespuestas
        )

        val reintentar = findViewById<Button>(
            R.id.btnReintentar
        )

        val volver = findViewById<Button>(
            R.id.btnVolverHome
        )

        val total = intent.getIntExtra("total", 10)
            .coerceAtLeast(1)

        val correctas = intent.getIntExtra("correctas", 0)
            .coerceIn(0, total)

        val incorrectas = total - correctas
        val porcentajeFinal = correctas * 100 / total

        val historial = intent.getStringExtra(
            "historialRespuestas"
        ) ?: "[]"

        puntaje.text = "$correctas / $total"
        porcentaje.text = "$porcentajeFinal%"
        correctasTexto.text = "Correctas: $correctas"
        incorrectasTexto.text = "Incorrectas: $incorrectas"
        barra.progress = porcentajeFinal

        mensaje.text = when {
            porcentajeFinal >= 90 ->
                "¡Excelente resultado! Sigue así."

            porcentajeFinal >= 70 ->
                "¡Buen trabajo! Estás avanzando."

            porcentajeFinal >= 50 ->
                "Vas por buen camino. Sigue practicando."

            else ->
                "Sigue intentando. Cada práctica ayuda."
        }

        if (savedInstanceState == null) {

            val datos = getSharedPreferences(
                "SkillQuizDatos",
                MODE_PRIVATE
            )

            val nombre = datos.getString(
                "nombre",
                "Estudiante"
            ) ?: "Estudiante"

            PuntajesHelper(this).guardarResultado(
                nombre,
                correctas,
                total
            )

            getSharedPreferences(
                "SkillQuizRevision",
                MODE_PRIVATE
            ).edit()
                .putString("ultimoHistorial", historial)
                .apply()
        }

        revisar.setOnClickListener {
            val intentRevision = Intent(
                this,
                ReviewActivity::class.java
            )

            intentRevision.putExtra(
                "historialRespuestas",
                historial
            )

            intentRevision.putExtra(
                "soloErrores",
                false
            )

            startActivity(intentRevision)
        }

        reintentar.setOnClickListener {
            startActivity(
                Intent(this, QuizActivity::class.java)
            )
            finish()
        }

        volver.setOnClickListener {
            val intentHome = Intent(
                this,
                HomeActivity::class.java
            )

            intentHome.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intentHome)
            finish()
        }
    }
}
