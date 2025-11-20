package com.example.trabalhoiii.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trabalhoiii.R
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var etusername: EditText
    private lateinit var etpassword: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etusername = findViewById(R.id.etusername)
        etpassword = findViewById(R.id.etpassword)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        val database = FirebaseDatabase.getInstance().getReference("users")

        loginButton.setOnClickListener {
            val username = etusername.text.toString()
            val password = etpassword.text.toString()

            database.child(username).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()){
                    val storedPassword = snapshot.child("password").value.toString()
                    if(storedPassword == password){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else{
                        Toast.makeText(this, "Senha incorreta", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(this, "Erro ao acessar o banco de dados", Toast.LENGTH_LONG).show()
                }
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}