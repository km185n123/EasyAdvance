package com.paparazziapps.pretamistapp.modulos.clientes.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.paparazziapps.pretamistapp.modulos.clientes.pojo.Client

class ClientAdapter(context: Context, objects: List<Client>) : ArrayAdapter<Client>(context, 0, objects) {


    private val fullList: List<Client> = objects
    private var filteredList: List<Client> = objects

    override fun getCount(): Int {
        return filteredList.size
    }

    override fun getItem(position: Int): Client {
        return filteredList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
        val item = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = "${item?.nombres} (${item?.dni})"
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    filterResults.values = fullList
                    filterResults.count = fullList.size
                } else {
                    val query = constraint.toString().lowercase().trim()
                    val filtered = fullList.filter {
                        it.nombres?.lowercase()?.contains(query) == true || it.dni?.contains(query) == true
                    }
                    filterResults.values = filtered
                    filterResults.count = filtered.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = if (results?.values == null) {
                    emptyList()
                } else {
                    results.values as List<Client>
                }
                notifyDataSetChanged()
            }
        }
    }
}