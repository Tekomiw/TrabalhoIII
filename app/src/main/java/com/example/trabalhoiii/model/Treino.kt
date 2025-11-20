package com.example.trabalhoiii.model

data class Treino(
    val id: String? = "",
    val nome: String = "",
    val objetivo: String = "",
    var exerciciosIds: MutableList<String> = mutableListOf()
)
