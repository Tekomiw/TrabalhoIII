package com.example.trabalhoiii.data.model


@Entity(tableName = "exercicio",
    foreignKeys = [ForeignKey(
        entity = Treino::class,
        parentColumns = ["id"],
        childColumns = ["treinoId"])],
    indices = [Index(value = ["treinoId"])])
data class Exercicio (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val grupoMuscular: String,
    val treinoId: Int
)