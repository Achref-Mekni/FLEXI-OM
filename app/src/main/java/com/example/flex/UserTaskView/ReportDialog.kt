package com.example.flex.UserTaskView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.example.flex.R
import com.example.flex.Tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.reportdialog_fragment.*
import kotlinx.android.synthetic.main.reportdialog_fragment.view.*

class ReportDialog : DialogFragment() {

    private lateinit var myView: View;
    private lateinit var databaseReference: DatabaseReference
    private var firebaseAuth: FirebaseAuth? = null
    private var currTask: Task = UserTaskAdapter.opened_task!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        myView = inflater.inflate(R.layout.reportdialog_fragment, container, false)
        return myView
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        var send_butt = myView.btn_send_report
        var text_box = myView.tv_reporttext
        databaseReference = Firebase.database.reference
        firebaseAuth = FirebaseAuth.getInstance()
        send_butt.setOnClickListener {
            if (text_box.text.toString() != "") {
                currTask.report= text_box.text.toString()
                firebaseAuth = FirebaseAuth.getInstance()
                var newData =
                    databaseReference.child("Tasks").child(currTask.getTaskDate()!!)
                        .child("report")
                newData.setValue(currTask.report)
                dialog!!.dismiss()
            }
        }
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}