package pe.edu.cibertec.skillquiznuevo

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pe.edu.cibertec.skillquiznuevo.network.RankingItemDto

class TopEstudiantesAdapter(
    private val listaEstudiantes: List<RankingItemDto>,
    private val nombreUsuarioActual: String
) : RecyclerView.Adapter<TopEstudiantesAdapter.EstudianteViewHolder>() {

    class EstudianteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPosicion: TextView = itemView.findViewById(R.id.txtPosicionEstudiante)
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreEstudiante)
        val txtBadgeTu: TextView = itemView.findViewById(R.id.txtBadgeTu)
        val txtDetalle: TextView = itemView.findViewById(R.id.txtDetalleEstudiante)
        val txtPuntaje: TextView = itemView.findViewById(R.id.txtPuntajeEstudiante)
        val txtPorcentaje: TextView = itemView.findViewById(R.id.txtPorcentajeEstudiante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstudianteViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_estudiante, parent, false)
        return EstudianteViewHolder(vista)
    }

    override fun onBindViewHolder(holder: EstudianteViewHolder, position: Int) {
        val item = listaEstudiantes[position]
        Log.d("ADAPTER_DEBUG", "Renderizando elemento $position: ${item.usuarioNombre}")
        val puesto = position + 1

        holder.txtPosicion.text = "#$puesto"
        when (puesto) {
            1 -> {
                holder.txtPosicion.setBackgroundColor(Color.parseColor("#FEF3C7"))
                holder.txtPosicion.setTextColor(Color.parseColor("#B45309"))
            }
            2 -> {
                holder.txtPosicion.setBackgroundColor(Color.parseColor("#F1F5F9"))
                holder.txtPosicion.setTextColor(Color.parseColor("#475569"))
            }
            3 -> {
                holder.txtPosicion.setBackgroundColor(Color.parseColor("#FFEDD5"))
                holder.txtPosicion.setTextColor(Color.parseColor("#C2410C"))
            }
            else -> {
                holder.txtPosicion.setBackgroundColor(Color.parseColor("#F8FAFC"))
                holder.txtPosicion.setTextColor(Color.parseColor("#64748B"))
            }
        }

        holder.txtNombre.text = item.usuarioNombre

        val esUsuarioActual = item.usuarioNombre.equals(nombreUsuarioActual, ignoreCase = true)
        holder.txtBadgeTu.visibility = if (esUsuarioActual) View.VISIBLE else View.GONE

        val textoPartidas = if (item.partidasJugadas == 1) "partida" else "partidas"
        holder.txtDetalle.text = "${item.partidasJugadas} $textoPartidas • ${item.totalAciertos} aciertos"

        holder.txtPuntaje.text = "${item.mejorPuntaje} pts"
        holder.txtPorcentaje.text = "${item.promedioAciertos.toInt()}%"
    }

    override fun getItemCount(): Int = listaEstudiantes.size
}
