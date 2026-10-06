package com.ivanbm.frontend_btz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.R
import com.ivanbm.frontend_btz.model.Material

class MaterialAdapter(
    private var materiales: List<Material>,
    private val onMaterialClick: (Material) -> Unit
) : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    class MaterialViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val textViewNombreMaterial: TextView =
            itemView.findViewById(R.id.textViewNombreMaterial)

        val textViewDetalleMaterial: TextView =
            itemView.findViewById(R.id.textViewDetalleMaterial)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MaterialViewHolder {

        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material, parent, false)

        return MaterialViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: MaterialViewHolder,
        position: Int
    ) {

        val material = materiales[position]

        holder.textViewNombreMaterial.text =
            material.nombre

        holder.textViewDetalleMaterial.text =
            material.detalle

        holder.itemView.setOnClickListener {
            onMaterialClick(material)
        }
    }

    override fun getItemCount(): Int =
        materiales.size

    fun actualizarMateriales(nuevosMateriales: List<Material>) {
        materiales = nuevosMateriales
        notifyDataSetChanged()
    }

    fun agregarMateriales(nuevosMateriales: List<Material>) {
        val posicionInicial = materiales.size

        materiales = materiales + nuevosMateriales

        notifyItemRangeInserted(
            posicionInicial,
            nuevosMateriales.size
        )
    }
}