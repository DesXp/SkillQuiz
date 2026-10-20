
package pe.edu.cibertec.skillquiznuevo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pe.edu.cibertec.skillquiznuevo.network.ApiClient
import pe.edu.cibertec.skillquiznuevo.network.RegisterRequest

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Sincronización en segundo plano: preguntas, perfil del estudiante y partidas pendientes
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Pre-cargar preguntas desde la API de Render para inicio instantáneo
                val resp = ApiClient.executeWithFallback { it.getPreguntas(limite = 30) }
                if (resp is ApiClient.FallbackResult.Success && resp.data.isNotEmpty()) {
                    QuizActivity.bancoRemoto = resp.data.map { dto ->
                        Pregunta(
                            texto = dto.texto,
                            opciones = dto.opciones,
                            correcta = dto.correcta,
                            explicacion = dto.explicacion
                        )
                    }
                }

                // 2. Sincronizar automáticamente el usuario local en Render si no estaba sincronizado
                val prefs = getSharedPreferences("SkillQuizDatos", MODE_PRIVATE)
                val usuarioCorreo = prefs.getString("correo", null)
                val usuarioNombre = prefs.getString("nombre", null)
                if (!usuarioCorreo.isNullOrBlank() && !usuarioNombre.isNullOrBlank()) {
                    val syncReq = RegisterRequest(
                        nombre = usuarioNombre,
                        correo = usuarioCorreo,
                        password = "auto_sync_account"
                    )
                    ApiClient.executeWithFallback { it.register(syncReq) }
                }

                // 3. Sincronizar partidas pendientes de SQLite a Render
                PuntajesHelper(this@HomeActivity).sincronizarPendientes()
            } catch (_: Exception) {}
        }

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
