package com.example.flex.Tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.AdminActivity
import com.example.flex.R
//import com.example.flexi.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.task_row.view.*
import java.util.*

class TaskAdapter: RecyclerView.Adapter<TaskAdapter.ViewHolder>, TaskTouchHelperCallback  {

    var taskItems = mutableListOf<Task>()
    private lateinit var databaseReference: DatabaseReference
    val context: Context

    constructor(context: Context, listTasks: List<Task>) {
        this.context = context
        taskItems.addAll(listTasks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.task_row, parent, false
        )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = taskItems[position]
        databaseReference = Firebase.database.reference

        holder.tvAddress.text = currentTask.getTaskAddress()
        holder.tvDate.text = currentTask.getTaskDate()
        holder.tvDesc.text = currentTask.getTaskDescription()
        holder.cbDone.text = currentTask.getTaskType()
        //holder.cbDone.isChecked = currentTask.getTaskDone()

        holder.btnDelete.setOnClickListener {
            deleteTask(holder.adapterPosition)
        }

        holder.btnEdit.setOnClickListener {
            (context as AdminActivity).showEditTodoDialog(
                taskItems[holder.adapterPosition], holder.adapterPosition
            )
        }

       /* holder.cbDone.setOnClickListener {
            taskItems[holder.adapterPosition].done = holder.cbDone.isChecked
            Thread{
                //AppDatabase.getInstance(context).todoDao().updateTodo(taskItems[holder.adapterPosition])
                databaseReference.child("Tasks").child(taskItems[holder.adapterPosition].getTaskDate()!!).setValue(taskItems[holder.adapterPosition])

            }.start()
        }*/

        /*if (taskItems[holder.adapterPosition].category == 0) {
            holder.ivIcon.setImageResource(R.mipmap.ic_launcher)
        } else if (taskItems[holder.adapterPosition].category == 1) {
            holder.ivIcon.setImageResource(R.drawable.tasks_head)
        }*/
    }

    private fun deleteTask(position: Int) {
        Thread {
            //AppDatabase.getInstance(context).todoDao().deleteTodo(
            //taskItems.get(position))
            databaseReference.child("Tasks").child(taskItems.get(position).getTaskDate()!!).removeValue()


            (context as AdminActivity).runOnUiThread {
                taskItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }.start()
    }

    public fun addTask(task: Task) {
        taskItems.add(task)

        //notifyDataSetChanged() // this refreshes the whole list
        notifyItemInserted(taskItems.lastIndex)
    }

    public fun updateTask(task: Task, editIndex: Int) {
        taskItems.set(editIndex, task)
        notifyItemChanged(editIndex)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate = itemView.tvDate
        val cbDone = itemView.cbDone
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
        val tvDesc = itemView.tvDesc
        val tvAddress = itemView.tvAddress
        // val ivIcon = itemView.ivIcon


    }

    override fun onDismissed(position: Int) {
        deleteTask(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(taskItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }
}