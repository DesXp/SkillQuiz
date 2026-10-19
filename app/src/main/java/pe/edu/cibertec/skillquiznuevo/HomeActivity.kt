
package pe.edu.cibertec.skillquiznuevo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bienvenida = findViewById<TextView>(
            R.id.txtBienvenida
        )

        val jugar = findViewById<Button>(R.id.btnJugar)
        val ranking = findViewById<Button>(R.id.btnRanking)
        val mejorar = findViewById<Button>(R.id.btnMejorar)
        val cerrar = findViewById<Button>(R.id.btnCerrarSesion)

        val datos = getSharedPreferences(
            "SkillQuizDatos",
            MODE_PRIVATE
        )

        val nombre = datos.getString(
            "nombre",
            "Estudiante"
        )

        bienvenida.text = "¡Hola, $nombre!"

        jugar.setOnClickListener {
            startActivity(
                Intent(this, QuizActivity::class.java)
            )
        }

        ranking.setOnClickListener {
            startActivity(
                Intent(this, RankingActivity::class.java)
            )
        }

        mejorar.setOnClickListener {
            startActivity(
                Intent(this, ImproveActivity::class.java)
            )
        }

        cerrar.setOnClickListener {
            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }
}
