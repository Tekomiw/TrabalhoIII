package com.example.trabalhoiii.data.dao


@Dao
interface TreinoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreino(treino: Treino)

    @Query("SELECT * FROM treino")
    suspend fun getAllTreino(): List<Treino>

    @Transaction
    @Query("SELECT * FROM treino WHERE alunoId = :alunoId")
    suspend fun getTreinoByAlunoId(alunoId: Int): List<Treino>

    @Update
    suspend fun updateTreino(treino: Treino)

    @Delete
    suspend fun deleteTreino(treino: Treino)
}