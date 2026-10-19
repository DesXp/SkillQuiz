package pe.edu.cibertec.skillquiznuevo.database

data class UsuarioLocal(
    val id: Long = 0,
    val nombre: String,
    val correo: String,
    val password: String = "",
    val fechaRegistro: String = ""
)

data class PartidaLocal(
    val id: Long = 0,
    val usuarioNombre: String,
    val correo: String = "",
    val puntaje: Int,
    val correctas: Int,
    val incorrectas: Int,
    val total: Int,
    val porcentaje: Int,
    val sincronizado: Boolean = false,
    val fecha: String = ""
)
