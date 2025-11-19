package com.example.trabalhoiii.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.trabalhoiii.R
import com.example.trabalhoiii.activity.adapter.AlunoAdapter
import com.example.trabalhoiii.activity.adapter.TreinoAdapter
import com.example.trabalhoiii.model.Aluno
import com.example.trabalhoiii.model.Treino
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TreinoActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var listViewTreinos: ListView
    private lateinit var buttonAdicionarTreino: Button
    private lateinit var buttonVoltarTreino: Button
    private var listaTreinos: MutableList<Treino> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino)

        database = FirebaseDatabase.getInstance().reference.child("treinos")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listViewTreinos = findViewById(R.id.listViewAlunos)
        buttonAdicionarTreino = findViewById(R.id.buttonAdicionarTreino)
        buttonVoltarTreino = findViewById(R.id.buttonVoltarTreino)

        carregarTreinos()

        buttonAdicionarTreino.setOnClickListener {
            adicionarTreino()
        }

        buttonVoltarTreino.setOnClickListener {
            finish()
        }
    }

    private fun carregarTreinos(){
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaTreinos.clear()
                for (treinoSnapshot in snapshot.children) {
                    val treino = treinoSnapshot.getValue(Treino::class.java)
                    if (treino != null) {
                        listaTreinos.add(treino)
                    }
                }

                val adapter = TreinoAdapter(
                    this@TreinoActivity,
                    R.layout.item_treino,
                    listaTreinos,
                    onEditClick = { treino ->
                        mostrarDialogoEditar(treino)
                    },
                    onDeleteClick = { treino ->
                        if (treino.id != null) {
                            database.child(treino.id!!).removeValue()
                            Toast.makeText(this@TreinoActivity, "Treino removido", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )

                findViewById<ListView>(R.id.listViewTreinos).adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TreinoActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun adicionarTreino(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_treino, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Adicionar Treino")

        val etNomeTreino = dialogView.findViewById<EditText>(R.id.etNomeTreino)
        val etObjetivoTreino = dialogView.findViewById<EditText>(R.id.etObjetivoTreino)

        builder.setPositiveButton("Salvar"){dialog, _ ->
            val nome = etNomeTreino.text.toString()
            val objetivo = etObjetivoTreino.text.toString()

            val id = database.push().key
            val novoTreino = Treino(id, nome, objetivo, exerciciosIds = mutableListOf())
            if (id != null){
                database.child(id).setValue(novoTreino)
                Toast.makeText(this, "Treino adicionado", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }

    private fun mostrarDialogoEditar(treino: Treino){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_treino, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Editar Treino")

        val etNomeTreino = dialogView.findViewById<EditText>(R.id.etNomeTreino)
        val etObjetivoTreino = dialogView.findViewById<EditText>(R.id.etObjetivoTreino)

        builder.setPositiveButton("Atualizar"){ dialog, _ ->
            val nome = etNomeTreino.text.toString()
            val objetivo = etObjetivoTreino.text.toString()

            val updateTreino = Treino(treino.id, nome, objetivo, treino.exerciciosIds)
            if(treino.id != null){
                database.child(treino.id!!).setValue(updateTreino)
                Toast.makeText(this, "Treino atualizado", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }
}