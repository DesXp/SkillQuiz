
package pe.edu.cibertec.skillquiznuevo

object ExplicacionesPreguntas {

    private val explicaciones = listOf(
        "La CPU procesa instrucciones y coordina las operaciones del equipo.",
        "La RAM almacena temporalmente los datos de los programas en ejecución.",
        "Linux es un sistema operativo utilizado en computadoras y servidores.",
        "HTML es el lenguaje de marcado que estructura el contenido web.",
        "CSS permite definir colores, tamaños y estilos de una página web.",
        "SQL permite consultar y manipular información en bases de datos relacionales.",
        "Firefox es un navegador que permite acceder a sitios web.",
        "Una URL identifica la ubicación de un recurso en internet.",
        "El router conecta redes y dirige los paquetes hacia su destino.",
        "HTTPS utiliza HTTP sobre TLS para proteger la comunicación.",
        "Una contraseña larga y difícil de predecir ofrece mayor protección.",
        "IP es el protocolo utilizado para el direccionamiento de equipos en redes.",
        "DNS permite resolver nombres de dominio a direcciones IP.",
        "SSH permite administrar equipos remotos mediante conexiones cifradas.",
        "El phishing utiliza engaños para conseguir datos personales o credenciales.",
        "Una LAN conecta equipos dentro de un área local.",
        "Un SSD mantiene los archivos almacenados incluso cuando se apaga el equipo.",
        "Python es un lenguaje utilizado en desarrollo, automatización y análisis de datos.",
        "Un algoritmo describe pasos ordenados para resolver un problema.",
        "El sistema binario representa información mediante los dígitos cero y uno.",
        "El procesador ejecuta instrucciones de los programas.",
        "Git permite registrar cambios y administrar versiones del código.",
        "GitHub permite alojar y colaborar en repositorios Git.",
        "Android Studio es el entorno de desarrollo para aplicaciones Android.",
        "Kotlin es el lenguaje que estamos utilizando para programar SkillQuiz.",
        "Una Activity es un componente Android que puede presentar una interfaz.",
        "XML permite declarar la estructura de los diseños de nuestras pantallas.",
        "SQLite es un motor de base de datos relacional integrado.",
        "Una API define mecanismos para que distintos programas se comuniquen.",
        "JSON es un formato de texto para representar e intercambiar datos."
    )

    fun obtener(indice: Int): String {
        return explicaciones.getOrElse(indice) {
            "Repasa este concepto y vuelve a practicar."
        }
    }
}
