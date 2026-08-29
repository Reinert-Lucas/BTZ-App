package com.ivanbm.frontend_btz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivanbm.frontend_btz.R
import com.ivanbm.frontend_btz.model.Usuario

class UsuarioAdapter(
    private var usuarios: List<Usuario>,
    private val onUsuarioClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewNombreUsuario: TextView =
            itemView.findViewById(R.id.textViewNombreUsuario)

        val textViewRolUsuario: TextView =
            itemView.findViewById(R.id.textViewRolUsuario)

        val textViewDniUsuario: TextView =
            itemView.findViewById(R.id.textViewDniUsuario)

        val textViewTelefonoUsuario: TextView =
            itemView.findViewById(R.id.textViewTelefonoUsuario)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsuarioViewHolder {

        val vistaUsuario = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_personal, parent, false)

        return UsuarioViewHolder(vistaUsuario)
    }

    override fun onBindViewHolder(
        holder: UsuarioViewHolder,
        position: Int
    ) {

        val usuario = usuarios[position]

        holder.textViewNombreUsuario.text =
            usuario.nombre

        holder.textViewRolUsuario.text =
            "Rol: ${usuario.rol}"

        holder.textViewDniUsuario.text =
            "DNI: ${usuario.dni}"

        holder.textViewTelefonoUsuario.text =
            "Teléfono: ${usuario.telefono}"


        holder.itemView.setOnClickListener {
            onUsuarioClick(usuario)
        }
    }

    override fun getItemCount(): Int {
        return usuarios.size
    }

    fun actualizarUsuarios(nuevosUsuarios: List<Usuario>) {
        usuarios = nuevosUsuarios
        notifyDataSetChanged()
    }

    fun obtenerUsuario(posicion: Int): Usuario {
        return usuarios[posicion]
    }
}