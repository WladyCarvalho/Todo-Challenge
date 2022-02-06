package com.codinginflow.mvvmtodo.ui.delete_all_completed

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.expressive.todo_challenge.dado.dao.TarefaDao
import com.expressive.todo_challenge.util.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllViewModel @ViewModelInject constructor(
    private val tarefaDao: TarefaDao,
    @ApplicationScope private val applicationScope: CoroutineScope
):ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        tarefaDao.deleteCompletedTasks()
    }
}