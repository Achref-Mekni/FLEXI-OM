package com.example.flex.Bag

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.flex.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.w3c.dom.Text


class ItemAdapter(var context: Context, var array: ArrayList<Medicine>)
    :BaseAdapter() {
    override fun getCount(): Int {
        return array.size
    }

    override fun getItem(position: Int): Any {
        return array.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View = View.inflate(context, R.layout.custom_list, null)

        var icons: ImageView = view.findViewById(R.id.icon_list)
        var name: TextView = view.findViewById(R.id.title_text_view)
        var quantity: TextView = view.findViewById(R.id.detail_text_view)

        var items : Medicine = array[position]
        //icons.setImageResource(items.icon!!)
        name.text = items.med_name
        quantity.text = items.quantity.toString()

        val finalName = items.med_name + ".jpg"
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageReference = firebaseStorage!!.reference
        storageReference.child("med-icons").child(finalName).downloadUrl.addOnSuccessListener { uri ->
            // Using "Picasso" (http://square.github.io/picasso/) after adding the dependency in the Gradle.
            // ".fit().centerInside()" fits the entire image into the specified area.
            // Finally, add "READ" and "WRITE" external storage permissions in the Manifest.
            Picasso.get().load(uri).fit().centerInside().into(icons)

            }




        return view!!
    }


}



