package com.example.flex.Tasks

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.flex.AdminActivity
import com.example.flex.R
import kotlinx.android.synthetic.main.task_dialog.view.*
import java.util.*
import java.util.concurrent.TimeUnit


class TaskDialog: DialogFragment() {

    interface TaskHandler{
        fun taskCreated(task: Task)
        fun taskUpdated(task: Task)
    }

    lateinit var taskHandler: TaskHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is TaskHandler){
            taskHandler = context
        } else {
            throw RuntimeException(
                "The Activity is not implementing the TodoHandler interface.")
        }
    }

    lateinit var etTaskText: EditText
    //lateinit var cbTaskDone: CheckBox
    lateinit var etDestName: EditText
    lateinit var etTaskAddress: EditText
    lateinit var etTaskType: EditText


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setTitle("Task dialog")
        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.task_dialog, null
        )

        etTaskText = dialogView.etTaskText
        //cbTaskDone = dialogView.cbTaskDone
        etDestName = dialogView.etDestinationName
        etTaskType = dialogView.etTaskType
        etTaskAddress = dialogView.etTaskAddress



        dialogBuilder.setView(dialogView)

        val arguments = this.arguments
        // if we are in EDIT mode
        if (arguments != null && arguments.containsKey(AdminActivity.KEY_EDIT)) {
            val taskItem = arguments.getSerializable(AdminActivity.KEY_EDIT) as Task

            etTaskText.setText(taskItem.getTaskDescription())
            //cbTaskDone.isChecked = taskItem.done
            etTaskAddress.setText(taskItem.getTaskAddress())
            etTaskType.setText(taskItem.getTaskType())
            etDestName.setText(taskItem.getTaskDestName())

            dialogBuilder.setTitle("Edit todo")
        }


        dialogBuilder.setPositiveButton("Ok") {
                _, _ ->
        }
        dialogBuilder.setNegativeButton("Cancel") {
                _, _ ->
        }

        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etTaskText.text.isNotEmpty() && etTaskAddress.text.isNotEmpty() && etDestName.text.isNotEmpty() && etTaskType.text.isNotEmpty()) {
                val arguments = this.arguments
                // IF EDIT MODE
                if (arguments != null && arguments.containsKey(AdminActivity.KEY_EDIT)) {
                    handleTodoEdit()
                } else {
                    handleTodoCreate()
                }

                dialog!!.dismiss()
            } else {
                etTaskText.error = "This field can not be empty"
                etDestName.error = "This field can not be empty"
                etTaskType.error = "This field can not be empty"
                etTaskAddress.error = "This field can not be empty"
            }
        }
    }

    private fun handleTodoCreate() {
        taskHandler.taskCreated(
            Task(
                Date(System.currentTimeMillis()).toString(),
                etTaskText.text.toString(),
                etDestName.text.toString(),
                etTaskAddress.text.toString(),
                etTaskType.text.toString(),
                Date(System.currentTimeMillis()+TimeUnit.DAYS.toMillis(7))

            )
        )
    }

    private fun handleTodoEdit() {
        val taskToEdit = arguments?.getSerializable(
            AdminActivity.KEY_EDIT
        ) as Task
        taskToEdit.description = etTaskText.text.toString()
        //taskToEdit.done = cbTaskDone.isChecked
        taskToEdit.address = etTaskAddress.text.toString()
        taskToEdit.type = etTaskType.text.toString()
        taskToEdit.Destination_Name = etDestName.text.toString()

        taskHandler.taskUpdated(taskToEdit)
    }

}