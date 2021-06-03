package com.example.flex.UserTaskView

import android.R.drawable
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flex.AdminActivity
import com.example.flex.HomeActivity
import com.example.flex.R
import com.example.flex.Tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.task_userview_row.view.*
import java.util.*
import java.util.concurrent.TimeUnit


class UserTaskAdapter : RecyclerView.Adapter<UserTaskAdapter.ViewHolder>, UserTaskTouchHelperCallback {

    var taskItems = mutableListOf<Task>()
    private lateinit var databaseReference: DatabaseReference
    val context: Context

    companion object{
        var opened_task : Task? = null
    }

    constructor(context: Context, listTasks: List<Task>) {
        this.context = context
        taskItems.addAll(listTasks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.task_userview_row, parent, false
        )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTask = taskItems[position]
        databaseReference = Firebase.database.reference

        //Set Pharmacy Icon for pharmacy Tasks
        if (currentTask.getTaskType() == "Pharmacy")
        {
            val img: Drawable = context.resources.getDrawable(R.drawable.pharmacy)
            //var h = holder.tvIcon.drawable.intrinsicHeight
            //var w = holder.tvIcon.drawable.intrinsicWidth
            //Log.i("firebase", "Rani Time w = "+ w )
            //Log.i("firebase", "Rani Time h = "+ h )
            holder.tvIcon.setImageDrawable(img)
        }
        //Set address / date /  type  / background color depending on Deadline
        holder.tvAddress.text = currentTask.getTaskAddress()
        holder.tvDate.text = currentTask.getTaskDate()
        holder.tvType.text = currentTask.getTaskType()
        holder.name.text = currentTask.getTaskDestName()

        holder.item.setOnClickListener {v ->
            Log.i("firebase", "rani fil button heeeey")
            opened_task = currentTask
            if (currentTask.getClosedTask() == false) {
                val activity = v!!.context as HomeActivity
                var fragment = activity.supportFragmentManager.findFragmentByTag(UserTaskDetailsFragment.TAG)

                if (fragment == null) {
                    fragment = UserTaskDetailsFragment()

                }

                if (fragment != null) {

                    val ft = activity.supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fragmentContainer, fragment, UserTaskDetailsFragment.TAG)

                    ft.addToBackStack(null) // add fragment transaction to the back stack
                    ft.commit()
                }
            }
        }

        var deadline = currentTask.getTaskDeadline()
        //val Before_deadline = currentTask.getTaskDeadline() - Date( TimeUnit.DAYS.toMillis(2))
        var currTime =  Date(System.currentTimeMillis())
        //val currTime = Date(System.currentTimeMillis())
        // JUST TO TRY OUT THE BACKGROUND COLOR
        Log.i("firebase", "Rani Time"+ currTime.after(deadline) )
        //Log.i("firebase", "Rani Time"+ deadline!!.date )
        if (currTime.after(deadline))
        {

            val red: Int = Color.parseColor("#ffb6c1")
            holder.card.setBackgroundColor(red)
        }
        //holder.cbDone.text = currentTask.getTaskType()
        // holder.cbDone.isChecked = currentTask.getTaskDone()!!

        /* holder.itemView.btnDetails.setOnClickListener { v ->
           Log.i("firebase", "rani fil button heeeey")
           opened_task = currentTask

           val activity = v!!.context as HomeActivity
           var fragment = activity.supportFragmentManager.findFragmentByTag(UserTaskDetailsFragment.TAG)

           if (fragment == null) {
               fragment = UserTaskDetailsFragment()

           }

           if (fragment != null) {

               val ft = activity.supportFragmentManager.beginTransaction()
               ft.replace(R.id.fragmentContainer, fragment, UserTaskDetailsFragment.TAG)

               ft.addToBackStack(null) // add fragment transaction to the back stack
               ft.commit()
           }
       }*/


        /*holder.cbDone.setOnClickListener {
            taskItems[holder.adapterPosition].done = holder.cbDone.isChecked
            Thread{
                //AppDatabase.getInstance(context).todoDao().updateTodo(taskItems[holder.adapterPosition])
                databaseReference.child("Tasks").child(taskItems[holder.adapterPosition].getTaskDate()!!).setValue(taskItems[holder.adapterPosition])

            }.start()
        }*/

    }

    private fun deleteTask(position: Int) {
        Thread {
            //AppDatabase.getInstance(context).todoDao().deleteTodo(
            //taskItems.get(position))
            databaseReference.child("Tasks").child(taskItems.get(position).getTaskDate()!!).removeValue()


            (context as HomeActivity).runOnUiThread {
                taskItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }.start()
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate = itemView.tvDateUser
        //val cbDone = itemView.cbDoneUser
        val tvType = itemView.tvType
        val tvAddress = itemView.tvlocalisation
        val tvIcon = itemView.task_icon
        val card = itemView.inside_card
        val item = itemView.card_view
        val name = itemView.tvname

        //val btnDetails = itemView.btnDetails



    }

    override fun onDismissed(position: Int) {
        deleteTask(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(taskItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


}