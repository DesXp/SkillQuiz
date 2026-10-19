package pe.edu.cibertec.skillquiznuevo.database

import android.provider.BaseColumns

object SkillQuizContract {

    const val DATABASE_NAME = "skillquiz_local.db"
    const val DATABASE_VERSION = 1

    object UsuarioEntry : BaseColumns {
        const val TABLE_NAME = "usuarios"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FECHA_REGISTRO = "fecha_registro"

        const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CORREO TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    object PartidaEntry : BaseColumns {
        const val TABLE_NAME = "partidas"
        const val COLUMN_ID = BaseColumns._ID
        const val COLUMN_USUARIO_NOMBRE = "usuario_nombre"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_PUNTAJE = "puntaje"
        const val COLUMN_CORRECTAS = "correctas"
        const val COLUMN_INCORRECTAS = "incorrectas"
        const val COLUMN_TOTAL = "total_preguntas"
        const val COLUMN_PORCENTAJE = "porcentaje"
        const val COLUMN_SINCRONIZADO = "sincronizado"
        const val COLUMN_FECHA = "fecha"

        const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USUARIO_NOMBRE TEXT NOT NULL,
                $COLUMN_CORREO TEXT,
                $COLUMN_PUNTAJE INTEGER NOT NULL,
                $COLUMN_CORRECTAS INTEGER NOT NULL,
                $COLUMN_INCORRECTAS INTEGER NOT NULL,
                $COLUMN_TOTAL INTEGER NOT NULL,
                $COLUMN_PORCENTAJE INTEGER NOT NULL,
                $COLUMN_SINCRONIZADO INTEGER DEFAULT 0,
                $COLUMN_FECHA TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
