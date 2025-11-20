package com.example.trabalhoiii.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.trabalhoiii.R
import com.example.trabalhoiii.model.Exercicio
import com.example.trabalhoiii.model.Treino

class ExercicioTreinoAdapter(
    private val context: Context,
    private val lista: MutableList<Exercicio>,
    private val onRemoverClick: (Exercicio) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = lista.size

    override fun getItem(position: Int): Any = lista[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_exercicio_treino, parent, false)

        val tvNome = view.findViewById<TextView>(R.id.tvNomeExercicioItem)
        val btnRemover = view.findViewById<Button>(R.id.btnRemoverExercicio)

        val exercicio = lista[position]

        tvNome.text = exercicio.nome

        btnRemover.setOnClickListener {
            onRemoverClick(exercicio)
        }

        return view
    }
}