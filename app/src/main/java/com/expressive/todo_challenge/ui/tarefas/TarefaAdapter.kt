package com.expressive.todo_challenge.ui.tarefas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.expressive.todo_challenge.dado.model.Tarefa
import com.expressive.todo_challenge.databinding.ItemTarefaBinding

class TarefaAdapter(private val listener: OnItemClickListener): ListAdapter<Tarefa,TarefaAdapter.TarefasViewHolder>(DiffCallBack()) {

   inner class TarefasViewHolder(private val binding:ItemTarefaBinding):RecyclerView.ViewHolder(binding.root)
    {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position!=RecyclerView.NO_POSITION){
                        val tarefa = getItem(position)
                        listener.onItemClick(tarefa)
                    }
                }
                checkBoxCompleto.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position !=RecyclerView.NO_POSITION){
                        val tarefa = getItem(position)
                        listener.onCheckBoxClick(tarefa,checkBoxCompleto.isChecked)
                    }
                }
            }
        }

        fun bind(tarefa: Tarefa)
        {
            binding.apply {
                checkBoxCompleto.isChecked = tarefa.completado
                txtNome.text = tarefa.nome
                txtNome.paint.isStrikeThruText = tarefa.completado
                txtDataCriacaoI.text = tarefa.dataCriada
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(tarefa:Tarefa)
        fun onCheckBoxClick(tarefa:Tarefa, isChecked:Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefasViewHolder {
        val binding = ItemTarefaBinding.inflate(LayoutInflater.from(parent.context),parent, false)
      return TarefasViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TarefasViewHolder, position: Int) {
            val current = getItem(position)
            holder.bind(current)
    }

    class DiffCallBack:DiffUtil.ItemCallback<Tarefa>()
    {
        override fun areItemsTheSame(oldItem: Tarefa, newItem: Tarefa) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Tarefa, newItem: Tarefa) = oldItem == newItem

    }
}