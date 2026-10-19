package pe.edu.cibertec.skillquiznuevo

data class Pregunta(
    val texto: String,
    val opciones: List<String>,
    val correcta: Int
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
            listOf("Un procesador", "Un tipo de cable", "Un formato para intercambio de datos", "Un sistema operativo"), 2),

        Pregunta("¿Cuál es una característica de Java?",
            listOf("Es un formato de imagen", "Es un protocolo", "Es un tipo de memoria", "Es un lenguaje orientado a objetos"), 3),

        Pregunta("¿Qué estructura permite repetir instrucciones?",
            listOf("Bucle", "Variable", "Constante", "Comentario"), 0),

        Pregunta("¿Qué estructura permite tomar decisiones?",
            listOf("Import", "if", "package", "class"), 1),

        Pregunta("¿Qué almacena un valor en programación?",
            listOf("Un comentario", "Un paquete", "Una variable", "Un compilador"), 2),

        Pregunta("¿Qué es un bug?",
            listOf("Una actualización", "Una carpeta", "Un algoritmo", "Un error en un programa"), 3),

        Pregunta("¿Qué significa UI?",
            listOf("User Interface", "Universal Internet", "User Installation", "Unified Input"), 0),

        Pregunta("¿Qué significa UX?",
            listOf("Unix Extension", "User Experience", "Universal XML", "User Execution"), 1),

        Pregunta("¿Qué protocolo asigna IP automáticamente en muchas redes?",
            listOf("HTTPS", "FTP", "DHCP", "SSH"), 2),

        Pregunta("¿Qué función cumple un firewall?",
            listOf("Imprimir archivos", "Aumentar la RAM", "Crear usuarios", "Filtrar tráfico de red"), 3),

        Pregunta("¿Qué es malware?",
            listOf("Software malicioso", "Memoria auxiliar", "Un servidor web", "Una red local"), 0),

        Pregunta("¿Qué permite la autenticación de dos factores?",
            listOf("Eliminar contraseñas", "Añadir una segunda verificación", "Aumentar el procesador", "Duplicar la memoria"), 1),

        Pregunta("¿Qué es la nube informática?",
            listOf("Un tipo de monitor", "Un cable de red", "Recursos informáticos accesibles por red", "Un antivirus"), 2),

        Pregunta("¿Cuál es un servicio de computación en la nube?",
            listOf("Paint", "Notepad", "Calculadora", "Amazon Web Services"), 3),

        Pregunta("¿Qué significa USB?",
            listOf("Universal Serial Bus", "User Security Backup", "Unified System Base", "Universal Server Block"), 0),

        Pregunta("¿Qué memoria suele ser volátil?",
            listOf("SSD", "RAM", "Disco duro", "DVD"), 1),

        Pregunta("¿Qué es una copia de seguridad?",
            listOf("Un virus", "Un navegador", "Una copia de datos para recuperarlos", "Un procesador"), 2),

        Pregunta("¿Qué es una base de datos?",
            listOf("Un monitor", "Un cable", "Un antivirus", "Una colección organizada de datos"), 3),

        Pregunta("¿Cuál es una ventaja del software libre?",
            listOf("Permite estudiar y modificar su código según la licencia", "Siempre es gratuito", "No necesita actualizaciones", "Nunca tiene errores"), 0),

        Pregunta("¿Qué es una dirección MAC?",
            listOf("Una contraseña web", "Un identificador de interfaz de red", "Un tipo de antivirus", "Un sistema operativo"), 1),

        Pregunta("¿Qué significa IDE?",
            listOf("Internal Data Engine", "Internet Device Extension", "Integrated Development Environment", "Input Digital Element"), 2)
    )
}