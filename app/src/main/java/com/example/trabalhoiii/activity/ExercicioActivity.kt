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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExercicioActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var exercicioDao: ExercicioDao
    private lateinit var treinoDao: TreinoDao
    private lateinit var listViewExercicios: ListView
    private lateinit var editTextNomeExercicio: EditText
    private lateinit var editTextGrupoExercicio: EditText
    private lateinit var spinnerTreino: Spinner
    private lateinit var buttonAdicionarExercicio: Button
    private lateinit var buttonVoltarExercicio: Button
    private lateinit var adapter: ExercicioAdapter
    private var listaExercicios: MutableList<Exercicio> = mutableListOf()
    private var listaTreinos: MutableList<Treino> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercicio)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "academia-db"
        ).build()
        exercicioDao = db.exercicioDao()
        treinoDao = db.treinoDao()

        listViewExercicios = findViewById(R.id.listViewExercicios)
        editTextNomeExercicio = findViewById(R.id.editTextExercicioNome)
        editTextGrupoExercicio = findViewById(R.id.editTextExercicioGrupo)
        spinnerTreino = findViewById(R.id.spinnerTreino)
        buttonAdicionarExercicio= findViewById(R.id.buttonAdicionarExercicio)
        buttonVoltarExercicio = findViewById(R.id.buttonVoltarExercicio)

        adapter = ExercicioAdapter(
            this,
            listaExercicios,
            listaTreinos,
            onEdit = {exercicio -> mostrarDialogoEditar(exercicio)},
            onDelete = {exercicio -> deletarExercicio(exercicio)}
        )

        listViewExercicios.adapter = adapter

        carregarExercicios()
        carregarTreinos()

        buttonAdicionarExercicio.setOnClickListener {
            val nomeExercicio = editTextNomeExercicio.text.toString().trim()
            val grupoExercico = editTextGrupoExercicio.text.toString().trim()
            val treinoSelecionado = spinnerTreino.selectedItem as Treino
            if (nomeExercicio.isNotEmpty() && grupoExercico.isNotEmpty() && treinoSelecionado != null){
                adicionarExercicio(nomeExercicio, grupoExercico, treinoSelecionado.id)
            }else{
                Toast.makeText(this, "Por favor, preencha todos os campos do exercicio", Toast.LENGTH_SHORT).show()
            }
        }

        buttonVoltarExercicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun carregarExercicios(){
        lifecycleScope.launch {
            val exercicios = withContext(Dispatchers.IO){
                exercicioDao.getAllExercicio()
            }
            listaExercicios.clear()
            listaExercicios.addAll(exercicios)
            adapter.notifyDataSetChanged()
        }
    }

    private fun carregarTreinos(){
        lifecycleScope.launch {
            val treinos = withContext(Dispatchers.IO){
                treinoDao.getAllTreino()
            }
            listaTreinos.clear()
            listaTreinos.addAll(treinos)

            val treinoAdapter = ArrayAdapter(this@ExercicioActivity, android.R.layout.simple_spinner_item, listaTreinos)
            treinoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTreino.adapter = treinoAdapter
        }
    }

    private fun adicionarExercicio(nome: String, grupo: String, treinoId: Int){
        lifecycleScope.launch {
            val novoExercicio = Exercicio(nome = nome, grupoMuscular = grupo, treinoId = treinoId)
            withContext(Dispatchers.IO){
                exercicioDao.insertExercicio(novoExercicio)
            }
            withContext(Dispatchers.Main){
                editTextNomeExercicio.text.clear()
                editTextGrupoExercicio.text.clear()
                Toast.makeText(this@ExercicioActivity, "Exercicio adicionado.", Toast.LENGTH_SHORT).show()
                carregarExercicios()
            }
        }
    }

    private fun mostrarDialogoEditar(exercicio: Exercicio){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar Exercício")

        val viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_editar_exercicio, null, false)
        val inputNome = viewInflated.findViewById<EditText>(R.id.editTextEditarExercicioNome)
        val inputGrupo = viewInflated.findViewById<EditText>(R.id.editTextEditarExercicioGrupo)
        val spinnerEditarTreino = viewInflated.findViewById<Spinner>(R.id.spinnerEditarTreino)


        inputNome.setText(exercicio.nome)
        inputGrupo.setText(exercicio.grupoMuscular)

        val treinoAdapter = ArrayAdapter(this@ExercicioActivity, android.R.layout.simple_spinner_item, listaTreinos)
        treinoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEditarTreino.adapter = treinoAdapter

        val treinoAtual = listaTreinos.firstOrNull() {it.id == exercicio.treinoId}
        if(treinoAtual != null){
            val treinoPos = treinoAdapter.getPosition(treinoAtual)
            spinnerEditarTreino.setSelection(treinoPos)
        }

        builder.setView(viewInflated)

        builder.setPositiveButton("Salvar"){ dialog, _ ->
            val novoNome = inputNome.text.toString().trim()
            val novoGrupo = inputGrupo.text.toString().trim()
            val novoTreino = spinnerEditarTreino.selectedItem as Treino

            if (novoNome.isNotEmpty() && novoGrupo.isNotEmpty() && novoTreino != null){
                atualizarExercicio(exercicio.copy(nome = novoNome, grupoMuscular = novoGrupo, treinoId = novoTreino.id))
            } else{
                Toast.makeText(this, "Os campos do exercicio não podem estar vazios", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") {dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun atualizarExercicio(exercicioAtualizado: Exercicio){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                exercicioDao.updateExercicio(exercicioAtualizado)
            }
            withContext(Dispatchers.Main){
                Toast.makeText(this@ExercicioActivity, "Exercicio atualizado.", Toast.LENGTH_SHORT).show()
                carregarExercicios()
            }
        }
    }

    private fun deletarExercicio(exercicio: Exercicio){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Deletar Exercicio")
        builder.setMessage("Tem certeza que deseja deletar o exercicio \"${exercicio.nome}?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    exercicioDao.deleteExercicio(exercicio)
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(this@ExercicioActivity, "Exercicio deletado.", Toast.LENGTH_SHORT).show()
                    carregarExercicios()
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