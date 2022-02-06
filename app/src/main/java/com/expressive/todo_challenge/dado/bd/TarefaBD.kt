package com.expressive.todo_challenge.dado.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.expressive.todo_challenge.dado.dao.TarefaDao
import com.expressive.todo_challenge.dado.model.Tarefa
import com.expressive.todo_challenge.util.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Tarefa::class], version =1)
abstract class TarefaDB: RoomDatabase() {

    abstract fun tarefaDao():TarefaDao

    class Callback @Inject constructor(
        private val database: Provider<TarefaDB>,
        @ApplicationScope private val applicationScope:CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //db operations
            val dao= database.get().tarefaDao()

            applicationScope.launch {
                dao.insert(Tarefa("Estudar para a prova") )
                dao.insert(Tarefa("Revisar o projeto me acha") )
                dao.insert(Tarefa("Pensar na vida") )
                dao.insert(Tarefa("Tomar decisões") )
                dao.insert(Tarefa("Seguir enfrente") )
                dao.insert(Tarefa("Ser resiliente acima de tudo!",completado = true))
            }



        }
    }
}