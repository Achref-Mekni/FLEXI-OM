package com.example.flex.Bag

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.flex.R


class MedicinesAdapter(context: Context, resource: Int, objects: ArrayList<Medicine>) : ArrayAdapter<Medicine?>(context, resource, objects as List<Medicine?>) {
    private val mContext: Context = context
    private val mResource: Int
    private var lastPosition = -1

    /**
     * Holds variables in a View
     */
    private class ViewHolder {
        var name: TextView? = null
        var quantity: TextView? = null
        var provider: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //get the Med information
        var convertingView: View? = convertView
        val name: String = getItem(position)?.getMedName()!!
        val quantity: Int = getItem(position)?.getMedQuantity()!!


        //Create the Med object with the information
        val med = Medicine(name, quantity)

        //create the view result for showing the animation
        val result: View

        //ViewHolder object
        val holder: ViewHolder
        if (convertingView == null) {
            val inflater = LayoutInflater.from(mContext)
            convertingView = inflater.inflate(mResource, parent, false)
            holder = ViewHolder()
            holder.name = convertingView.findViewById(R.id.textView1)
            holder.quantity = convertingView.findViewById(R.id.textView2)

            result = convertingView
            convertingView.tag = holder
        } else {
            holder = convertingView.tag as ViewHolder
            result = convertingView
        }
        val animation: Animation = AnimationUtils.loadAnimation(mContext,
            if (position > lastPosition) R.anim.load_down_anim else R.anim.load_up_anim)
        result.startAnimation(animation)
        lastPosition = position
        holder.name?.text = med.getMedName()
        holder.quantity?.text = med.getMedQuantity().toString()

        return convertingView!!
    }

    companion object {
        private const val TAG = "PersonListAdapter"
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    init {
        mResource = resource
    }
}