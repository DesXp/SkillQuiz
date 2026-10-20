package pe.edu.cibertec.skillquiznuevo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pe.edu.cibertec.skillquiznuevo.PuntajesHelper

class SkillQuizDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    SkillQuizContract.DATABASE_NAME,
    null,
    SkillQuizContract.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SkillQuizContract.UsuarioEntry.SQL_CREATE_TABLE)
        db.execSQL(SkillQuizContract.PartidaEntry.SQL_CREATE_TABLE)

        val demoUser = ContentValues().apply {
            put(SkillQuizContract.UsuarioEntry.COLUMN_NOMBRE, "Estudiante Cibertec")
            put(SkillQuizContract.UsuarioEntry.COLUMN_CORREO, "estudiante@cibertec.pe")
            put(SkillQuizContract.UsuarioEntry.COLUMN_PASSWORD, "cibertec2026")
        }
        db.insert(SkillQuizContract.UsuarioEntry.TABLE_NAME, null, demoUser)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SkillQuizContract.PartidaEntry.SQL_DROP_TABLE)
        db.execSQL(SkillQuizContract.UsuarioEntry.SQL_DROP_TABLE)
        onCreate(db)
    }

    fun insertarUsuario(nombre: String, correo: String, password: String): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(SkillQuizContract.UsuarioEntry.COLUMN_NOMBRE, nombre.trim())
            put(SkillQuizContract.UsuarioEntry.COLUMN_CORREO, correo.trim().lowercase())
            put(SkillQuizContract.UsuarioEntry.COLUMN_PASSWORD, password)
        }
        return db.insert(SkillQuizContract.UsuarioEntry.TABLE_NAME, null, valores)
    }

    fun validarCredenciales(correo: String, password: String): UsuarioLocal? {
        val db = readableDatabase
        val proyeccion = arrayOf(
            SkillQuizContract.UsuarioEntry.COLUMN_ID,
            SkillQuizContract.UsuarioEntry.COLUMN_NOMBRE,
            SkillQuizContract.UsuarioEntry.COLUMN_CORREO,
            SkillQuizContract.UsuarioEntry.COLUMN_PASSWORD,
            SkillQuizContract.UsuarioEntry.COLUMN_FECHA_REGISTRO
        )
        val seleccion = "${SkillQuizContract.UsuarioEntry.COLUMN_CORREO} = ? AND ${SkillQuizContract.UsuarioEntry.COLUMN_PASSWORD} = ?"
        val argumentos = arrayOf(correo.trim().lowercase(), password)

        val cursor = db.query(
            SkillQuizContract.UsuarioEntry.TABLE_NAME,
            proyeccion,
            seleccion,
            argumentos,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return mapearUsuario(it)
            }
        }
        return null
    }

    fun existeCorreo(correo: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            SkillQuizContract.UsuarioEntry.TABLE_NAME,
            arrayOf(SkillQuizContract.UsuarioEntry.COLUMN_ID),
            "${SkillQuizContract.UsuarioEntry.COLUMN_CORREO} = ?",
            arrayOf(correo.trim().lowercase()),
            null,
            null,
            null
        )
        cursor.use {
            return it.count > 0
        }
    }

    fun obtenerUsuarioPorCorreo(correo: String): UsuarioLocal? {
        val db = readableDatabase
        val cursor = db.query(
            SkillQuizContract.UsuarioEntry.TABLE_NAME,
            null,
            "${SkillQuizContract.UsuarioEntry.COLUMN_CORREO} = ?",
            arrayOf(correo.trim().lowercase()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return mapearUsuario(it)
            }
        }
        return null
    }

    fun actualizarPassword(correo: String, nuevoPassword: String): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(SkillQuizContract.UsuarioEntry.COLUMN_PASSWORD, nuevoPassword)
        }
        return db.update(
            SkillQuizContract.UsuarioEntry.TABLE_NAME,
            valores,
            "${SkillQuizContract.UsuarioEntry.COLUMN_CORREO} = ?",
            arrayOf(correo.trim().lowercase())
        )
    }

    fun eliminarUsuario(correo: String): Int {
        val db = writableDatabase
        return db.delete(
            SkillQuizContract.UsuarioEntry.TABLE_NAME,
            "${SkillQuizContract.UsuarioEntry.COLUMN_CORREO} = ?",
            arrayOf(correo.trim().lowercase())
        )
    }

    fun insertarPartida(
        usuarioNombre: String,
        correo: String = "",
        puntaje: Int,
        correctas: Int,
        incorrectas: Int,
        total: Int,
        porcentaje: Int,
        sincronizado: Boolean = false
    ): Long {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(SkillQuizContract.PartidaEntry.COLUMN_USUARIO_NOMBRE, usuarioNombre.trim())
            put(SkillQuizContract.PartidaEntry.COLUMN_CORREO, correo.trim().lowercase())
            put(SkillQuizContract.PartidaEntry.COLUMN_PUNTAJE, puntaje)
            put(SkillQuizContract.PartidaEntry.COLUMN_CORRECTAS, correctas)
            put(SkillQuizContract.PartidaEntry.COLUMN_INCORRECTAS, incorrectas)
            put(SkillQuizContract.PartidaEntry.COLUMN_TOTAL, total)
            put(SkillQuizContract.PartidaEntry.COLUMN_PORCENTAJE, porcentaje)
            put(SkillQuizContract.PartidaEntry.COLUMN_SINCRONIZADO, if (sincronizado) 1 else 0)
        }
        return db.insert(SkillQuizContract.PartidaEntry.TABLE_NAME, null, valores)
    }

    fun obtenerPartidas(correo: String? = null): List<PartidaLocal> {
        val db = readableDatabase
        val partidas = mutableListOf<PartidaLocal>()

        val seleccion = if (!correo.isNullOrBlank()) {
            "${SkillQuizContract.PartidaEntry.COLUMN_CORREO} = ?"
        } else null

        val argumentos = if (!correo.isNullOrBlank()) {
            arrayOf(correo.trim().lowercase())
        } else null

        val cursor = db.query(
            SkillQuizContract.PartidaEntry.TABLE_NAME,
            null,
            seleccion,
            argumentos,
            null,
            null,
            "${SkillQuizContract.PartidaEntry.COLUMN_ID} ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                partidas.add(mapearPartida(it))
            }
        }
        return partidas
    }

    fun obtenerPartidasPendientesSincronizar(): List<PartidaLocal> {
        val db = readableDatabase
        val partidas = mutableListOf<PartidaLocal>()
        val cursor = db.query(
            SkillQuizContract.PartidaEntry.TABLE_NAME,
            null,
            "${SkillQuizContract.PartidaEntry.COLUMN_SINCRONIZADO} = 0",
            null,
            null,
            null,
            "${SkillQuizContract.PartidaEntry.COLUMN_ID} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                partidas.add(mapearPartida(it))
            }
        }
        return partidas
    }

    fun marcarPartidaComoSincronizada(id: Long): Int {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put(SkillQuizContract.PartidaEntry.COLUMN_SINCRONIZADO, 1)
        }
        return db.update(
            SkillQuizContract.PartidaEntry.TABLE_NAME,
            valores,
            "${SkillQuizContract.PartidaEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun eliminarPartida(id: Long): Int {
        val db = writableDatabase
        return db.delete(
            SkillQuizContract.PartidaEntry.TABLE_NAME,
            "${SkillQuizContract.PartidaEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun limpiarHistorial(): Int {
        val db = writableDatabase
        return db.delete(SkillQuizContract.PartidaEntry.TABLE_NAME, null, null)
    }

    fun obtenerEstadisticas(correo: String? = null): PuntajesHelper.Estadisticas {
        val partidas = obtenerPartidas(correo)
        val puntos = partidas.sumOf { it.puntaje }
        val respuestasCorrectas = partidas.sumOf { it.correctas }
        val totalPreguntas = partidas.sumOf { it.total }
        val mejor = partidas.maxWithOrNull(
            compareBy<PartidaLocal> { it.porcentaje }.thenBy { it.puntaje }
        )
        val promedio = if (totalPreguntas > 0) respuestasCorrectas * 100 / totalPreguntas else 0

        return PuntajesHelper.Estadisticas(
            puntos = puntos,
            partidas = partidas.size,
            mejorPuntaje = mejor?.puntaje ?: 0,
            mejorTotal = (mejor?.total ?: 10) * 10,
            respuestasCorrectas = respuestasCorrectas,
            preguntasRespondidas = totalPreguntas,
            promedio = promedio
        )
    }

    private fun mapearUsuario(cursor: Cursor): UsuarioLocal {
        val idIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.UsuarioEntry.COLUMN_ID)
        val nombreIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.UsuarioEntry.COLUMN_NOMBRE)
        val correoIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.UsuarioEntry.COLUMN_CORREO)
        val passwordIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.UsuarioEntry.COLUMN_PASSWORD)
        val fechaIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.UsuarioEntry.COLUMN_FECHA_REGISTRO)

        return UsuarioLocal(
            id = cursor.getLong(idIndex),
            nombre = cursor.getString(nombreIndex),
            correo = cursor.getString(correoIndex),
            password = cursor.getString(passwordIndex),
            fechaRegistro = cursor.getString(fechaIndex) ?: ""
        )
    }

    private fun mapearPartida(cursor: Cursor): PartidaLocal {
        val idIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_ID)
        val usuarioIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_USUARIO_NOMBRE)
        val correoIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_CORREO)
        val puntajeIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_PUNTAJE)
        val correctasIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_CORRECTAS)
        val incorrectasIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_INCORRECTAS)
        val totalIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_TOTAL)
        val porcentajeIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_PORCENTAJE)
        val sincIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_SINCRONIZADO)
        val fechaIndex = cursor.getColumnIndexOrThrow(SkillQuizContract.PartidaEntry.COLUMN_FECHA)

        return PartidaLocal(
            id = cursor.getLong(idIndex),
            usuarioNombre = cursor.getString(usuarioIndex),
            correo = cursor.getString(correoIndex) ?: "",
            puntaje = cursor.getInt(puntajeIndex),
            correctas = cursor.getInt(correctasIndex),
            incorrectas = cursor.getInt(incorrectasIndex),
            total = cursor.getInt(totalIndex),
            porcentaje = cursor.getInt(porcentajeIndex),
            sincronizado = cursor.getInt(sincIndex) == 1,
            fecha = cursor.getString(fechaIndex) ?: ""
        )
    }
}
