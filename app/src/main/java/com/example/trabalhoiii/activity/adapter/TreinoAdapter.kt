package com.example.trabalhoiii.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class TreinoAdapter (
    context: Context,
    private var treinos: MutableList<Treino>,
    private var alunos: List<Aluno>,
    private var onEdit: (Treino) -> Unit,
    private var onDelete: (Treino) -> Unit
) : ArrayAdapter<Treino>(context, 0, treinos){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val treino = treinos[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_treino, parent, false)

        val textViewNome = view.findViewById<TextView>(R.id.textViewNomeTreino)
        val textViewObjetivo = view.findViewById<TextView>(R.id.textViewObjetivoTreino)
        val buttonEditar = view.findViewById<ImageButton>(R.id.buttonEditarTreino)
        val buttonDeletar = view.findViewById<ImageButton>(R.id.buttonDeletarTreino)

        val aluno = alunos.firstOrNull {it.id == treino.alunoId}

        textViewObjetivo.text = treino.objetivo
        textViewNome.text = if(aluno != null) {
            "${treino.nome} - ${treino.objetivo} -  ${aluno.nome}"
        } else{
            "${treino.nome} - ${treino.objetivo}"
        }

        buttonEditar.setOnClickListener {
            onEdit(treino)
        }

        buttonDeletar.setOnClickListener {
            onDelete(treino)
        }

        return view
    }

    fun atualizarTreino(novosTreinos: List<Treino>, novosAlunos: List<Aluno>){
        treinos.clear()
        treinos.addAll(novosTreinos)
        alunos = novosAlunos
        notifyDataSetChanged()
    }
}