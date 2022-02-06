package com.expressive.todo_challenge.ui.tarefas

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.expressive.todo_challenge.dado.dao.Preferencias
import com.expressive.todo_challenge.dado.dao.SortOrder
import com.expressive.todo_challenge.dado.dao.TarefaDao
import com.expressive.todo_challenge.dado.model.Tarefa
import com.expressive.todo_challenge.ui.ADD_TAREFA_OK
import com.expressive.todo_challenge.ui.EDIT_TAREFA_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TarefasViewModel @ViewModelInject constructor(
    private val tarefaDao: TarefaDao,
    @Assisted
    private val state: SavedStateHandle,
    private val preferenceManager:Preferencias ):ViewModel()

{

    val searchQuery = state.getLiveData("searchQuery","")
    val preferencesFlow = preferenceManager.preferencesFlow

    private val tarefaEventoCanal = Channel<TarefasEventos>()
    val tarefaEvent = tarefaEventoCanal.receiveAsFlow()

    private val tarefasFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow)
    {
            query,filterPreferences ->

        Pair(query, filterPreferences)
    }
        .flatMapLatest { (query,filterPreferences) ->
            tarefaDao.getTarefas(query,filterPreferences.sortOrder,filterPreferences.hideCompleted)
        }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }



    fun onTarefaSelecionada(tarefa: Tarefa) = viewModelScope.launch{
        tarefaEventoCanal.send(TarefasEventos.NavigateToEditTaskScreen(tarefa))

    }

    fun onHideCompletedClick(hideCompleted:Boolean) = viewModelScope.launch {
        preferenceManager.updateHideCompleted(hideCompleted)
    }

    fun onTarefaCheckedChanged(tarefa:Tarefa, isChecked:Boolean) = viewModelScope.launch {
        tarefaDao.update(tarefa.copy(completado = isChecked))
    }

    fun onTaskSwipe(tarefa:Tarefa) = viewModelScope.launch{
        tarefaDao.delete(tarefa)
        tarefaEventoCanal.send(TarefasEventos.ShowUndoDeleteTaskMessage(tarefa))
    }

    fun onDesfazerEliminarClick(tarefa: Tarefa) = viewModelScope.launch{
        tarefaDao.insert(tarefa)
    }

    fun onAddNovaTarefaClick() = viewModelScope.launch {
        tarefaEventoCanal.send(TarefasEventos.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) = viewModelScope.launch {
        when(result){
            ADD_TAREFA_OK -> mostrarMensagemConfirmacaoSalvar("Tarefa adicionada")
            EDIT_TAREFA_OK -> mostrarMensagemConfirmacaoSalvar("Tarefa ACTUALIZADA")
        }

    }

    fun onDeleteCompletedClick() = viewModelScope.launch {
        tarefaEventoCanal.send(TarefasEventos.NavigateToDeleteAllCompletedScreen)
    }

    private fun mostrarMensagemConfirmacaoSalvar(text:String) = viewModelScope.launch {
        tarefaEventoCanal.send(TarefasEventos.ShowTarefaConfirmationMessage(text))
    }



    sealed class TarefasEventos {
        object NavigateToAddTaskScreen:TarefasEventos()
        data class NavigateToEditTaskScreen(val tarefa: Tarefa):TarefasEventos()
        data class ShowUndoDeleteTaskMessage(val tarefa:Tarefa) : TarefasEventos()
        data class ShowTarefaConfirmationMessage(val msg:String) : TarefasEventos()
        object NavigateToDeleteAllCompletedScreen : TarefasEventos()
    }


    val tarefas = tarefasFlow.asLiveData()
}