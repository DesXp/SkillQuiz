const { DatabaseSync } = require('node:sqlite');
const path = require('node:path');

const dbPath = path.join(__dirname, 'skillquiz.db');
const db = new DatabaseSync(dbPath);

function initDatabase() {
    db.exec(`
        CREATE TABLE IF NOT EXISTS usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            correo TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            rol TEXT DEFAULT 'ESTUDIANTE',
            fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS categorias (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT UNIQUE NOT NULL,
            descripcion TEXT
        );

        CREATE TABLE IF NOT EXISTS preguntas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            categoria_id INTEGER NOT NULL REFERENCES categorias(id),
            texto TEXT NOT NULL,
            opcion_a TEXT NOT NULL,
            opcion_b TEXT NOT NULL,
            opcion_c TEXT NOT NULL,
            opcion_d TEXT NOT NULL,
            correcta INTEGER NOT NULL,
            explicacion TEXT NOT NULL,
            dificultad TEXT DEFAULT 'Media',
            creado_en DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS partidas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            usuario_nombre TEXT NOT NULL,
            correo TEXT,
            puntaje INTEGER NOT NULL,
            correctas INTEGER NOT NULL,
            incorrectas INTEGER NOT NULL,
            total_preguntas INTEGER NOT NULL DEFAULT 10,
            porcentaje INTEGER NOT NULL,
            fecha DATETIME DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS detalle_partidas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            partida_id INTEGER NOT NULL REFERENCES partidas(id),
            pregunta_id INTEGER NOT NULL REFERENCES preguntas(id),
            respuesta_usuario INTEGER NOT NULL,
            es_correcta INTEGER NOT NULL
        );
    `);

    const checkAdmin = db.prepare('SELECT id FROM usuarios WHERE correo = ?').get('admin@skillquiz.edu.pe');
    if (!checkAdmin) {
        db.prepare(`
            INSERT INTO usuarios (nombre, correo, password, rol)
            VALUES (?, ?, ?, ?)
        `).run('Administrador', 'admin@skillquiz.edu.pe', 'admin123', 'ADMIN');
    }

    const categorias = [
        { nombre: 'Hardware', descripcion: 'Componentes físicos, procesadores y memorias' },
        { nombre: 'Redes y Conectividad', descripcion: 'Protocolos, direccionamiento IP y topologías' },
        { nombre: 'Sistemas y Software', descripcion: 'Sistemas operativos, navegadores y herramientas' },
        { nombre: 'Programación y Algoritmos', descripcion: 'Lógica, lenguajes, estructuras de control y Android' },
        { nombre: 'Seguridad Informática', descripcion: 'Buenas prácticas, amenazas, malware y cifrado' },
        { nombre: 'Bases de Datos', descripcion: 'Modelado relacional, SQL y persistencia' }
    ];

    const insertCat = db.prepare('INSERT OR IGNORE INTO categorias (nombre, descripcion) VALUES (?, ?)');
    for (const c of categorias) {
        insertCat.run(c.nombre, c.descripcion);
    }

    const catMap = {};
    const catRows = db.prepare('SELECT id, nombre FROM categorias').all();
    for (const row of catRows) {
        catMap[row.nombre] = row.id;
    }

    const countPreguntas = db.prepare('SELECT COUNT(*) as count FROM preguntas').get();
    if (countPreguntas.count === 0) {
        const banco = [
            {
                texto: '¿Qué significa CPU?',
                opciones: ['Unidad Central de Procesamiento', 'Control de Programas', 'Centro de Usuarios', 'Unidad de Pantalla'],
                correcta: 0,
                categoria: 'Hardware',
                explicacion: 'CPU son las siglas de Central Processing Unit, considerado el cerebro del computador que procesa las instrucciones.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué componente almacena datos temporalmente?',
                opciones: ['Disco duro', 'RAM', 'Monitor', 'Teclado'],
                correcta: 1,
                categoria: 'Hardware',
                explicacion: 'La memoria RAM es volátil; almacena los datos de los programas en ejecución mientras el equipo está encendido.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Cuál es un sistema operativo?',
                opciones: ['Excel', 'Chrome', 'Linux', 'Photoshop'],
                correcta: 2,
                categoria: 'Sistemas y Software',
                explicacion: 'Linux es un sistema operativo de código abierto tipo Unix creado originalmente por Linus Torvalds.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué significa HTML?',
                opciones: ['HyperText Markup Language', 'High Transfer Machine Language', 'Home Text Main Link', 'Hyper Tool Memory Language'],
                correcta: 0,
                categoria: 'Sistemas y Software',
                explicacion: 'HTML es el lenguaje de marcado estándar utilizado para estructurar páginas web en Internet.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Para qué sirve CSS?',
                opciones: ['Crear bases de datos', 'Diseñar la apariencia web', 'Instalar Windows', 'Administrar redes'],
                correcta: 1,
                categoria: 'Sistemas y Software',
                explicacion: 'CSS (Cascading Style Sheets) define el diseño, estilos, colores y distribución visual de los documentos HTML.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué lenguaje se utiliza para consultar bases de datos relacionales?',
                opciones: ['HTML', 'CSS', 'SQL', 'XML'],
                correcta: 2,
                categoria: 'Bases de Datos',
                explicacion: 'SQL (Structured Query Language) es el estándar internacional para consultar, manipular y gestionar bases de datos relacionales.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Cuál es un navegador web?',
                opciones: ['Android', 'MySQL', 'Ubuntu', 'Firefox'],
                correcta: 3,
                categoria: 'Sistemas y Software',
                explicacion: 'Mozilla Firefox es un navegador web multiplataforma desarrollado por la Fundación Mozilla.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué significa URL?',
                opciones: ['Uniform Resource Locator', 'Universal Router Link', 'User Remote Login', 'Unified Record List'],
                correcta: 0,
                categoria: 'Redes y Conectividad',
                explicacion: 'URL (Localizador Uniforme de Recursos) es la dirección específica asignada a cada recurso disponible en la web.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué dispositivo conecta diferentes redes?',
                opciones: ['Monitor', 'Router', 'Mouse', 'Impresora'],
                correcta: 1,
                categoria: 'Redes y Conectividad',
                explicacion: 'El router o enrutador opera en la capa 3 (Red) del modelo OSI y reenvía paquetes entre redes distintas.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué protocolo permite navegar por sitios web mediante HTTPS?',
                opciones: ['FTP', 'SMTP', 'HTTP sobre TLS', 'SSH'],
                correcta: 2,
                categoria: 'Redes y Conectividad',
                explicacion: 'HTTPS combina HTTP con el protocolo criptográfico TLS/SSL para garantizar una conexión cifrada y segura.',
                dificultad: 'Media'
            },
            {
                texto: '¿Cuál es una contraseña más segura?',
                opciones: ['123456', 'password', 'admin', 'R7#mP2!vQ9@t'],
                correcta: 3,
                categoria: 'Seguridad Informática',
                explicacion: 'Una contraseña segura combina mayúsculas, minúsculas, números, caracteres especiales y longitud adecuada.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué significa IP?',
                opciones: ['Internet Protocol', 'Internal Program', 'Internet Password', 'Input Processor'],
                correcta: 0,
                categoria: 'Redes y Conectividad',
                explicacion: 'IP significa Protocolo de Internet y es el estándar de comunicación encargado del direccionamiento de red.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué servicio traduce dominios a direcciones IP?',
                opciones: ['FTP', 'DNS', 'SMTP', 'SSH'],
                correcta: 1,
                categoria: 'Redes y Conectividad',
                explicacion: 'DNS (Domain Name System) traduce nombres legibles como cibertec.edu.pe en direcciones IP numéricas.',
                dificultad: 'Media'
            },
            {
                texto: '¿Qué protocolo permite acceso remoto cifrado?',
                opciones: ['Telnet', 'HTTP', 'SSH', 'FTP'],
                correcta: 2,
                categoria: 'Seguridad Informática',
                explicacion: 'SSH (Secure Shell) reemplazó a Telnet al ofrecer un canal de comunicación totalmente cifrado para administración remota.',
                dificultad: 'Media'
            },
            {
                texto: '¿Qué es phishing?',
                opciones: ['Actualizar programas', 'Crear una red', 'Cifrar documentos', 'Engañar para obtener información'],
                correcta: 3,
                categoria: 'Seguridad Informática',
                explicacion: 'El phishing es una técnica de ingeniería social para suplantar identidades legítimas y robar credenciales bancarias o personales.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué significa LAN?',
                opciones: ['Local Area Network', 'Long Access Number', 'Linux Administration Node', 'Local Android Network'],
                correcta: 0,
                categoria: 'Redes y Conectividad',
                explicacion: 'LAN es una red de área local que interconecta equipos dentro de un espacio geográfico delimitado (hogar, oficina).',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué almacena archivos de forma permanente?',
                opciones: ['Memoria caché', 'SSD', 'RAM', 'Registro CPU'],
                correcta: 1,
                categoria: 'Hardware',
                explicacion: 'Las unidades de estado sólido (SSD) utilizan memoria flash no volátil para conservar archivos aun sin suministro eléctrico.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Cuál es un lenguaje de programación?',
                opciones: ['JPEG', 'MP3', 'Python', 'PNG'],
                correcta: 2,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Python es un lenguaje de programación interpretado de alto nivel muy popular en ciencia de datos y backend.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es un algoritmo?',
                opciones: ['Un antivirus', 'Un procesador', 'Una red', 'Una secuencia de pasos para resolver un problema'],
                correcta: 3,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Un algoritmo es un conjunto finito, ordenado y unívoco de instrucciones para realizar una tarea o resolver un problema.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué representa el sistema binario?',
                opciones: ['Valores 0 y 1', 'Valores 0 al 9', 'Letras A a Z', 'Solo números pares'],
                correcta: 0,
                categoria: 'Hardware',
                explicacion: 'El sistema binario utiliza la base 2 con los dígitos 0 y 1 para representar los estados de los circuitos digitales.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué componente ejecuta instrucciones?',
                opciones: ['Monitor', 'Procesador', 'Impresora', 'Parlante'],
                correcta: 1,
                categoria: 'Hardware',
                explicacion: 'El procesador se encarga de buscar, decodificar y ejecutar las instrucciones de máquina de los programas.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué permite Git?',
                opciones: ['Editar imágenes', 'Controlar hardware', 'Gestionar versiones del código', 'Eliminar virus'],
                correcta: 2,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Git es un sistema de control de versiones distribuido que rastrea los cambios en el código fuente de los proyectos.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es GitHub?',
                opciones: ['Un procesador', 'Un sistema operativo', 'Un antivirus', 'Una plataforma para alojar repositorios Git'],
                correcta: 3,
                categoria: 'Programación y Algoritmos',
                explicacion: 'GitHub es una plataforma en la nube que facilita la colaboración, alojamiento y control de versiones usando Git.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Para qué sirve Android Studio?',
                opciones: ['Desarrollar aplicaciones Android', 'Diseñar circuitos', 'Crear redes eléctricas', 'Editar videos'],
                correcta: 0,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Android Studio es el Entorno de Desarrollo Integrado (IDE) oficial respaldado por Google para crear apps de Android.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué lenguaje utilizamos en SkillQuiz?',
                opciones: ['PHP', 'Kotlin', 'Ruby', 'C'],
                correcta: 1,
                categoria: 'Programación y Algoritmos',
                explicacion: 'SkillQuiz está desarrollado en Kotlin, el lenguaje oficial moderno recomendado por Google para Android.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es una Activity en Android?',
                opciones: ['Un antivirus', 'Un servidor', 'Un componente que puede mostrar una pantalla', 'Un archivo de audio'],
                correcta: 2,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Una Activity es un componente fundamental que provee una ventana en la cual la app dibuja su interfaz de usuario.',
                dificultad: 'Media'
            },
            {
                texto: '¿Para qué se utiliza XML en nuestra aplicación?',
                opciones: ['Crear procesadores', 'Cifrar discos', 'Instalar controladores', 'Definir diseños de pantalla'],
                correcta: 3,
                categoria: 'Programación y Algoritmos',
                explicacion: 'En Android, los archivos XML en res/layout se usan para estructurar declarativamente las interfaces de usuario.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es SQLite?',
                opciones: ['Una base de datos relacional integrada', 'Un navegador', 'Un antivirus', 'Un sistema de archivos remoto'],
                correcta: 0,
                categoria: 'Bases de Datos',
                explicacion: 'SQLite es un motor de base de datos SQL embebido y sin servidor que se ejecuta directamente en la memoria y disco del dispositivo.',
                dificultad: 'Media'
            },
            {
                texto: '¿Qué significa API?',
                opciones: ['Advanced Program Internet', 'Application Programming Interface', 'Android Process Installation', 'Access Private Information'],
                correcta: 1,
                categoria: 'Programación y Algoritmos',
                explicacion: 'API (Interfaz de Programación de Aplicaciones) define contratos y métodos para que dos sistemas de software se comuniquen.',
                dificultad: 'Media'
            },
            {
                texto: '¿Qué es JSON?',
                opciones: ['Un procesador', 'Un tipo de cable', 'Un formato para intercambio de datos', 'Un sistema operativo'],
                correcta: 2,
                categoria: 'Programación y Algoritmos',
                explicacion: 'JSON (JavaScript Object Notation) es un formato de texto ligero, estructurado y universalmente usado en APIs web.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Cuál es una característica de Java?',
                opciones: ['Es un formato de imagen', 'Es un protocolo', 'Es un tipo de memoria', 'Es un lenguaje orientado a objetos'],
                correcta: 3,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Java es un lenguaje fuertemente tipado basado en clases y orientado a objetos, ejecutable en la JVM.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué estructura permite repetir instrucciones?',
                opciones: ['Bucle', 'Variable', 'Constante', 'Comentario'],
                correcta: 0,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Los bucles (como for, while y do-while) repiten bloques de código mientras se cumpla una condición determinada.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué estructura permite tomar decisiones?',
                opciones: ['Import', 'if', 'package', 'class'],
                correcta: 1,
                categoria: 'Programación y Algoritmos',
                explicacion: 'La estructura if evalúa una condición booleana para ejecutar un flujo u otro en el programa.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué almacena un valor en programación?',
                opciones: ['Un comentario', 'Un paquete', 'Una variable', 'Un compilador'],
                correcta: 2,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Una variable es un espacio de memoria con un nombre simbólico que almacena un valor modificable.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es un bug?',
                opciones: ['Una actualización', 'Una carpeta', 'Un algoritmo', 'Un error en un programa'],
                correcta: 3,
                categoria: 'Programación y Algoritmos',
                explicacion: 'En informática, un bug es un error o defecto en el código que produce un resultado incorrecto o inesperado.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué significa UI?',
                opciones: ['User Interface', 'Universal Internet', 'User Installation', 'Unified Input'],
                correcta: 0,
                categoria: 'Sistemas y Software',
                explicacion: 'UI (Interfaz de Usuario) comprende todos los elementos visuales e interactivos con los que una persona interactúa en una app.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué significa UX?',
                opciones: ['Unix Extension', 'User Experience', 'Universal XML', 'User Execution'],
                correcta: 1,
                categoria: 'Sistemas y Software',
                explicacion: 'UX (Experiencia de Usuario) se enfoca en cómo se siente y cuán intuitiva y satisfactoria resulta la interacción del usuario.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué protocolo asigna IP automáticamente en muchas redes?',
                opciones: ['HTTPS', 'FTP', 'DHCP', 'SSH'],
                correcta: 2,
                categoria: 'Redes y Conectividad',
                explicacion: 'DHCP (Dynamic Host Configuration Protocol) asigna dinámicamente direcciones IP y parámetros de red a los clientes.',
                dificultad: 'Media'
            },
            {
                texto: '¿Qué función cumple un firewall?',
                opciones: ['Imprimir archivos', 'Aumentar la RAM', 'Crear usuarios', 'Filtrar tráfico de red'],
                correcta: 3,
                categoria: 'Seguridad Informática',
                explicacion: 'Un firewall inspecciona y filtra el tráfico de red entrante y saliente según políticas de seguridad predefinidas.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es malware?',
                opciones: ['Software malicioso', 'Memoria auxiliar', 'Un servidor web', 'Una red local'],
                correcta: 0,
                categoria: 'Seguridad Informática',
                explicacion: 'Malware engloba a todo software diseñado para dañar, infiltrarse o causar un mal funcionamiento en sistemas informáticos.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué permite la autenticación de dos factores?',
                opciones: ['Eliminar contraseñas', 'Añadir una segunda verificación', 'Aumentar el procesador', 'Duplicar la memoria'],
                correcta: 1,
                categoria: 'Seguridad Informática',
                explicacion: 'La autenticación de doble factor (2FA) exige dos pruebas de identidad distintas antes de conceder acceso al sistema.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es la nube informática?',
                opciones: ['Un tipo de monitor', 'Un cable de red', 'Recursos informáticos accesibles por red', 'Un antivirus'],
                correcta: 2,
                categoria: 'Sistemas y Software',
                explicacion: 'Cloud computing es la entrega bajo demanda de servidores, almacenamiento, bases de datos y software a través de Internet.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Cuál es un servicio de computación en la nube?',
                opciones: ['Paint', 'Notepad', 'Calculadora', 'Amazon Web Services'],
                correcta: 3,
                categoria: 'Sistemas y Software',
                explicacion: 'Amazon Web Services (AWS) es una de las plataformas de infraestructura cloud más grandes y extendidas del mundo.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué significa USB?',
                opciones: ['Universal Serial Bus', 'User Security Backup', 'Unified System Base', 'Universal Server Block'],
                correcta: 0,
                categoria: 'Hardware',
                explicacion: 'USB (Bus Universal en Serie) es el estándar de conexión física e intercambio de datos entre periféricos y computadoras.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué memoria suele ser volátil?',
                opciones: ['SSD', 'RAM', 'Disco duro', 'DVD'],
                correcta: 1,
                categoria: 'Hardware',
                explicacion: 'La memoria RAM pierde su contenido inmediatamente al apagarse o interrumpirse la energía eléctrica.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es una copia de seguridad?',
                opciones: ['Un virus', 'Un navegador', 'Una copia de datos para recuperarlos', 'Un procesador'],
                correcta: 2,
                categoria: 'Seguridad Informática',
                explicacion: 'Un backup es un respaldo de la información que permite restaurarla ante incidentes, fallos de disco o pérdidas de datos.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Qué es una base de datos?',
                opciones: ['Un monitor', 'Un cable', 'Un antivirus', 'Una colección organizada de datos'],
                correcta: 3,
                categoria: 'Bases de Datos',
                explicacion: 'Una base de datos es un conjunto de información estructurada y organizada electrónicamente en un sistema informático.',
                dificultad: 'Básica'
            },
            {
                texto: '¿Cuál es una ventaja del software libre?',
                opciones: ['Permite estudiar y modificar su código según la licencia', 'Siempre es gratuito', 'No necesita actualizaciones', 'Nunca tiene errores'],
                correcta: 0,
                categoria: 'Sistemas y Software',
                explicacion: 'El software libre garantiza las libertades de ejecutar, estudiar, modificar y redistribuir el código respetando su licencia.',
                dificultad: 'Media'
            },
            {
                texto: '¿Qué es una dirección MAC?',
                opciones: ['Una contraseña web', 'Un identificador de interfaz de red', 'Un tipo de antivirus', 'Un sistema operativo'],
                correcta: 1,
                categoria: 'Redes y Conectividad',
                explicacion: 'La dirección MAC es un identificador único de 48 bits asignado por el fabricante a cada tarjeta de red física (capa de enlace).',
                dificultad: 'Media'
            },
            {
                texto: '¿Qué significa IDE?',
                opciones: ['Internal Data Engine', 'Internet Device Extension', 'Integrated Development Environment', 'Input Digital Element'],
                correcta: 2,
                categoria: 'Programación y Algoritmos',
                explicacion: 'Un IDE (Entorno de Desarrollo Integrado) reúne editor de código, depurador y compilador en una misma suite de trabajo.',
                dificultad: 'Básica'
            }
        ];

        const insertPregunta = db.prepare(`
            INSERT INTO preguntas (categoria_id, texto, opcion_a, opcion_b, opcion_c, opcion_d, correcta, explicacion, dificultad)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        `);

        for (const item of banco) {
            const catId = catMap[item.categoria] || 1;
            insertPregunta.run(
                catId,
                item.texto,
                item.opciones[0],
                item.opciones[1],
                item.opciones[2],
                item.opciones[3],
                item.correcta,
                item.explicacion,
                item.dificultad
            );
        }
    }
}

initDatabase();

module.exports = db;
