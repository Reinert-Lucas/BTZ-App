package com.ivanbm.frontend_btz.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.EditarAvisoActivity
import com.ivanbm.frontend_btz.R
import com.ivanbm.frontend_btz.model.Aviso

class AvisoAdapter(
    private var avisos: List<Aviso>
) : RecyclerView.Adapter<AvisoAdapter.AvisoViewHolder>() {

    class AvisoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewDireccionAviso: TextView =
            itemView.findViewById(R.id.textViewDireccionAviso)

        val textViewFechaAviso: TextView =
            itemView.findViewById(R.id.textViewFechaAviso)

        val textViewClienteAviso: TextView =
            itemView.findViewById(R.id.textViewClienteAviso)

        val textViewUrgenciaAviso: TextView =
            itemView.findViewById(R.id.textViewUrgenciaAviso)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AvisoViewHolder {

        val vistaAviso = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aviso, parent, false)

        return AvisoViewHolder(vistaAviso)
    }

    override fun onBindViewHolder(
        holder: AvisoViewHolder,
        position: Int
    ) {

        val aviso = avisos[position]

        holder.textViewDireccionAviso.text =
            "Dirección: ${aviso.direccion}"

        holder.textViewFechaAviso.text =
            "Fecha de visita: ${aviso.fecha} ${aviso.hora}"

        holder.textViewClienteAviso.text =
            "Cliente: ${aviso.cliente.nombre}"

        holder.textViewUrgenciaAviso.text =
            "Prioridad: ${aviso.urgencia}"

        holder.itemView.setOnClickListener {

            val intent = Intent(
                holder.itemView.context,
                EditarAvisoActivity::class.java
            )

            intent.putExtra("AVISO_ID", aviso.id)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return avisos.size
    }

    fun actualizarAvisos(nuevosAvisos: List<Aviso>) {
        avisos = nuevosAvisos
        notifyDataSetChanged()
    }

    fun agregarAvisos(nuevosAvisos: List<Aviso>) {
        val posicionInicial = avisos.size

        avisos = avisos + nuevosAvisos

        notifyItemRangeInserted(
            posicionInicial,
            nuevosAvisos.size
        )
    }
}