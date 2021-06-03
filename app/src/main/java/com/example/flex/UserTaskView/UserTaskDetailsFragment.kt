package com.example.flex.UserTaskView

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.flex.Bag.Medicine
import com.example.flex.HomeActivity
import com.example.flex.R
import com.example.flex.Tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.romainpiel.shimmer.Shimmer
import kotlinx.android.synthetic.main.fragment_task_details.view.*


class UserTaskDetailsFragment : Fragment() {

    companion object {
        const val TAG = "TAG_FRAGMENT_DETAILS"
        var Opened = false
        var items: Array<String> = arrayOf("--Select--")
        var selectedMed: String = ""
        var deductedNum: Int = 0
        var medQuant: Int = 0
        var newQuantity = 0
    }

    lateinit var taskAdapter: UserTaskAdapter
    private lateinit var databaseReference: DatabaseReference
    private var firebaseAuth: FirebaseAuth? = null
    private var stage_note: String? = null
    private var onWayClicked: Boolean = false
    private var waitingClicked: Boolean = false
    private var meetingClicked: Boolean = false
    private lateinit var myContext: FragmentActivity

    override fun onAttach(activity: Activity) {
        myContext = activity as HomeActivity
        super.onAttach(activity)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Opened = true

        val rootView = inflater.inflate(R.layout.fragment_task_details, container, false)
        val onWay = rootView.onthewayButton
        val waiting = rootView.waitingroomButton
        val meeting = rootView.meetingButton
        val address = rootView.addView
        val type = rootView.title_type
        val deadline = rootView.deadView
        val notes = rootView.editStageNote
        val update = rootView.updateButton
        val orderButt = rootView.orderButton
        val repButt = rootView.reportButton
        val map_icon = rootView.map_icon
        val ded_med = rootView.deduct_med_Button
        val name = rootView.nameView
        //val rep = rootView.editReport
        items = arrayOf("--Select--")
        selectedMed = ""
        deductedNum = 0
        medQuant = 0
        newQuantity = 0

        //***Type animation
        var shimmer = Shimmer()
        shimmer.start(type)
        //***
        notes.isEnabled = false

        var currTask = UserTaskAdapter.opened_task!!

        if(currTask.getTaskType()=="Pharmacy")
        {
            orderButt.isEnabled = true
            orderButt.isVisible = true
        }

        if (currTask.getTaskType() == "Pharmacy")
        {
            waiting.text = "Queue"
        }

        //***db info
        databaseReference = Firebase.database.reference
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth!!.currentUser
        //***
        var newNotes =
            databaseReference.child("Tasks").child(currTask.getTaskDate()!!).child("notes")
        newNotes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (ds in dataSnapshot.children) {

                    val note: String? = ds.value.toString()
                    val key: String? = ds.key.toString()
                    //Log.i("firebase", "Rani Hne"+ key + note )
                    currTask.setTaskStageNote(key!!, note!!)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), databaseError.code, Toast.LENGTH_SHORT)
                    .show()
            }
        })
        var newReport =
            databaseReference.child("Tasks").child(currTask.getTaskDate()!!).child("report")
        newReport.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val reportText = dataSnapshot.value.toString()
                currTask.report = reportText

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), databaseError.code, Toast.LENGTH_SHORT)
                    .show()
            }
        })



        address.text = currTask.getTaskAddress()
        type.text = currTask.getTaskType()
        deadline.text = currTask.getTaskDeadline().toString()
        name.text = currTask.getTaskDestName()

        onWay.setOnClickListener {
            notes.isEnabled = true
            currTask.stage = "On The Way"
            onWayClicked = true
            notes.setText(currTask.getTaskStageNote(currTask.stage!!))
            onWay.background = ResourcesCompat.getDrawable(
                requireActivity().resources,
                R.drawable.status_button_border_after_click,
                null
            )
            waiting.background = ResourcesCompat.getDrawable(
                requireActivity().resources,
                R.drawable.status_button_border_before_click,
                null
            )
            meeting.background = ResourcesCompat.getDrawable(
                requireActivity().resources,
                R.drawable.status_button_border_before_click,
                null
            )
        }

        waiting.setOnClickListener {
            if (onWayClicked) {
                currTask.stage = "Waiting Room"
                notes.setText(currTask.getTaskStageNote(currTask.stage!!))
                waitingClicked = true
                onWay.background = ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.status_button_border_before_click,
                    null
                )
                waiting.background = ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.status_button_border_after_click,
                    null
                )
                meeting.background = ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.status_button_border_before_click,
                    null
                )
            }
        }

        meeting.setOnClickListener {
            if(onWayClicked && waitingClicked) {
                currTask.stage = "Meeting"
                notes.setText(currTask.getTaskStageNote(currTask.stage!!))
                meetingClicked = true
                ded_med.isEnabled = true
                ded_med.isVisible = true
                onWay.background = ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.status_button_border_before_click,
                    null
                )
                waiting.background = ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.status_button_border_before_click,
                    null
                )
                meeting.background = ResourcesCompat.getDrawable(
                    requireActivity().resources,
                    R.drawable.status_button_border_after_click,
                    null
                )
            }
        }

        // to complete later
        update.setOnClickListener {
            shimmer.cancel()
            //Log.i("firebase", "D5alna fi "+ currTask.closed )
            //Log.i("firebase", "D5alna fi meeting "+ meetingClicked )
            //updateQuantity(user,databaseReference)
            if (meetingClicked == true)
            {
                currTask.closed = true
                //Log.i("firebase", "D5alna fi "+ currTask.closed )
                currTask.done = true
                updateClosed(user,databaseReference,currTask)
            }
            Opened = false
            //activity?.getFragmentManager()!!.popBackStack()
            activity?.onBackPressed()
        }



        notes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {}


            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                var stage = currTask.getTaskStage()
                currTask.setTaskStageNote(stage!!, notes!!.text.toString())
                Log.i("firebase", "User is" + user.uid)
                var newData =
                    databaseReference.child("Tasks").child(currTask.getTaskDate()!!)
                        .child("notes")
                newData.setValue(currTask.stage_notes)

            }
        })

        repButt.setOnClickListener {

            val fragManager: FragmentManager = myContext.supportFragmentManager //If using fragments from support v4
            ReportDialog().show(fragManager, "ReportDialog")

        }
        ded_med.setOnClickListener {
            val fragManager: FragmentManager = myContext.supportFragmentManager //If using fragments from support v4
            DeductMedDialog().show(fragManager, "DeductDialog")
        }
        orderButt.setOnClickListener {
            val fragManager: FragmentManager = myContext.supportFragmentManager //If using fragments from support v4
            OrderDialog().show(fragManager, "OrderDialog")
        }
        //***Access Google Maps
        map_icon.setOnClickListener {
            // Create a Uri from an intent string. Use the result to create an Intent.
            val gmmIntentUri = Uri.parse("geo:0,0?q="+currTask.getTaskAddress())

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps")

            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent)
        }
        //***

       /* show_ded.setOnClickListener {
            if (dropdown.isVisible == false && medNum.isVisible == false)
            {
                dropdown.isVisible = true
                medNum.isVisible = true
            }
            else
            {
                dropdown.isVisible = false
                medNum.isVisible = false
            }

        }*/

     /*   ded_med.setOnClickListener {
            AlertDialog.Builder(context, R.style.AlertDialogTheme)
                .setTitle("Message")
                .setMessage("Are you sure you want to update this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    // Continue with delete operation
                    deductMedicines(user,databaseReference,medNum)
                }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }*/

        return rootView
    }


    private class NumericKeyBoardTransformationMethod : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: View?): CharSequence {
            return source
        }
    }

    private fun getMedsNames(user:FirebaseUser, db:DatabaseReference,drop:Spinner,Mcontext:Context)
    {
        //var arrayM: MutableList<String> = mutableListOf()
        db!!.child("Bags").child("b:"+user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Log.i("firebase", "Rani Hne "+ user.uid  )

                for (ds in dataSnapshot.children) {
                    val medicine: Medicine? =
                        ds.getValue(Medicine::class.java)

                    if (medicine != null ) {
                        items = append(items,medicine.med_name!!)
                    }

                }
                //Log.i("firebase", "Rani Hne"+ items.size )
                val arrayAdapter: ArrayAdapter<*>
                arrayAdapter = ArrayAdapter(Mcontext, android.R.layout.simple_list_item_1, items)
                //set the spinners adapter to the previously created one.
                drop.adapter = arrayAdapter

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), databaseError.code, Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }
    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    private fun deductMedicines(user:FirebaseUser, db:DatabaseReference,medNum:EditText)
    {
        if (selectedMed != "--Select--")
            {
            db!!.child("Bags").child("b:"+user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (ds in dataSnapshot.children) {

                        val medicine: Medicine? =
                            ds.getValue(Medicine::class.java)
                        Log.i("firebase", "Dwe Name "+ medicine!!.getMedName())
                        if (medicine != null) {

                            if (medicine.getMedName() == selectedMed) {
                                Log.i("firebase", "Dwe value 9dim "+ medicine.getMedQuantity())
                                medQuant = medicine.getMedQuantity()
                                break

                            }
                        }

                    }
                    deductedNum = medNum.text.toString().toInt()
                    newQuantity = medQuant - deductedNum
                    updateQuantity(user,db)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), databaseError.code, Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }
    }

    private fun updateQuantity(user: FirebaseUser,db: DatabaseReference)
    {
        Log.i("firebase", "Dwe Quantity Jdida "+ newQuantity)
        db!!.child("Bags").child("b:"+user.uid).child(selectedMed).child("quantity")
            .setValue(newQuantity)
    }

    private fun updateClosed(user: FirebaseUser,db: DatabaseReference,currTask: Task)
    {
        db!!.child("Tasks").child(currTask.getTaskDate()!!).child("closed")
            .setValue(true)
    }


}

