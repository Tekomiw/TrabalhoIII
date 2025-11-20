package com.example.trabalhoiii.activity.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.trabalhoiii.R
import com.example.trabalhoiii.activity.ExerciciosTreinoActivity
import com.example.trabalhoiii.model.Treino

class TreinoAdapter (
    private val context: Context,
    private val resource: Int,
    private val treinos: List<Treino>,
    private val onEditClick: (Treino) -> Unit,
    private val onDeleteClick: (Treino) -> Unit
) : ArrayAdapter<Treino>(context, resource, treinos){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val treino = treinos[position]

        val tvNome = view.findViewById<TextView>(R.id.tvTreinoNome)
        val tvObjetivo = view.findViewById<TextView>(R.id.tvTreinoObjetivo)
        val buttonEditar = view.findViewById<ImageButton>(R.id.buttonEditarTreino)
        val buttonDeletar = view.findViewById<ImageButton>(R.id.buttonDeletarTreino)

        val buttonGerenciar = view.findViewById<Button>(R.id.buttonGerenciarExercicios)

        buttonGerenciar.setOnClickListener {
            val intent = Intent(context, ExerciciosTreinoActivity::class.java)
            intent.putExtra("treinoId", treino.id)
            context.startActivity(intent)
        }

        tvNome.text = treino.nome
        tvObjetivo.text = treino.objetivo

        buttonEditar.setOnClickListener {
            onEditClick(treino)
        }

        buttonDeletar.setOnClickListener {
            onDeleteClick(treino)
        }

        return view
    }
}