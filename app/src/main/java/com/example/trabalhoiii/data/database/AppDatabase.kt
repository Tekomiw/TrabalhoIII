package com.example.trabalhoiii.data.database


@Database(entities = [Aluno::class, Treino::class, Exercicio::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alunoDao(): AlunoDao
    abstract fun exercicioDao(): ExercicioDao
    abstract fun treinoDao(): TreinoDao
}