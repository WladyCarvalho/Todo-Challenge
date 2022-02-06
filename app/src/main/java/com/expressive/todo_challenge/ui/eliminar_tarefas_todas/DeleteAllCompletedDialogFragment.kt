package com.codinginflow.mvvmtodo.ui.delete_all_completed

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment:DialogFragment() {

    private val viewModel:DeleteAllViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminação")
            .setMessage("Tem certeza de que pretende eliminar todas as tarefas?")
            .setNegativeButton("Cancelar",null)
            .setPositiveButton("Sim"){ _, _ ->
                viewModel.onConfirmClick()
            }
            .create()

}