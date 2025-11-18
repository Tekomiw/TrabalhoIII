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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TreinoActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var treinoDao: TreinoDao
    private lateinit var alunoDao: AlunoDao
    private lateinit var listViewTreinos: ListView
    private lateinit var editTextNomeTreino: EditText
    private lateinit var editTextObjetivoTreino: EditText
    private lateinit var spinnerAluno: Spinner
    private lateinit var buttonAdicionarTreino: Button
    private lateinit var buttonVoltarTreino: Button
    private lateinit var adapter: TreinoAdapter
    private var listaTreinos: MutableList<Treino> = mutableListOf()
    private var listaAlunos: MutableList<Aluno> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treino)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "academia-db"
        ).build()
        treinoDao = db.treinoDao()
        alunoDao = db.alunoDao()

        listViewTreinos = findViewById(R.id.listViewTreinos)
        editTextNomeTreino = findViewById(R.id.editTextTreinoNome)
        editTextObjetivoTreino = findViewById(R.id.editTextTreinoObjetivo)
        spinnerAluno = findViewById(R.id.spinnerAluno)
        buttonAdicionarTreino= findViewById(R.id.buttonAdicionarTreino)
        buttonVoltarTreino = findViewById(R.id.buttonVoltarTreino)

        adapter = TreinoAdapter(
            this,
            listaTreinos,
            listaAlunos,
            onEdit = {treino -> mostrarDialogoEditar(treino)},
            onDelete = {treino -> deletarTreino(treino)}
        )

        listViewTreinos.adapter = adapter

        carregarTreinos()
        carregarAlunos()

        buttonAdicionarTreino.setOnClickListener {
            val nomeTreino = editTextNomeTreino.text.toString().trim()
            val objetivoTreino = editTextObjetivoTreino.text.toString().trim()
            val alunoSelecionado = spinnerAluno.selectedItem as Aluno
            if (nomeTreino.isNotEmpty() && objetivoTreino.isNotEmpty() && alunoSelecionado != null){
                adicionarTreino(nomeTreino, objetivoTreino, alunoSelecionado.id)
            }else{
                Toast.makeText(this, "Por favor, preencha todos os campos do treino", Toast.LENGTH_SHORT).show()
            }
        }

        buttonVoltarTreino.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun carregarTreinos(){
        lifecycleScope.launch {
            val treinos = withContext(Dispatchers.IO){
                treinoDao.getAllTreino()
            }
            listaTreinos.clear()
            listaTreinos.addAll(treinos)
            adapter.notifyDataSetChanged()
        }
    }

    private fun carregarAlunos(){
        lifecycleScope.launch {
            val alunos = withContext(Dispatchers.IO){
                alunoDao.getAllAlunos()
            }
            listaAlunos.clear()
            listaAlunos.addAll(alunos)

            val alunoAdapter = ArrayAdapter(this@TreinoActivity, android.R.layout.simple_spinner_item, listaAlunos)
            alunoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAluno.adapter = alunoAdapter
        }
    }

    private fun adicionarTreino(nome: String, objetivo: String, alunoId: Int){
        lifecycleScope.launch {
            val novoTreino = Treino(nome = nome, objetivo = objetivo, alunoId = alunoId)
            withContext(Dispatchers.IO){
                treinoDao.insertTreino(novoTreino)
            }
            withContext(Dispatchers.Main){
                editTextNomeTreino.text.clear()
                editTextObjetivoTreino.text.clear()
                Toast.makeText(this@TreinoActivity, "Treino adicionado.", Toast.LENGTH_SHORT).show()
                carregarTreinos()
            }
        }
    }

    private fun mostrarDialogoEditar(treino: Treino){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Treino")

        val viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_editar_treino, null, false)
        val inputNome = viewInflated.findViewById<EditText>(R.id.editTextEditarTreinoNome)
        val inputObjetivo = viewInflated.findViewById<EditText>(R.id.editTextEditarTreinoObjetivo)
        val spinnerEditarAluno = viewInflated.findViewById<Spinner>(R.id.spinnerEditarAluno)

        inputNome.setText(treino.nome)
        inputObjetivo.setText(treino.objetivo)

        val alunoAdapter = ArrayAdapter(this@TreinoActivity, android.R.layout.simple_spinner_item, listaAlunos)
        alunoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEditarAluno.adapter = alunoAdapter

        val alunoAtual = listaAlunos.firstOrNull() {it.id == treino.alunoId}
        if(alunoAtual != null){
            val alunoPos = alunoAdapter.getPosition(alunoAtual)
            spinnerEditarAluno.setSelection(alunoPos)
        }

        builder.setView(viewInflated)

        builder.setPositiveButton("Salvar"){ dialog, _ ->
            val novoNome = inputNome.text.toString().trim()
            val novoObjetivo = inputObjetivo.text.toString().trim()
            val novoAluno = spinnerEditarAluno.selectedItem as Aluno

            if (novoNome.isNotEmpty() && novoObjetivo.isNotEmpty() && novoAluno != null){
                atualizarTreino(treino.copy(nome = novoNome, objetivo = novoObjetivo, alunoId = novoAluno.id))
            } else{
                Toast.makeText(this, "Os campos do treino não podem estar vazios", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") {dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun atualizarTreino(treinoAtualizado: Treino){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                treinoDao.updateTreino(treinoAtualizado)
            }
            withContext(Dispatchers.Main){
                Toast.makeText(this@TreinoActivity, "Treino atualizado.", Toast.LENGTH_SHORT).show()
                carregarTreinos()
            }
        }
    }

    private fun deletarTreino(treino: Treino){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Deletar treino")
        builder.setMessage("Tem certeza que deseja deletar o treino \"${treino.nome}?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    treinoDao.deleteTreino(treino)
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(this@TreinoActivity, "Treino deletado.", Toast.LENGTH_SHORT).show()
                    carregarTreinos()
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