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
import com.example.trabalhoiii.model.Aluno
import org.w3c.dom.Text

class AlunoAdapter (
    private val context: Context,
    private val resource: Int,
    private val alunos: List<Aluno>,
    private val onEditClick: (Aluno) -> Unit,
    private val onDeleteClick: (Aluno) -> Unit
) : ArrayAdapter<Aluno>(context, resource, alunos){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val aluno = alunos[position]

        val tvNome = view.findViewById<TextView>(R.id.tvAlunoNome)
        val buttonEditar = view.findViewById<Button>(R.id.buttonEditarAluno)
        val buttonDeletar = view.findViewById<Button>(R.id.buttonDeletarAluno)

        tvNome.text = aluno.nome

        buttonEditar.setOnClickListener {
            onEditClick(aluno)
        }

        buttonDeletar.setOnClickListener {
            onDeleteClick(aluno)
        }

        return view
    }
}