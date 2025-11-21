package com.example.trabalhoiii.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalhoiii.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etusername: EditText
    private lateinit var etpassword: EditText
    private lateinit var registerButton: Button
    private lateinit var buttonVoltarRegister: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etusername = findViewById(R.id.etusername)
        etpassword = findViewById(R.id.etpassword)
        registerButton = findViewById(R.id.registerButton)
        buttonVoltarRegister = findViewById(R.id.buttonVoltarRegister)

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().getReference("users")

        registerButton.setOnClickListener {
            val email = etusername.text.toString()
            val password = etpassword.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user!!.uid

                    val userData = mapOf(
                        "email" to email,
                        "password" to password
                    )

                    database.child(uid).setValue(userData)

                    Toast.makeText(this, "Usuário registrado!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao registrar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        buttonVoltarRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
