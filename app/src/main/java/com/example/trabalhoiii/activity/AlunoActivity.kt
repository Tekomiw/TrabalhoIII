package com.example.trabalhoiii.activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.trabalhoiii.activity.adapter.AlunoAdapter
import com.example.trabalhoiii.data.model.Aluno
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlunoActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var listViewAlunos: ListView
    private lateinit var editTextNomeAluno: EditText
    private lateinit var editTextIdadeAluno: EditText
    private lateinit var buttonAdicionarAluno: Button

    private lateinit var buttonVoltarAluno: Button
    private lateinit var adapter: AlunoAdapter
    private var listaAlunos: MutableList<Aluno> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aluno)

        database = FirebaseDatabase.getInstance().reference.child("alunos")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listViewAlunos = findViewById(R.id.listViewAlunos)
        editTextNomeAluno = findViewById(R.id.editTextAlunoNome)
        editTextIdadeAluno = findViewById(R.id.editTextAlunoIdade)
        buttonAdicionarAluno = findViewById(R.id.buttonAdicionarAluno)
        buttonVoltarAluno = findViewById(R.id.buttonVoltarAluno)

        adapter = AlunoAdapter(
            this,
            listaAlunos,
            onEdit = {aluno -> mostrarDialogoEditar(aluno)},
            onDelete = {aluno -> deletarAluno(aluno)}
        )

        listViewAlunos.adapter = adapter

        carregarAlunos()

        buttonAdicionarAluno.setOnClickListener {
            adicionarAluno()
        }

        buttonVoltarAluno.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun carregarAlunos(){
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaAlunos.clear()
                for (alunoSnapshot in snapshot.children){
                    val aluno = alunoSnapshot.getValue(Aluno::class.java)
                    if (aluno != null){
                        listaAlunos.add(aluno)
                    }
                }
            }
        })
    }

    private fun adicionarAluno(){
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_aluno, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Adicionar Aluno")

        editTextNomeAluno = dialogView.findViewById(R.layout.editTextAlunoNome)
        editTextIdadeAluno = dialogView.findViewById(R.layout.editTextAlunoIdade)

        builder.setPositiveButton("Salvar"){dialog, _ ->
            val nome = editTextNomeAluno.text.toString()
            val idade = editTextIdadeAluno.text.toString().toIntOrNull()

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
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Aluno")

        val viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_editar_aluno, null, false)
        val inputNome = viewInflated.findViewById<EditText>(R.id.editTextEditarAlunoNome)
        val inputIdade = viewInflated.findViewById<EditText>(R.id.editTextEditarAlunoIdade)
        inputNome.setText(aluno.nome)
        inputIdade.setText(aluno.idade.toString())

        builder.setView(viewInflated)

        builder.setPositiveButton("Salvar"){ dialog, _ ->
            val novoNome = inputNome.text.toString().trim()
            val novaIdade = inputIdade.text.toString().toIntOrNull()
            if (novoNome.isNotEmpty() && novaIdade != null){
                atualizarAluno(aluno.copy(nome = novoNome, idade = novaIdade))
            } else{
                Toast.makeText(this, "Os campos do aluno não podem estar vazios", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") {dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun atualizarAluno(alunoAtualizado: Aluno){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                alunoDao.updateAluno(alunoAtualizado)
            }
            withContext(Dispatchers.Main){
                Toast.makeText(this@AlunoActivity, "Aluno atualizado.", Toast.LENGTH_SHORT).show()
                carregarAlunos()
            }
        }
    }

    private fun deletarAluno(aluno: Aluno){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Deletar aluno")
        builder.setMessage("Deletar Aluno")
        builder.setMessage("Tem certeza que deseja deletar o aluno \"${aluno.nome}? Isso tamém deletará todos os treinos associados a esse aluno.")

        builder.setPositiveButton("Sim") { dialog, _ ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    alunoDao.deleteAluno(aluno)
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(this@AlunoActivity, "Aluno deletado.", Toast.LENGTH_SHORT).show()
                    carregarAlunos()
                }
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Não"){dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

}