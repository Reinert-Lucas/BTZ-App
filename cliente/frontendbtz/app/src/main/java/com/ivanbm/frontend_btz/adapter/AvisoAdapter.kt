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
    private var avisos: List<Aviso>,
    private val onAvisoClick: ((Aviso) -> Unit)? = null
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

        holder.textViewDireccionAviso.text = aviso.direccion
        holder.textViewFechaAviso.text = "${aviso.fecha} · ${aviso.hora}"
        holder.textViewClienteAviso.text = aviso.cliente.nombre

        val urgencia = aviso.urgencia.trim().lowercase()

        when (urgencia) {
            "urgente" -> {
                holder.textViewUrgenciaAviso.text = "Urgente"
                holder.textViewUrgenciaAviso.setBackgroundResource(
                    R.drawable.bg_urgencia_urgente
                )
                holder.textViewUrgenciaAviso.setTextColor(
                    android.graphics.Color.parseColor("#B3261E")
                )
            }

            "media" -> {
                holder.textViewUrgenciaAviso.text = "Media"
                holder.textViewUrgenciaAviso.setBackgroundResource(
                    R.drawable.bg_urgencia_media
                )
                holder.textViewUrgenciaAviso.setTextColor(
                    android.graphics.Color.parseColor("#8A5A00")
                )
            }

            "baja" -> {
                holder.textViewUrgenciaAviso.text = "Baja"
                holder.textViewUrgenciaAviso.setBackgroundResource(
                    R.drawable.bg_urgencia_baja
                )
                holder.textViewUrgenciaAviso.setTextColor(
                    android.graphics.Color.parseColor("#247342")
                )
            }

            else -> {
                holder.textViewUrgenciaAviso.text = aviso.urgencia
                holder.textViewUrgenciaAviso.setBackgroundResource(
                    R.drawable.bg_urgencia_media
                )
                holder.textViewUrgenciaAviso.setTextColor(
                    android.graphics.Color.parseColor("#8A5A00")
                )
            }
        }

        holder.itemView.setOnClickListener {
            if (onAvisoClick != null) {
                onAvisoClick.invoke(aviso)
            } else {
                val intent = Intent(
                    holder.itemView.context,
                    EditarAvisoActivity::class.java
                )

                intent.putExtra("AVISO_ID", aviso.id)
                holder.itemView.context.startActivity(intent)
            }
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