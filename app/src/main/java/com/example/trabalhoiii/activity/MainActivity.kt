package com.example.trabalhoiii.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGoToAluno = findViewById<Button>(R.id.buttonGoToAluno)
        buttonGoToAluno.setOnClickListener {
            val intent = Intent(this, AlunoActivity::class.java)
            startActivity(intent)
        }

        val buttonGoToTreino = findViewById<Button>(R.id.buttonGoToTreino)
        buttonGoToTreino.setOnClickListener {
            val intent = Intent(this, TreinoActivity::class.java)
            startActivity(intent)
        }

        val buttonGoToExercicio = findViewById<Button>(R.id.buttonGoToExercicio)
        buttonGoToExercicio.setOnClickListener {
            val intent = Intent(this, ExercicioActivity::class.java)
            startActivity(intent)
        }
    }
}