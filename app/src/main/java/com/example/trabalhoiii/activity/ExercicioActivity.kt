package com.example.trabalhoiii.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalhoiii.R
import com.example.trabalhoiii.activity.adapter.ExercicioAdapter
import com.example.trabalhoiii.model.Exercicio
import com.example.trabalhoiii.model.Treino
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExercicioActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var listViewExercicios: ListView
    private lateinit var buttonAdicionarExercicio: Button
    private lateinit var buttonVoltarExercicio: Button
    private var listaExercicios: MutableList<Exercicio> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercicio)

        database = FirebaseDatabase.getInstance().reference.child("exercicios")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listViewExercicios = findViewById(R.id.listViewExercicios)
        buttonAdicionarExercicio = findViewById(R.id.buttonAdicionarExercicio)
        buttonVoltarExercicio = findViewById(R.id.buttonVoltarExercicio)

        carregarExercicios()

        buttonAdicionarExercicio.setOnClickListener {
            adicionarExercicio()
        }

        buttonVoltarExercicio.setOnClickListener {
            finish()
        }
    }

    private fun carregarExercicios(){
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaExercicios.clear()
                for (exercicioSnapshot in snapshot.children) {
                    val exercicio = exercicioSnapshot.getValue(Exercicio::class.java)
                    if (exercicio != null) {
                        listaExercicios.add(exercicio)
                    }
                }

                val adapter = ExercicioAdapter(
                    this@ExercicioActivity,
                    R.layout.item_exercicio,
                    listaExercicios,
                    onEditClick = { exercicio ->
                        mostrarDialogoEditar(exercicio)
                    },
                    onDeleteClick = { exercicio ->
                        if (exercicio.id != null) {
                            database.child(exercicio.id!!).removeValue()
                            Toast.makeText(this@ExercicioActivity, "Exercicio removido", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                )

                findViewById<ListView>(R.id.listViewTreinos).adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ExercicioActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun adicionarExercicio(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercicio, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Adicionar Exercicio")

        val etNomeExercicio = dialogView.findViewById<EditText>(R.id.etNomeExercicio)
        val etGrupoExercicio = dialogView.findViewById<EditText>(R.id.etGrupoExercicio)

        builder.setPositiveButton("Salvar"){dialog, _ ->
            val nome = etNomeExercicio.text.toString()
            val grupo = etGrupoExercicio.text.toString()

            val id = database.push().key
            val novoExercicio = Exercicio(id, nome, grupo)
            if (id != null){
                database.child(id).setValue(novoExercicio)
                Toast.makeText(this, "Exercicio adicionado", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }
    private fun mostrarDialogoEditar(exercicio: Exercicio){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercicio, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Editar Exercicio")

        val etNomeExercicio = dialogView.findViewById<EditText>(R.id.etNomeExercicio)
        val etGrupoExercicio = dialogView.findViewById<EditText>(R.id.etGrupoExercicio)

        builder.setPositiveButton("Atualizar"){ dialog, _ ->
            val nome = etNomeExercicio.text.toString()
            val grupo = etGrupoExercicio.text.toString()

            val updateExercicio = Exercicio(exercicio.id, nome, grupo)
            if(exercicio.id != null){
                database.child(exercicio.id!!).setValue(updateExercicio)
                Toast.makeText(this, "Exercicio atualizado", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }
}