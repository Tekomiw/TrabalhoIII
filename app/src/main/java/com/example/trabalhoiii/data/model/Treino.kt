package com.example.trabalhoiii.data.model

@Entity(
    tableName = "treino",
    foreignKeys = [ForeignKey(
        entity = Aluno::class,
        parentColumns = ["id"],
        childColumns = ["alunoId"],
        onDelete = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["alunoId"])]
)
data class Treino(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val objetivo: String,
    val alunoId: Int
)
