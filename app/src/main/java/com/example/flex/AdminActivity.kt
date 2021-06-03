package com.example.flex

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.flex.Tasks.Task
import com.example.flex.Tasks.TaskAdapter
import com.example.flex.Tasks.TaskDialog
import com.example.flex.Tasks.TaskRecyclerTouchCallback
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.*

class AdminActivity: AppCompatActivity(), TaskDialog.TaskHandler {

    lateinit var taskAdapter: TaskAdapter
    private lateinit var databaseReference: DatabaseReference
    private var firebaseAuth: FirebaseAuth? = null
    //private var firebaseDatabase: FirebaseDatabase? = null
    companion object {
        const val KEY_EDIT = "KEY_EDIT"

        const val PREF_NAME = "PREFTODO"
        const val KEY_STARTED = "KEY_STARTED"
        const val KEY_LAST_USED = "KEY_LAST_USED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbar))

        databaseReference = Firebase.database.reference

        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            showAddTodoDialog()
        }

        if (!wasStartedBefore()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab)
                .setPrimaryText("Create Task")
                .setSecondaryText("Click here to create new items")
                .show()
        }

        Thread {

            //firebaseAuth = FirebaseAuth.getInstance()
            //firebaseDatabase = FirebaseDatabase.getInstance()
            //val user = firebaseAuth!!.currentUser
            //val databaseReference = firebaseDatabase!!.reference.child("Tasks")
            databaseReference.child("Tasks").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var taskList : MutableList<Task> = mutableListOf()

                    for (ds in dataSnapshot.children) {
                        val task: Task? =
                            ds.getValue(Task::class.java)
                        print(task!!.getTaskDescription())
                        Log.i("firebase", "Got value"+task.getTaskDescription())
                        if (task != null ) {
                            taskList.add(task)
                        }


                    }
                    runOnUiThread {
                        Log.i("firebase", "Got value" + taskList.size)
                        taskAdapter = TaskAdapter(this@AdminActivity, taskList)
                        recyclerTasks.adapter = taskAdapter

                        val itemDecoration = DividerItemDecoration(this@AdminActivity, DividerItemDecoration.VERTICAL)
                        recyclerTasks.addItemDecoration(itemDecoration)

                        //recyclerTodo.layoutManager = GridLayoutManager(this, 2)
                        //recyclerTodo.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)


                        val touchCallbakList = TaskRecyclerTouchCallback(taskAdapter)
                        val itemTouchHelper = ItemTouchHelper(touchCallbakList)
                        itemTouchHelper.attachToRecyclerView(recyclerTasks)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@AdminActivity, databaseError.code, Toast.LENGTH_SHORT)
                        .show()
                }

            })

        }.start()


        saveStartInfo()
    }

    private fun saveStartInfo() {
        var sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.putString(KEY_LAST_USED, Date(System.currentTimeMillis()).toString())
        editor.apply()
    }

    private fun wasStartedBefore(): Boolean {
        var sharedPref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var lastTime = sharedPref.getString(KEY_LAST_USED, "This is the first time")
        Toast.makeText(this, lastTime, Toast.LENGTH_LONG).show()

        return sharedPref.getBoolean(KEY_STARTED, false)
    }

    fun showAddTodoDialog() {
        TaskDialog().show(supportFragmentManager, "Dialog")
    }

    var editIndex: Int = -1

    public fun showEditTodoDialog(taskToEdit: Task, index: Int) {
        editIndex = index

        val editItemDialog = TaskDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_EDIT, taskToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager, "EDITDIALOG")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun taskCreated(task: Task) {
        saveTask(task)
    }

    private fun saveTask(task: Task) {
        Thread{
            databaseReference.child("Tasks").child(task.getTaskDate()!!).setValue(task)

            runOnUiThread {
                taskAdapter.addTask(task)
            }
        }.start()
    }

    override fun taskUpdated(task: Task) {
        Thread{
            databaseReference.child("Tasks").child(task.getTaskDate()!!).setValue(task)

            runOnUiThread {
                taskAdapter.updateTask(task, editIndex)
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseAuth.getInstance().signOut()

    }

    override fun onRestart() {
        super.onRestart()
        FirebaseAuth.getInstance().signOut()

    }

    override fun onResume() {
        super.onResume()
    }
}