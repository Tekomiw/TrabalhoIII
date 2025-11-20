package com.example.trabalhoiii.activity.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.trabalhoiii.R
import com.example.trabalhoiii.model.Exercicio

class ExercicioAdapter (
    private val context: Context,
    private val resource: Int,
    private val exercicios: MutableList<Exercicio>,
    private val onEditClick: (Exercicio) -> Unit,
    private val onDeleteClick: (Exercicio) -> Unit
) : ArrayAdapter<Exercicio>(context, resource, exercicios){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(resource, parent, false)

        val exercicio = exercicios[position]

        val tvNome = view.findViewById<TextView>(R.id.tvExercicioNome)
        val tvGrupo = view.findViewById<TextView>(R.id.tvExercicioGrupo)
        val buttonEditar = view.findViewById<ImageButton>(R.id.buttonEditarExercicio)
        val buttonDeletar = view.findViewById<ImageButton>(R.id.buttonDeletarExercicio)

        tvNome.text = exercicio.nome
        tvGrupo.text = exercicio.grupoMuscular

        buttonEditar.setOnClickListener {
            onEditClick(exercicio)
        }

        buttonDeletar.setOnClickListener {
            onDeleteClick(exercicio)
        }

        return view
    }
}