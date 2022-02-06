package com.expressive.todo_challenge.ui.add_editar_tarefa

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expressive.todo_challenge.dado.dao.TarefaDao
import com.expressive.todo_challenge.dado.model.Tarefa
import com.expressive.todo_challenge.ui.ADD_TAREFA_OK
import com.expressive.todo_challenge.ui.EDIT_TAREFA_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class AddEditTarefaViewModel @ViewModelInject constructor (
    private val tarefaDao: TarefaDao,
    @Assisted
    private val state: SavedStateHandle
):ViewModel() {

    val tarefa = state.get<Tarefa>("tarefa")
    var nomeTarefa = state.get<String>("nomeTarefa")?:tarefa?.nome?:""
        set(value) {
            field = value
            state.set("nomeTarefa",value)
        }


    private val addEditTarefaEventoCanal = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTarefaEventoCanal.receiveAsFlow()

    fun onSaveClick(){

        if (nomeTarefa.isBlank()) {
            showInvalidInputMessage("Mome não pode ser nulo")
            return
        }
        if (tarefa != null) {
            val updatedtask = tarefa.copy(nome = nomeTarefa)
            updateTarefa(updatedtask)
        } else {
            val nova_tarefa = Tarefa(nome = nomeTarefa)
            criarTarefa(nova_tarefa)
        }
    }

    private fun showInvalidInputMessage(text:String) = viewModelScope.launch {
        addEditTarefaEventoCanal.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    private fun criarTarefa(tarefa:Tarefa) = viewModelScope.launch {
        tarefaDao.insert(tarefa)
        addEditTarefaEventoCanal.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TAREFA_OK))
    }

    private fun updateTarefa(tarefa: Tarefa) = viewModelScope.launch {
        tarefaDao.update(tarefa)
        addEditTarefaEventoCanal.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TAREFA_OK))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg:String):AddEditTaskEvent()
        data class NavigateBackWithResult(val result:Int):AddEditTaskEvent()
    }

}