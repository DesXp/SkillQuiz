
package pe.edu.cibertec.skillquiznuevo

data class Pregunta(
    val texto: String,
    val opciones: List<String>,
    val correcta: Int,
    val explicacion: String? = null
)

object BancoPreguntas {

    val preguntas = listOf(
        Pregunta("¿Qué significa CPU?",
            listOf("Unidad Central de Procesamiento", "Control de Programas", "Centro de Usuarios", "Unidad de Pantalla"), 0),

        Pregunta("¿Qué componente almacena datos temporalmente?",
            listOf("Disco duro", "RAM", "Monitor", "Teclado"), 1),

        Pregunta("¿Cuál es un sistema operativo?",
            listOf("Excel", "Chrome", "Linux", "Photoshop"), 2),

        Pregunta("¿Qué significa HTML?",
            listOf("HyperText Markup Language", "High Transfer Machine Language", "Home Text Main Link", "Hyper Tool Memory Language"), 0),

        Pregunta("¿Para qué sirve CSS?",
            listOf("Crear bases de datos", "Diseñar la apariencia web", "Instalar Windows", "Administrar redes"), 1),

        Pregunta("¿Qué lenguaje se utiliza para consultar bases de datos relacionales?",
            listOf("HTML", "CSS", "SQL", "XML"), 2),

        Pregunta("¿Cuál es un navegador web?",
            listOf("Android", "MySQL", "Ubuntu", "Firefox"), 3),

        Pregunta("¿Qué significa URL?",
            listOf("Uniform Resource Locator", "Universal Router Link", "User Remote Login", "Unified Record List"), 0),

        Pregunta("¿Qué dispositivo conecta diferentes redes?",
            listOf("Monitor", "Router", "Mouse", "Impresora"), 1),

        Pregunta("¿Qué protocolo permite navegar por sitios web mediante HTTPS?",
            listOf("FTP", "SMTP", "HTTP sobre TLS", "SSH"), 2),

        Pregunta("¿Cuál es una contraseña más segura?",
            listOf("123456", "password", "admin", "R7#mP2!vQ9@t"), 3),

        Pregunta("¿Qué significa IP?",
            listOf("Internet Protocol", "Internal Program", "Internet Password", "Input Processor"), 0),

        Pregunta("¿Qué servicio traduce dominios a direcciones IP?",
            listOf("FTP", "DNS", "SMTP", "SSH"), 1),

        Pregunta("¿Qué protocolo permite acceso remoto cifrado?",
            listOf("Telnet", "HTTP", "SSH", "FTP"), 2),

        Pregunta("¿Qué es phishing?",
            listOf("Actualizar programas", "Crear una red", "Cifrar documentos", "Engañar para obtener información"), 3),

        Pregunta("¿Qué significa LAN?",
            listOf("Local Area Network", "Long Access Number", "Linux Administration Node", "Local Android Network"), 0),

        Pregunta("¿Qué almacena archivos de forma permanente?",
            listOf("Memoria caché", "SSD", "RAM", "Registro CPU"), 1),

        Pregunta("¿Cuál es un lenguaje de programación?",
            listOf("JPEG", "MP3", "Python", "PNG"), 2),

        Pregunta("¿Qué es un algoritmo?",
            listOf("Un antivirus", "Un procesador", "Una red", "Una secuencia de pasos para resolver un problema"), 3),

        Pregunta("¿Qué representa el sistema binario?",
            listOf("Valores 0 y 1", "Valores 0 al 9", "Letras A a Z", "Solo números pares"), 0),

        Pregunta("¿Qué componente ejecuta instrucciones?",
            listOf("Monitor", "Procesador", "Impresora", "Parlante"), 1),

        Pregunta("¿Qué permite Git?",
            listOf("Editar imágenes", "Controlar hardware", "Gestionar versiones del código", "Eliminar virus"), 2),

        Pregunta("¿Qué es GitHub?",
            listOf("Un procesador", "Un sistema operativo", "Un antivirus", "Una plataforma para alojar repositorios Git"), 3),

        Pregunta("¿Para qué sirve Android Studio?",
            listOf("Desarrollar aplicaciones Android", "Diseñar circuitos", "Crear redes eléctricas", "Editar videos"), 0),

        Pregunta("¿Qué lenguaje utilizamos en SkillQuiz?",
            listOf("PHP", "Kotlin", "Ruby", "C"), 1),

        Pregunta("¿Qué es una Activity en Android?",
            listOf("Un antivirus", "Un servidor", "Un componente que puede mostrar una pantalla", "Un archivo de audio"), 2),

        Pregunta("¿Para qué se utiliza XML en nuestra aplicación?",
            listOf("Crear procesadores", "Cifrar discos", "Instalar controladores", "Definir diseños de pantalla"), 3),

        Pregunta("¿Qué es SQLite?",
            listOf("Una base de datos relacional integrada", "Un navegador", "Un antivirus", "Un sistema de archivos remoto"), 0),

        Pregunta("¿Qué significa API?",
            listOf("Advanced Program Internet", "Application Programming Interface", "Android Process Installation", "Access Private Information"), 1),

        Pregunta("¿Qué es JSON?",
            listOf("Un procesador", "Un tipo de cable", "Un formato para intercambio de datos", "Un sistema operativo"), 2)
    )
}
