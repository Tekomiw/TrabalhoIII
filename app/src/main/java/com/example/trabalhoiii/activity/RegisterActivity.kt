package com.example.trabalhoiii.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalhoiii.R
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var etusername: EditText
    private lateinit var etpassword: EditText
    private lateinit var registerButton: Button
    private lateinit var buttonVoltarRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etusername = findViewById(R.id.etusername)
        etpassword = findViewById(R.id.etpassword)
        registerButton = findViewById(R.id.registerButton)
        buttonVoltarRegister = findViewById(R.id.buttonVoltarRegister)

        val database = FirebaseDatabase.getInstance().getReference("users")

        registerButton.setOnClickListener {
            val username = etusername.text.toString()
            val password = etpassword.text.toString()

            if(username.isNotEmpty() && password.isNotEmpty()){
                val user = mapOf("password" to password)
                database.child(username).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao registrar o usuário", Toast.LENGTH_SHORT).show()
                    }
            } else{
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        buttonVoltarRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}