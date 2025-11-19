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
import com.example.trabalhoiii.activity.adapter.AlunoAdapter
import com.example.trabalhoiii.model.Aluno
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AlunoActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var listViewAlunos: ListView
    private lateinit var buttonAdicionarAluno: Button
    private lateinit var buttonVoltarAluno: Button
    private var listaAlunos: MutableList<Aluno> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno)

        database = FirebaseDatabase.getInstance().reference.child("alunos")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listViewAlunos = findViewById(R.id.listViewAlunos)
        buttonAdicionarAluno = findViewById(R.id.buttonAdicionarAluno)
        buttonVoltarAluno = findViewById(R.id.buttonVoltarAluno)

        carregarAlunos()

        buttonAdicionarAluno.setOnClickListener {
            adicionarAluno()
        }

        buttonVoltarAluno.setOnClickListener {
            finish()
        }
    }

    private fun carregarAlunos(){
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaAlunos.clear()
                for (alunoSnapshot in snapshot.children) {
                    val aluno = alunoSnapshot.getValue(Aluno::class.java)
                    if (aluno != null) {
                        listaAlunos.add(aluno)
                    }
                }

                val adapter = AlunoAdapter(
                    this@AlunoActivity,
                    R.layout.item_aluno,
                    listaAlunos,
                    onEditClick = { aluno ->
                        mostrarDialogoEditar(aluno)
                    },
                    onDeleteClick = { aluno ->
                        if(aluno.id != null){
                            database.child(aluno.id!!).removeValue()
                            Toast.makeText(this@AlunoActivity, "Aluno removido", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                findViewById<ListView>(R.id.listViewAlunos).adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AlunoActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun adicionarAluno(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_aluno, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Adicionar Aluno")

        val etNomeAluno = dialogView.findViewById<EditText>(R.id.etNomeAluno)
        val etIdateAluno = dialogView.findViewById<EditText>(R.id.etIdadeAluno)

        builder.setPositiveButton("Salvar"){dialog, _ ->
            val nome = etNomeAluno.text.toString()
            val idade = etIdateAluno.text.toString().toIntOrNull() ?: 0

            val id = database.push().key
            val novoAluno = Aluno(id, nome, idade)
            if (id != null){
                database.child(id).setValue(novoAluno)
                Toast.makeText(this, "Aluno adicionado", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }

    private fun mostrarDialogoEditar(aluno: Aluno){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_aluno, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Editar Aluno")

        val etNomeAluno = dialogView.findViewById<EditText>(R.id.etNomeAluno)
        val etIdateAluno = dialogView.findViewById<EditText>(R.id.etIdadeAluno)

        builder.setPositiveButton("Atualizar"){ dialog, _ ->
            val nome = etNomeAluno.text.toString()
            val idade = etIdateAluno.text.toString().toIntOrNull() ?: 0

            val updateAluno = Aluno(aluno.id, nome, idade)
            if(aluno.id != null){
                database.child(aluno.id!!).setValue(updateAluno)
                Toast.makeText(this, "aluno atualizado", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }
}