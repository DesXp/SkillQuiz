package pe.edu.cibertec.skillquiznuevo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etRegName = findViewById<EditText>(R.id.etRegName)
        val etRegEmail = findViewById<EditText>(R.id.etRegEmail)
        val etRegPassword = findViewById<EditText>(R.id.etRegPassword)
        val btnDoRegister = findViewById<Button>(R.id.btnDoRegister)
        val btnBackToLogin = findViewById<Button>(R.id.btnBackToLogin)

        btnDoRegister.setOnClickListener(View.OnClickListener {
            val name = etRegName.text.toString().trim()
            val email = etRegEmail.text.toString().trim()
            val password = etRegPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    baseContext,
                    "Por favor, completa todos los campos de registro",
                    Toast.LENGTH_LONG
                ).show()
                return@OnClickListener
            }

            Toast.makeText(baseContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
            finish()
        })

        btnBackToLogin.setOnClickListener {
            finish()
        }
    }
}