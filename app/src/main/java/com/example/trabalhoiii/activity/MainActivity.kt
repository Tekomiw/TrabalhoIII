package com.example.trabalhoiii.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.trabalhoiii.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Encontra os CardViews
        val cardAluno = findViewById<CardView>(R.id.cardAluno)
        val cardTreino = findViewById<CardView>(R.id.cardTreino)
        val cardExercicio = findViewById<CardView>(R.id.cardExercicio)
        val buttonLogout = findViewById<Button>(R.id.buttonLogout)

        // Configura os cliques
        cardAluno.setOnClickListener {
            val intent = Intent(this, AlunoActivity::class.java)
            startActivity(intent)
        }

        cardTreino.setOnClickListener {
            val intent = Intent(this, TreinoActivity::class.java)
            startActivity(intent)
        }

        cardExercicio.setOnClickListener {
            val intent = Intent(this, ExercicioActivity::class.java)
            startActivity(intent)
        }

        buttonLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}