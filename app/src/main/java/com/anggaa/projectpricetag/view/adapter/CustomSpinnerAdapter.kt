package com.anggaa.projectpricetag.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.anggaa.projectpricetag.R

class CustomSpinnerAdapter(context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, R.layout.spinner_item, items) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.spinner_item, parent, false)

        val textView = customView.findViewById<TextView>(R.id.Text)
        textView.text = items[position]

        return customView
    }
}
