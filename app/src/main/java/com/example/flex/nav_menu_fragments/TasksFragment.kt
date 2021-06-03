package com.example.flex.nav_menu_fragments

import android.icu.util.TimeUnit
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.flex.R
import com.example.flex.Tasks.Task
import com.example.flex.UserTaskView.UserTaskAdapter
import com.example.flex.UserTaskView.UserTaskRecyclerTouchCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_user_tasks.*
import java.util.*

class TasksFragment : Fragment() {

    companion object {
        const val TAG = "TAG_FRAGMENT_TASKS"
        const val KEY_EDIT = "KEY_EDIT"

        const val PREF_NAME = "PREFTODO"
        const val KEY_STARTED = "KEY_STARTED"
        const val KEY_LAST_USED = "KEY_LAST_USED"
        var notify_counter_TODO = 0
        var notify_counter_MISSED = 0
    }
    lateinit var taskAdapter: UserTaskAdapter
    private lateinit var databaseReference: DatabaseReference
    private var firebaseAuth: FirebaseAuth? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_tasks, container, false)
        notify_counter_TODO = 0
        notify_counter_MISSED = 0
        databaseReference = Firebase.database.reference

        Thread {

            firebaseAuth = FirebaseAuth.getInstance()
            //firebaseDatabase = FirebaseDatabase.getInstance()
            val user = firebaseAuth!!.currentUser
            Log.i("firebase", "User is"+user.uid)
            //val databaseReference = firebaseDatabase!!.reference.child("Tasks")
            databaseReference.child("Tasks").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var taskList : MutableList<Task> = mutableListOf()

                    for (ds in dataSnapshot.children) {
                        val task: Task? =
                            ds.getValue(Task::class.java)

                        //print(task!!.getTaskDescription())
                        Log.i("firebase", "Got value"+task!!.getTaskDescription())
                        if (task != null && task.getTaskuid() == user.uid) {
                            taskList.add(task)
                            //*** related to notfication part
                            //i changed deadline to tryout so i dont create new tasks
                            //+ Date( java.util.concurrent.TimeUnit.DAYS.toMillis(2)).time
                            var deadline = task.getTaskDeadline()!!.time
                            val before_deadline = deadline - Date( java.util.concurrent.TimeUnit.DAYS.toMillis(2)).time
                            var currTime =  Date(System.currentTimeMillis()).time
                            if(currTime in before_deadline..deadline)
                            {
                                notify_counter_TODO += 1
                            }
                            else if(currTime> deadline)
                            {
                                notify_counter_MISSED += 1
                            }
                            //***
                        }



                    }
                    runOnUiThread {
                        Log.i("firebase", "Got value" + taskList.size)
                        taskAdapter = UserTaskAdapter(requireContext(), taskList)
                        UserrecyclerTasks.adapter = taskAdapter

                        val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                        UserrecyclerTasks.addItemDecoration(itemDecoration)


                        val touchCallbakList = UserTaskRecyclerTouchCallback(taskAdapter)
                        val itemTouchHelper = ItemTouchHelper(touchCallbakList)
                        itemTouchHelper.attachToRecyclerView(UserrecyclerTasks)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), databaseError.code, Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }.start()


        return rootView
    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }

}