package pe.edu.cibertec.skillquiznuevo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter(
    private val listaPartidas: List<ItemRanking>
) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    data class ItemRanking(
        val numeroPartida: Int,
        val correctas: Int,
        val total: Int,
        val porcentaje: Int,
        val puntosAcumulados: Int,
        val sincronizado: Boolean = false
    )

    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNumeroPartida: TextView = itemView.findViewById(R.id.txtNumeroPartida)
        val txtBadgeSincronizado: TextView = itemView.findViewById(R.id.txtBadgeSincronizado)
        val txtDetallePartida: TextView = itemView.findViewById(R.id.txtDetallePartida)
        val txtPuntajeAcumulado: TextView = itemView.findViewById(R.id.txtPuntajeAcumulado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(vista)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val item = listaPartidas[position]

        holder.txtNumeroPartida.text = "Partida ${item.numeroPartida}"
        holder.txtDetallePartida.text = "${item.correctas}/${item.total} correctas • ${item.porcentaje}%"
        holder.txtPuntajeAcumulado.text = "Total acumulado: ${item.puntosAcumulados} puntos"

        if (item.sincronizado) {
            holder.txtBadgeSincronizado.text = "Sincronizado Cloud"
            holder.txtBadgeSincronizado.setTextColor(android.graphics.Color.parseColor("#15803D"))
            holder.txtBadgeSincronizado.setBackgroundColor(android.graphics.Color.parseColor("#DCFCE7"))
        } else {
            holder.txtBadgeSincronizado.text = "SQLite Local"
            holder.txtBadgeSincronizado.setTextColor(android.graphics.Color.parseColor("#0369A1"))
            holder.txtBadgeSincronizado.setBackgroundColor(android.graphics.Color.parseColor("#E0F2FE"))
        }
    }

    override fun getItemCount(): Int = listaPartidas.size
}
