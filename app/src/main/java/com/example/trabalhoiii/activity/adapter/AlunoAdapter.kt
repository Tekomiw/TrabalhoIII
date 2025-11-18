package com.example.trabalhoiii.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class AlunoAdapter (
    context: Context,
    private var alunos: MutableList<Aluno>,
    private var onEdit: (Aluno) -> Unit,
    private var onDelete: (Aluno) -> Unit
) : ArrayAdapter<Aluno>(context, 0, alunos){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val aluno = alunos[position]
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_aluno, parent, false)

        val textViewNome = view.findViewById<TextView>(R.id.textViewNomeAluno)
        val textViewIdade = view.findViewById<TextView>(R.id.textViewIdadeAluno)
        val buttonEditar = view.findViewById<ImageButton>(R.id.buttonEditarAluno)
        val buttonDeletar = view.findViewById<ImageButton>(R.id.buttonDeletarAluno)

        textViewNome.text = aluno.nome
        textViewIdade.text = aluno.idade.toString()

        buttonEditar.setOnClickListener {
            onEdit(aluno)
        }

        buttonDeletar.setOnClickListener {
            onDelete(aluno)
        }

        return view
    }

    fun atualizarAlunos(novosAlunos: List<Aluno>){
        alunos.clear()
        alunos.addAll(novosAlunos)
        notifyDataSetChanged()
    }
}