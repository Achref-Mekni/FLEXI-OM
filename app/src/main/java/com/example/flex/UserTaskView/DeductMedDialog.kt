package com.example.flex.UserTaskView

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
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
import kotlinx.android.synthetic.main.deduct_dialog.view.*
import kotlinx.android.synthetic.main.order_dialog.view.*
import java.util.*

class DeductMedDialog : DialogFragment() {

    companion object{
        var frag_items: Array<String> = arrayOf("--Select--")
        var selectedMed: String = ""
        var medQuant: Int = 0
        var deductedNum: Int = 0
        var newQuantity = 0
    }

    private lateinit var myView: View;
    private lateinit var databaseReference: DatabaseReference
    private var firebaseAuth: FirebaseAuth? = null
    private var currTask: Task = UserTaskAdapter.opened_task!!
    private lateinit var myContext: FragmentActivity
    var content : MutableMap<String,Int> = mutableMapOf()
    private var mTableLayout: TableLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        myView = inflater.inflate(R.layout.deduct_dialog, container, false)
        return myView
    }

    override fun onStart() {
        super.onStart()
        myContext = activity as HomeActivity
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val height = (resources.displayMetrics.heightPixels * 1.40).toInt()
        var add_butt = myView.addButton_deduct
        var deduct = myView.deductButton
        var medNum = myView.insert_deduct_dialog
        var dropdown = myView.spinner_deduct_dialog
        var exit = myView.exitButton_deduct
        mTableLayout = myView.tableDeductMeds
        frag_items = arrayOf("--Select--")
        selectedMed = ""
        deductedNum = 0
        medQuant = 0
        newQuantity = 0
        medNum.transformationMethod = NumericKeyBoardTransformationMethod()
        databaseReference = Firebase.database.reference
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth!!.currentUser
        //***spinner part
        getMedsNames(user ,databaseReference,dropdown,this.myContext)
        dropdown.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                Log.i("firebase", "Dwe Selected frag "+ frag_items[0])
                selectedMed = frag_items[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })
        //****
        add_butt.setOnClickListener {
            if (medNum.text.toString().toInt() != 0 && selectedMed != "--Select--")
            {
                content[selectedMed] = medNum.text.toString().toInt()
                loadData(selectedMed,medNum.text.toString().toInt())
            }
        }

        deduct.setOnClickListener {
            if (content.isNotEmpty())
            {
                for ((key, value) in content.entries) {
                    deductMedicines(user, databaseReference, key, value)
                }
            }
        }

        exit.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

    }


    private class NumericKeyBoardTransformationMethod : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: View?): CharSequence {
            return source
        }
    }
    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }
    private fun deductMedicines(user:FirebaseUser, db:DatabaseReference,medName:String,medNum:Int)
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

                            if (medicine.getMedName() == medName) {
                                Log.i("firebase", "Dwe value 9dim "+ medicine.getMedQuantity())
                                medQuant = medicine.getMedQuantity()
                                break

                            }
                        }

                    }
                    deductedNum = medNum
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

    private fun loadData(name:String, quant: Int) {
        for (index in 0 until mTableLayout!!.childCount) {
            //val nextChild : TextView  = (TextView)mTableLayout!!.getChildAt(index)
            val nextChild = (mTableLayout as TableLayout).getChildAt(index) as TableRow
            val tv = nextChild.getChildAt(1) as TextView
            if (tv.id == frag_items.indexOf(name))
            {
                tv.text = quant.toString()
                return
            }
        }

        val tv1 = TextView(context)
        val tv2 = TextView(context)
        tv2.id = frag_items.indexOf(name)
        tv2.setPadding(30,0,0,0)
        val tr = TableRow(context)
        tr.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
        tv1.text = name
        tv2.text = quant.toString()
        tv1.setTextColor(Color.BLACK)
        tv2.setTextColor(Color.BLACK);
        tr.addView(tv1)
        tr.addView(tv2)
        mTableLayout!!.addView(tr, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

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
                        frag_items = append(frag_items,medicine.med_name!!)
                    }

                }
                //Log.i("firebase", "Rani Hne"+ items.size )
                val arrayAdapter: ArrayAdapter<*>
                arrayAdapter = ArrayAdapter(Mcontext, android.R.layout.simple_list_item_1,
                    frag_items
                )
                //set the spinners adapter to the previously created one.
                drop.adapter = arrayAdapter

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), databaseError.code, Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }



}