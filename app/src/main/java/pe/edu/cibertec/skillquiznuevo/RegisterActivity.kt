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
import pe.edu.cibertec.skillquiznuevo.database.SkillQuizDbHelper
import pe.edu.cibertec.skillquiznuevo.network.ApiClient
import pe.edu.cibertec.skillquiznuevo.network.RegisterRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: SkillQuizDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = SkillQuizDbHelper(this)

        val nombre = findViewById<EditText>(R.id.etNombre)
        val correo = findViewById<EditText>(R.id.etCorreoRegistro)
        val password = findViewById<EditText>(R.id.etPasswordRegistro)
        val confirmar = findViewById<EditText>(R.id.etConfirmarPassword)

        val registrar = findViewById<Button>(R.id.btnGuardarRegistro)
        val volver = findViewById<Button>(R.id.btnVolverLogin)

        registrar.setOnClickListener {
            val n = nombre.text.toString().trim()
            val c = correo.text.toString().trim()
            val p = password.text.toString()
            val cp = confirmar.text.toString()

            when {
                n.isEmpty() || c.isEmpty() || p.isEmpty() || cp.isEmpty() -> {
                    Toast.makeText(
                        this,
                        "Completa todos los campos obligatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                !Patterns.EMAIL_ADDRESS.matcher(c).matches() -> {
                    correo.error = "Correo electrónico no válido"
                }

                p.length < 6 -> {
                    password.error = "La contraseña debe tener al menos 6 caracteres"
                }

                p != cp -> {
                    confirmar.error = "Las contraseñas no coinciden"
                }

                dbHelper.existeCorreo(c) -> {
                    correo.error = "Este correo ya está registrado en el dispositivo"
                    Toast.makeText(
                        this,
                        "El correo ya se encuentra registrado. Inicia sesión.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val userId = dbHelper.insertarUsuario(n, c, p)

                    if (userId > 0) {
                        val datos = getSharedPreferences(
                            "SkillQuizDatos",
                            MODE_PRIVATE
                        )
                        datos.edit()
                            .putString("nombre", n)
                            .putString("correo", c)
                            .putLong("usuario_id", userId)
                            .apply()

                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                val req = RegisterRequest(
                                    nombre = n,
                                    correo = c,
                                    password = p
                                )
                                ApiClient.executeWithFallback { it.register(req) }
                            } catch (_: Exception) {
                            }
                        }

                        Toast.makeText(
                            this,
                            "¡Registro exitoso! Bienvenido $n",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this, HomeActivity::class.java))
                        finishAffinity()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al registrar usuario en la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        volver.setOnClickListener {
            finish()
        }
    }
}
