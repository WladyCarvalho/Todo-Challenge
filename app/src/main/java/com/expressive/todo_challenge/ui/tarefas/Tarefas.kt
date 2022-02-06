package com.expressive.todo_challenge.ui.tarefas

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.expressive.todo_challenge.R
import com.expressive.todo_challenge.dado.dao.SortOrder
import com.expressive.todo_challenge.dado.model.Tarefa
import com.expressive.todo_challenge.databinding.TarefasFragmentBinding
import com.expressive.todo_challenge.util.OnQueryTextChanged
import com.expressive.todo_challenge.util.exhaustive

import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Tarefas :Fragment(R.layout.tarefas_fragment),TarefaAdapter.OnItemClickListener
{

    private val viewmodel:TarefasViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = TarefasFragmentBinding.bind(view)
        val tarefaAdapter = TarefaAdapter(this)

        binding.apply {
            recyclerViewTarefas.apply {
                adapter = tarefaAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val tarefa = tarefaAdapter.currentList[viewHolder.adapterPosition]
                    viewmodel.onTaskSwipe(tarefa)
                }
            }).attachToRecyclerView(recyclerViewTarefas)

            fabAddTarefas.setOnClickListener {
                viewmodel.onAddNovaTarefaClick()
            }
        }

        setFragmentResultListener("add_edit_request"){
                _,bundle ->
            val result = bundle.getInt("add_edit_result")
            viewmodel.onAddEditResult(result)
        }

        viewmodel.tarefas.observe(viewLifecycleOwner){
            tarefaAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewmodel.tarefaEvent.collect{
                    evento ->
                when(evento){
                    is TarefasViewModel.TarefasEventos.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(),"Tarefa eliminada", Snackbar.LENGTH_LONG)
                            .setAction("DESFAZER"){
                                viewmodel.onDesfazerEliminarClick(evento.tarefa)
                            }.show()
                    }
                    is TarefasViewModel.TarefasEventos.NavigateToAddTaskScreen -> {
                        val action = TarefasDirections.actionTarefasToAddEditTarefa(null,"Nova tarefa")
                        findNavController().navigate(action)
                    }
                    is TarefasViewModel.TarefasEventos.NavigateToEditTaskScreen -> {
                        val action = TarefasDirections.actionTarefasToAddEditTarefa(evento.tarefa,"Editar tarefa")
                        findNavController().navigate(action)
                    }
                    is TarefasViewModel.TarefasEventos.ShowTarefaConfirmationMessage -> {
                        Snackbar.make(requireView(), evento.msg, Snackbar.LENGTH_LONG).show()
                    }
                    TarefasViewModel.TarefasEventos.NavigateToDeleteAllCompletedScreen -> {
                        val action = TarefasDirections.actionTarefasToDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tarefa, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.OnQueryTextChanged{
            viewmodel.searchQuery.value= it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked
            viewmodel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId)
        {
            R.id.action_sort_by_name ->{
                viewmodel.onSortOrderSelected(SortOrder.POR_NOME)
                true
            }

            R.id.action_sort_by_date_created ->{
                viewmodel.onSortOrderSelected(SortOrder.POR_DATA)
                true
            }

            R.id.action_hide_completed_tasks ->{
                item.isChecked = !item.isChecked
                viewmodel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks ->{
                viewmodel.onDeleteCompletedClick()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(tarefa: Tarefa) {
        viewmodel.onTarefaSelecionada(tarefa)
    }

    override fun onCheckBoxClick(tarefa: Tarefa, isChecked: Boolean) {
        viewmodel.onTarefaCheckedChanged(tarefa,isChecked)
    }

}