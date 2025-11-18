package com.example.trabalhoiii.data.model

@Entity(tableName = "aluno")
data class Aluno(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val idade: Int
)
