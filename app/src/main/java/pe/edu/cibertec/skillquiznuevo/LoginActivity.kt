package pe.edu.cibertec.skillquiznuevo

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.edu.cibertec.skillquiznuevo.database.SkillQuizDbHelper
import pe.edu.cibertec.skillquiznuevo.network.ApiClient
import pe.edu.cibertec.skillquiznuevo.network.LoginRequest

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: SkillQuizDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = SkillQuizDbHelper(this)

        val correo = findViewById<EditText>(R.id.etCorreo)
        val password = findViewById<EditText>(R.id.etPassword)

        val login = findViewById<Button>(R.id.btnLogin)
        val registro = findViewById<Button>(R.id.btnRegistro)

        registro.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        login.setOnClickListener {
            val c = correo.text.toString().trim()
            val p = password.text.toString()

            when {
                c.isEmpty() || p.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Completa todos los campos obligatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !Patterns.EMAIL_ADDRESS.matcher(c).matches() -> {
                    correo.error = "Correo electrónico no válido"
                }

                else -> {
                    val usuarioLocal = dbHelper.validarCredenciales(c, p)

                    if (usuarioLocal != null) {
                        guardarSesionYContinuar(usuarioLocal.nombre, usuarioLocal.correo, usuarioLocal.id)
                    } else {
                        login.isEnabled = false
                        login.text = "Verificando..."

                        lifecycleScope.launch {
                            val authExitosa = withContext(Dispatchers.IO) {
                                try {
                                    when (val res = ApiClient.executeWithFallback { it.login(LoginRequest(c, p)) }) {
                                        is ApiClient.FallbackResult.Success -> {
                                            if (res.data.success && res.data.usuario != null) {
                                                val u = res.data.usuario
                                                dbHelper.insertarUsuario(u.nombre, u.correo, p)
                                                u
                                            } else null
                                        }
                                        is ApiClient.FallbackResult.Error -> null
                                    }
                                } catch (_: Exception) {
                                    null
                                }
                            }

                            login.isEnabled = true
                            login.text = "Ingresar"

                            if (authExitosa != null) {
                                guardarSesionYContinuar(authExitosa.nombre, authExitosa.correo, authExitosa.id)
                            } else {
                                password.error = "Contraseña o correo incorrectos"
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Credenciales incorrectas. Verifique sus datos o regístrese.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun guardarSesionYContinuar(nombre: String, correo: String, id: Long) {
        val datos = getSharedPreferences("SkillQuizDatos", MODE_PRIVATE)
        datos.edit()
            .putString("nombre", nombre)
            .putString("correo", correo)
            .putLong("usuario_id", id)
            .apply()

        Toast.makeText(
            this,
            "¡Bienvenido, $nombre!",
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
