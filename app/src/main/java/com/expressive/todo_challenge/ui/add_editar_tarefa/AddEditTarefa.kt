package com.expressive.todo_challenge.ui.add_editar_tarefa

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.expressive.todo_challenge.R
import com.expressive.todo_challenge.databinding.AddEditTarefaFragmentBinding
import com.expressive.todo_challenge.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTarefa : Fragment(R.layout.add_edit_tarefa_fragment) {

    companion object {
        fun newInstance() = AddEditTarefa()
    }

    private val viewModel: AddEditTarefaViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = AddEditTarefaFragmentBinding.bind(view)

        binding.apply {

            edtNomeTarefa.setText(viewModel.nomeTarefa)
            txtDataCriacao.isVisible = viewModel.tarefa !=null
            txtDataCriacao.text = "Criado: ${viewModel.tarefa?.dataCriada}"

            edtNomeTarefa.addTextChangedListener {
                viewModel.nomeTarefa = it.toString()
            }



            fabSalvarTarefa.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect {
                    event ->
                when(event){
                    is AddEditTarefaViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.edtNomeTarefa.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf(Pair("add_edit_result",to(event.result)))
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditTarefaViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        return@collect Snackbar.make(requireView(),event.msg, Snackbar.LENGTH_LONG).show()
                    }

                }.exhaustive
            }
        }
    }

}