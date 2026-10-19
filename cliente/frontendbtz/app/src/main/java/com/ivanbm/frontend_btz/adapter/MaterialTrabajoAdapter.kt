package com.ivanbm.frontend_btz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.R
import com.ivanbm.frontend_btz.model.MaterialSeleccionado

class MaterialTrabajoAdapter(
    private var materiales: MutableList<MaterialSeleccionado>,
    private val onEliminarMaterial: (MaterialSeleccionado) -> Unit,
    private val soloLectura: Boolean = false
) : RecyclerView.Adapter<MaterialTrabajoAdapter.MaterialTrabajoViewHolder>() {

    class MaterialTrabajoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val textViewNombreMaterialTrabajo: TextView =
            itemView.findViewById(R.id.textViewNombreMaterialTrabajo)

        val textViewCantidadMaterialTrabajo: TextView =
            itemView.findViewById(R.id.textViewCantidadMaterialTrabajo)

        val buttonEliminarMaterialTrabajo: Button =
            itemView.findViewById(R.id.buttonEliminarMaterialTrabajo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MaterialTrabajoViewHolder {

        val vistaMaterial = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_material_trabajo,
                parent,
                false
            )

        return MaterialTrabajoViewHolder(vistaMaterial)
    }

    override fun onBindViewHolder(
        holder: MaterialTrabajoViewHolder,
        position: Int
    ) {
        val materialSeleccionado = materiales[position]

        holder.textViewNombreMaterialTrabajo.text =
            materialSeleccionado.material.nombre

        holder.textViewCantidadMaterialTrabajo.text =
            "Cantidad: ${materialSeleccionado.cantidad}"

        if (soloLectura) {
            holder.buttonEliminarMaterialTrabajo.visibility = View.GONE
        } else {
            holder.buttonEliminarMaterialTrabajo.visibility = View.VISIBLE

            holder.buttonEliminarMaterialTrabajo.setOnClickListener {
                onEliminarMaterial(materialSeleccionado)
            }
        }
    }

    override fun getItemCount(): Int = materiales.size

    fun agregarMaterial(material: MaterialSeleccionado) {
        materiales.add(material)
        notifyItemInserted(materiales.size - 1)
    }

    fun eliminarMaterial(material: MaterialSeleccionado) {
        if (soloLectura) return

        val posicion = materiales.indexOf(material)

        if (posicion != -1) {
            materiales.removeAt(posicion)
            notifyItemRemoved(posicion)
        }
    }

    fun obtenerMateriales(): List<MaterialSeleccionado> =
        materiales.toList()

    fun mostrarMateriales(nuevosMateriales: List<MaterialSeleccionado>) {
        materiales.clear()
        materiales.addAll(nuevosMateriales)
        notifyDataSetChanged()
    }
}
