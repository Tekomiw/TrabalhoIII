package com.example.trabalhoiii.model

data class Treino(
    val id: String? = "",
    val nome: String = "",
    val objetivo: String = "",
    val exerciciosIds: MutableList<String> = mutableListOf()
)
