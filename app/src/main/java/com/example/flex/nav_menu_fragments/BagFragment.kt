package com.example.flex.nav_menu_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.flex.Bag.ItemAdapter
import com.example.flex.Bag.Medicine
import com.example.flex.Bag.MedicinesAdapter
import com.example.flex.CircleTransform
import com.example.flex.R
import com.example.flex.UserTaskView.UserTaskAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.fragment_bag.view.*
import kotlinx.android.synthetic.main.tryout_list.view.*


class BagFragment : Fragment(), AdapterView.OnItemClickListener {

    companion object {
        const val TAG = "TAG_FRAGMENT_BAG"
    }

    //new style code
    private var listView: ListView ? = null
    private var itemAdapter: ItemAdapter? = null
    private var array: ArrayList<Medicine> ? = null
    private var firebaseAuth: FirebaseAuth? = null


    //lateinit var medicinesAdapter: MedicinesAdapter
    //private var  med_list:ListView? = null
    private var databaseReference: DatabaseReference? = null
    //private var firebaseAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val rootView = inflater.inflate(R.layout.fragment_bag, container, false)
        //new style code
        val rootView = inflater.inflate(R.layout.tryout_list, container, false)
        listView = rootView.card_view_list_view
        array = ArrayList()
        databaseReference = FirebaseDatabase.getInstance().reference
        //array = setDataItems()
        //itemAdapter = this.context?.let { ItemAdapter(it,array!!) }

        Thread {

            firebaseAuth = FirebaseAuth.getInstance()
            //firebaseDatabase = FirebaseDatabase.getInstance()
            val user = firebaseAuth!!.currentUser
            Log.i("firebase", "User is"+user.uid)
            //val databaseReference = firebaseDatabase!!.reference.child("Tasks")
            databaseReference!!.child("Bags").child("b:"+user.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (ds in dataSnapshot.children) {
                        val medicine: Medicine? =
                            ds.getValue(Medicine::class.java)

                        if (medicine != null ) {
                            array!!.add(medicine)
                        }

                    }
                    runOnUiThread {
                        Log.i("firebase", "Got value" + array!!.size)
                        itemAdapter = context?.let { ItemAdapter(it,array!!) }
                        listView?.adapter = itemAdapter
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), databaseError.code, Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }.start()

        //listView?.adapter = itemAdapter
        listView?.onItemClickListener = this






        /*med_list = rootView.medecine_list

        databaseReference = Firebase.database.reference

        Thread {

            firebaseAuth = FirebaseAuth.getInstance()
            //firebaseDatabase = FirebaseDatabase.getInstance()
            val user = firebaseAuth!!.currentUser
            Log.i("firebase", "User is"+user.uid)
            //val databaseReference = firebaseDatabase!!.reference.child("Tasks")
            databaseReference.child("Bags").child("b:"+user.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var stock: ArrayList<Medicine> = ArrayList()

                    for (ds in dataSnapshot.children) {
                        val medicine: Medicine? =
                            ds.getValue(Medicine::class.java)

                        //print(task!!.getTaskDescription())
                        //Log.i("firebase", "Got value"+task!!.getTaskDescription())
                        if (medicine != null ) {
                            stock.add(medicine)
                        }


                    }
                    runOnUiThread {
                        Log.i("firebase", "Got value" + stock.size)
                        medicinesAdapter = MedicinesAdapter(requireContext(), R.layout.medicines_adapter_view,stock)
                        med_list!!.adapter = medicinesAdapter
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(), databaseError.code, Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }.start()*/


        return rootView
    }

    /*private fun setDataItems(): ArrayList<Medicine>
    {
        var listItem: ArrayList<Medicine> = ArrayList()
        //for loop to add all items mel base
        listItem.add(Medicine()) //njibhom mel base ba3d

        return listItem

    }*/


    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        var items:Medicine = array!!.get(position)
        Toast.makeText(context,items.med_name,Toast.LENGTH_LONG).show()

    }

}