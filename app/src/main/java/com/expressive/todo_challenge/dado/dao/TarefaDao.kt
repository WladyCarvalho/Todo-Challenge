package com.expressive.todo_challenge.dado.dao

import androidx.room.*
import com.expressive.todo_challenge.dado.model.Tarefa
import kotlinx.coroutines.flow.Flow

@Dao
interface TarefaDao {

    fun getTarefas(query:String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Tarefa>> =
        when(sortOrder){
            SortOrder.POR_DATA -> getTarefasOrdenadosPorData(query,hideCompleted)
            SortOrder.POR_NOME -> getTarefasOrdenadosPorNome(query,hideCompleted)
        }

    @Query("SELECT * FROM tb_tarefa WHERE (completado!=:hideCompleted OR completado = 0 ) AND nome LIKE '%' || :searchQuery || '%' ORDER BY criado DESC")
    fun getTarefasOrdenadosPorData(searchQuery:String,hideCompleted:Boolean,): Flow<List<Tarefa>>

    @Query("SELECT * FROM tb_tarefa WHERE (completado!=:hideCompleted OR completado = 0 ) AND nome LIKE '%' || :searchQuery || '%' ORDER BY nome DESC")
    fun getTarefasOrdenadosPorNome(searchQuery:String, hideCompleted:Boolean,): Flow<List<Tarefa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarefa: Tarefa)
    
    @Update
    suspend fun update(tarefa:Tarefa)
    
    @Delete
    suspend fun delete(tarefa: Tarefa)

    @Query("DELETE FROM tb_tarefa WHERE completado=1")
    suspend fun deleteCompletedTasks()
}