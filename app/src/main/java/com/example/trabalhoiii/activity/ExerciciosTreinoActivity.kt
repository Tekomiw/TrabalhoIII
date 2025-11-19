package com.example.trabalhoiii.activity

import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalhoiii.R
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
    private lateinit var buttonAdicionar: Button
    private lateinit var buttonVoltar: Button

    private var listaExercicios: MutableList<Exercicio> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercicios_treino)

        treinoId = intent.getStringExtra("treinoId") ?: ""

        dbTreinos = FirebaseDatabase.getInstance().reference.child("treinos")
        dbExercicios = FirebaseDatabase.getInstance().reference.child("exercicios")

        tvTituloTreino = findViewById(R.id.tvTituloTreino)
        listViewExercicios = findViewById(R.id.listViewExerciciosDoTreino)
        buttonAdicionar = findViewById(R.id.buttonAdicionarExercicio)
        buttonVoltar = findViewById(R.id.buttonVoltarExercicioTreino)

        carregarTreino()
        carregarExercicios()

        buttonAdicionar.setOnClickListener {
            Toast.makeText(this, "Tela de adicionar exercício será implementada aqui", Toast.LENGTH_SHORT).show()
        }

        buttonVoltar.setOnClickListener { finish() }
    }

    private fun carregarTreino() {
        dbTreinos.child(treinoId).get().addOnSuccessListener {
            treinoAtual = it.getValue(Treino::class.java)!!
            tvTituloTreino.text = "Exercícios do Treino: ${treinoAtual.nome}"
        }
    }

    private fun carregarExercicios() {
        dbExercicios.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaExercicios.clear()
                for (snap in snapshot.children) {
                    val ex = snap.getValue(Exercicio::class.java)
                    if (ex != null && treinoAtual.exerciciosIds.contains(ex.id)) {
                        listaExercicios.add(ex)
                    }
                }

                // Aqui futuramente vai o Adapter com checkbox ou lista de exercícios
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun adicionarExercicioAoTreino(exercicioId: String) {
        if (!treinoAtual.exerciciosIds.contains(exercicioId)) {
            treinoAtual.exerciciosIds.add(exercicioId)
            dbTreinos.child(treinoId).setValue(treinoAtual)
        }
    }

    fun removerExercicioDoTreino(exercicioId: String) {
        treinoAtual.exerciciosIds.remove(exercicioId)
        dbTreinos.child(treinoId).setValue(treinoAtual)
    }
}