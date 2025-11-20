package com.example.trabalhoiii.activity

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalhoiii.R
import com.example.trabalhoiii.activity.adapter.ExercicioTreinoAdapter
import com.example.trabalhoiii.model.Exercicio
import com.example.trabalhoiii.model.Treino
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExerciciosTreinoActivity : AppCompatActivity() {

    private lateinit var dbTreinos: DatabaseReference
    private lateinit var dbExercicios: DatabaseReference
    private lateinit var treinoId: String
    private lateinit var treinoAtual: Treino

    private lateinit var tvTituloTreino: TextView
    private lateinit var listViewExercicios: ListView
    private lateinit var buttonVoltar: Button
    private var listaExercicios: MutableList<Exercicio> = mutableListOf()
    private lateinit var adapter: ArrayAdapter<String>
    private var nomesExercicios: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercicios_treino)

        treinoId = intent.getStringExtra("treinoId") ?: ""

        dbTreinos = FirebaseDatabase.getInstance().reference.child("treinos")
        dbExercicios = FirebaseDatabase.getInstance().reference.child("exercicios")

        tvTituloTreino = findViewById(R.id.tvTituloTreino)
        buttonVoltar = findViewById(R.id.buttonVoltarExercicioTreino)

        listViewExercicios = findViewById(R.id.listViewTodosExercicios)

        carregarTreino()
        carregarExercicios()

        listViewExercicios.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val exercicioSelecionado = listaExercicios[position]
                adicionarExercicioAoTreino(exercicioSelecionado.id!!)
            }

        buttonVoltar.setOnClickListener { finish() }
    }

    private fun carregarTreino() {
        dbTreinos.child(treinoId).get().addOnSuccessListener {
            treinoAtual = it.getValue(Treino::class.java)!!
        }
    }

    private fun carregarExercicios() {
        dbExercicios.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaExercicios.clear()
                nomesExercicios.clear()

                for (snap in snapshot.children) {
                    val ex = snap.getValue(Exercicio::class.java)
                    if (ex != null) {
                        listaExercicios.add(ex)
                        nomesExercicios.add(ex.nome ?: "Sem nome")
                    }
                }

                adapter = ArrayAdapter(
                    this@ExerciciosTreinoActivity,
                    android.R.layout.simple_list_item_1,
                    nomesExercicios
                )

                listViewExercicios.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun adicionarExercicioAoTreino(exercicioId: String) {
        if (!treinoAtual.exerciciosIds.contains(exercicioId)) {
            treinoAtual.exerciciosIds.add(exercicioId)

            dbTreinos.child(treinoId).setValue(treinoAtual)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Exercício adicionado ao treino!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                this,
                "Este exercício já está no treino!",
                Toast.LENGTH_SHORT
            ).show()


        }
    }

    fun removerExercicioDoTreino(exercicioId: String) {
        treinoAtual.exerciciosIds.remove(exercicioId)
        dbTreinos.child(treinoId).setValue(treinoAtual)
    }
}