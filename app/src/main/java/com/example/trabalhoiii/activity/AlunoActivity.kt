package com.example.trabalhoiii.activity

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlunoActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var alunoDao: AlunoDao
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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "academia-db"
        ).build()
        alunoDao = db.alunoDao()

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
            val nome = editTextNomeAluno.text.toString().trim()
            val idade = editTextIdadeAluno.text.toString().toIntOrNull()
            if (nome.isNotEmpty() && idade != null){
                adicionarAluno(nome, idade)
            }else{
                Toast.makeText(this, "Por favor, preencha todos os campos do aluno", Toast.LENGTH_SHORT).show()
            }
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
        lifecycleScope.launch {
            val alunos = withContext(Dispatchers.IO){
                alunoDao.getAllAlunos()
            }
            listaAlunos.clear()
            listaAlunos.addAll(alunos)
            adapter.notifyDataSetChanged()
        }
    }

    private fun adicionarAluno(nome: String, idade: Int){
        lifecycleScope.launch {
            val novoAluno = Aluno(nome = nome, idade = idade)
            withContext(Dispatchers.IO){
                alunoDao.insertAluno(novoAluno)
            }
            withContext(Dispatchers.Main){
                editTextNomeAluno.text.clear()
                editTextIdadeAluno.text.clear()
                Toast.makeText(this@AlunoActivity, "Aluno adicionado.", Toast.LENGTH_SHORT).show()
                carregarAlunos()
            }
        }
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