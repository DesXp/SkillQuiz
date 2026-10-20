let todasLasPreguntas = [];
let categoriasDisponibles = [];
let chartCategoriasInstance = null;
let chartNivelesInstance = null;

document.addEventListener('DOMContentLoaded', () => {
    const userJson = sessionStorage.getItem('skillquiz_user');
    if (!userJson) {
        window.location.href = 'index.html';
        return;
    }

    try {
        const user = JSON.parse(userJson);
        const nameLabel = document.getElementById('userNameLabel');
        if (nameLabel) nameLabel.innerText = user.nombre || 'Prof. Administrador';
    } catch (e) {}

    cargarCategorias();
    fetchMetrics();
    cargarPreguntas();
});

function logout() {
    sessionStorage.removeItem('skillquiz_user');
    window.location.href = 'index.html';
}

function switchTab(tab) {
    const secMetrics = document.getElementById('sectionMetrics');
    const secQuestions = document.getElementById('sectionQuestions');
    const navMetrics = document.getElementById('navMetrics');
    const navQuestions = document.getElementById('navQuestions');

    if (tab === 'metrics') {
        secMetrics.style.display = 'block';
        secQuestions.style.display = 'none';
        navMetrics.classList.add('active');
        navQuestions.classList.remove('active');
        fetchMetrics();
    } else {
        secMetrics.style.display = 'none';
        secQuestions.style.display = 'block';
        navMetrics.classList.remove('active');
        navQuestions.classList.add('active');
        filtrarPreguntas();
    }
}

async function cargarCategorias() {
    try {
        const res = await fetch('/api/categorias');
        categoriasDisponibles = await res.json();

        const selectFiltro = document.getElementById('selectFiltroCategoria');
        const selectModal = document.getElementById('preguntaCategoria');

        selectFiltro.innerHTML = '<option value="">Todas las categorías</option>';
        selectModal.innerHTML = '';

        categoriasDisponibles.forEach(c => {
            selectFiltro.innerHTML += `<option value="${c.id}">${c.nombre}</option>`;
            selectModal.innerHTML += `<option value="${c.id}">${c.nombre}</option>`;
        });
    } catch (err) {
        console.error('Error al cargar categorías:', err);
    }
}

async function fetchMetrics() {
    try {
        const res = await fetch('/api/metricas');
        const data = await res.json();

        document.getElementById('kpiPartidas').innerText = data.kpis.totalPartidas;
        document.getElementById('kpiPromedio').innerText = data.kpis.promedioGeneral + '%';
        document.getElementById('kpiPreguntas').innerText = data.kpis.totalPreguntas;
        document.getElementById('kpiCategorias').innerText = data.kpis.totalCategorias;

        renderChartCategorias(data.rendimientoPorCategoria);

        renderChartNiveles(data.distribucionNivel);

        renderPreguntasFalladas(data.preguntasMasFalladas);

    } catch (err) {
        console.error('Error al cargar métricas:', err);
    }
}

function renderChartCategorias(datos) {
    const ctx = document.getElementById('chartCategorias').getContext('2d');
    if (chartCategoriasInstance) chartCategoriasInstance.destroy();

    const labels = datos.map(d => d.categoria);
    const valores = datos.map(d => d.porcentaje);

    chartCategoriasInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '% de Aciertos',
                data: valores,
                backgroundColor: [
                    'rgba(99, 102, 241, 0.85)',
                    'rgba(168, 85, 247, 0.85)',
                    'rgba(59, 130, 246, 0.85)',
                    'rgba(16, 185, 129, 0.85)',
                    'rgba(245, 158, 11, 0.85)',
                    'rgba(6, 182, 212, 0.85)'
                ],
                borderRadius: 8,
                borderSkipped: false
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: (c) => ` Rendimiento: ${c.raw}%`
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    grid: { color: 'rgba(255, 255, 255, 0.05)' },
                    ticks: { color: '#94a3b8', callback: v => v + '%' }
                },
                x: {
                    grid: { display: false },
                    ticks: { color: '#94a3b8', font: { size: 11 } }
                }
            }
        }
    });
}

function renderChartNiveles(niveles) {
    const ctx = document.getElementById('chartNiveles').getContext('2d');
    if (chartNivelesInstance) chartNivelesInstance.destroy();

    chartNivelesInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Sobresaliente (≥80%)', 'Aprobado (50-79%)', 'En Desarrollo (<50%)'],
            datasets: [{
                data: [niveles.sobresaliente, niveles.aprobado, niveles.enDesarrollo],
                backgroundColor: [
                    '#10b981',
                    '#6366f1',
                    '#f43f5e'
                ],
                borderWidth: 0,
                hoverOffset: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { color: '#94a3b8', boxWidth: 12, padding: 14 }
                }
            },
            cutout: '68%'
        }
    });
}

function renderPreguntasFalladas(preguntas) {
    const tbody = document.getElementById('tablaPreguntasFalladas');
    tbody.innerHTML = '';

    preguntas.forEach(p => {
        tbody.innerHTML += `
            <tr>
                <td style="font-weight: 500;">${p.texto}</td>
                <td><span class="badge badge-cat">${p.categoria}</span></td>
                <td>
                    <span class="badge ${p.tasaError > 40 ? 'badge-danger' : 'badge-warning'}">
                        ${p.tasaError}% error
                    </span>
                </td>
                <td style="color: #94a3b8; font-size: 0.85rem;">Reforzar en sesión de laboratorio práctico</td>
            </tr>
        `;
    });
}

async function cargarPreguntas() {
    try {
        const res = await fetch('/api/preguntas');
        todasLasPreguntas = await res.json();
        filtrarPreguntas();
    } catch (err) {
        console.error('Error al cargar preguntas:', err);
    }
}

function filtrarPreguntas() {
    const searchVal = document.getElementById('inputBuscarPregunta').value.toLowerCase().trim();
    const catVal = document.getElementById('selectFiltroCategoria').value;

    const filtradas = todasLasPreguntas.filter(p => {
        const matchTexto = p.texto.toLowerCase().includes(searchVal) ||
                           p.opciones.some(o => o.toLowerCase().includes(searchVal));
        const matchCat = catVal === '' || String(p.categoria_id) === catVal;
        return matchTexto && matchCat;
    });

    renderTablaPreguntas(filtradas);
}

function renderTablaPreguntas(lista) {
    const tbody = document.getElementById('tablaPreguntasBody');
    tbody.innerHTML = '';

    if (lista.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 32px; color: #64748b;">No se encontraron preguntas coincidentes.</td></tr>`;
        return;
    }

    lista.forEach(p => {
        const opcionesHtml = p.opciones.map((op, idx) => {
            const isCorrect = idx === p.correcta;
            return `<div style="font-size: 0.85rem; margin-bottom: 2px; color: ${isCorrect ? '#34d399' : '#94a3b8'};">
                <strong style="color: ${isCorrect ? '#10b981' : '#64748b'};">${String.fromCharCode(65 + idx)})</strong> ${op} ${isCorrect ? '✓' : ''}
            </div>`;
        }).join('');

        const dificultadBadge = p.dificultad === 'Básica' ? 'badge-success' : (p.dificultad === 'Media' ? 'badge-warning' : 'badge-danger');

        tbody.innerHTML += `
            <tr>
                <td style="color: #64748b; font-weight: 600;">#${p.id}</td>
                <td>
                    <div style="font-weight: 600; margin-bottom: 4px;">${p.texto}</div>
                    <div style="font-size: 0.8rem; color: #64748b;">💡 ${p.explicacion}</div>
                </td>
                <td><span class="badge badge-cat">${p.categoria}</span></td>
                <td>${opcionesHtml}</td>
                <td><span class="badge ${dificultadBadge}">${p.dificultad || 'Media'}</span></td>
                <td style="text-align: right;">
                    <div class="action-btns" style="justify-content: flex-end;">
                        <button class="btn-icon btn-edit" title="Editar pregunta" onclick="openModalEditarPregunta(${p.id})">✏️</button>
                        <button class="btn-icon btn-delete" title="Eliminar pregunta" onclick="eliminarPregunta(${p.id})">🗑️</button>
                    </div>
                </td>
            </tr>
        `;
    });
}

function openModalNuevaPregunta() {
    document.getElementById('modalPreguntaTitulo').innerText = 'Registrar Nueva Pregunta';
    document.getElementById('formPregunta').reset();
    document.getElementById('preguntaId').value = '';
    document.getElementById('radio0').checked = true;
    document.getElementById('modalPregunta').classList.add('active');
}

function openModalEditarPregunta(id) {
    const p = todasLasPreguntas.find(item => item.id === id);
    if (!p) return;

    document.getElementById('modalPreguntaTitulo').innerText = `Editar Pregunta #${p.id}`;
    document.getElementById('preguntaId').value = p.id;
    document.getElementById('preguntaTexto').value = p.texto;
    document.getElementById('preguntaCategoria').value = p.categoria_id;
    document.getElementById('preguntaDificultad').value = p.dificultad || 'Media';
    document.getElementById('preguntaExplicacion').value = p.explicacion;

    document.getElementById('opcion0').value = p.opciones[0] || '';
    document.getElementById('opcion1').value = p.opciones[1] || '';
    document.getElementById('opcion2').value = p.opciones[2] || '';
    document.getElementById('opcion3').value = p.opciones[3] || '';

    const radios = document.getElementsByName('opcionCorrectaRadio');
    for (let r of radios) {
        if (Number(r.value) === p.correcta) {
            r.checked = true;
            break;
        }
    }

    document.getElementById('modalPregunta').classList.add('active');
}

function cerrarModalPregunta() {
    document.getElementById('modalPregunta').classList.remove('active');
}

async function guardarPregunta(e) {
    e.preventDefault();

    const id = document.getElementById('preguntaId').value;
    const texto = document.getElementById('preguntaTexto').value.trim();
    const categoria_id = Number(document.getElementById('preguntaCategoria').value);
    const dificultad = document.getElementById('preguntaDificultad').value;
    const explicacion = document.getElementById('preguntaExplicacion').value.trim();

    const opciones = [
        document.getElementById('opcion0').value.trim(),
        document.getElementById('opcion1').value.trim(),
        document.getElementById('opcion2').value.trim(),
        document.getElementById('opcion3').value.trim()
    ];

    let correcta = 0;
    const radios = document.getElementsByName('opcionCorrectaRadio');
    for (let r of radios) {
        if (r.checked) {
            correcta = Number(r.value);
            break;
        }
    }

    const payload = {
        texto,
        opciones,
        correcta,
        categoria_id,
        dificultad,
        explicacion
    };

    try {
        let res;
        if (id) {
            
            res = await fetch(`/api/preguntas/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        } else {
            
            res = await fetch('/api/preguntas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        }

        if (!res.ok) throw new Error('Error al guardar la pregunta en la base de datos');

        cerrarModalPregunta();
        await cargarPreguntas();
        await fetchMetrics();

    } catch (err) {
        alert(err.message);
    }
}

async function eliminarPregunta(id) {
    if (!confirm(`¿Está seguro de eliminar la pregunta #${id}? Esta acción actualizará inmediatamente el banco en la nube.`)) {
        return;
    }

    try {
        const res = await fetch(`/api/preguntas/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar la pregunta');

        await cargarPreguntas();
        await fetchMetrics();
    } catch (err) {
        alert(err.message);
    }
}
