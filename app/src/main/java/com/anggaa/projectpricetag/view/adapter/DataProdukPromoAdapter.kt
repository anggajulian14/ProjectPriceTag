package com.anggaa.projectpricetag.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anggaa.projectpricetag.R
import com.anggaa.projectpricetag.model.PricetagNormal
import com.anggaa.projectpricetag.model.PricetagPromo
import com.anggaa.projectpricetag.model.SharedPreferencesManager

class DataProdukPromoAdapter(private val context: Context, private val dataList: MutableList<PricetagPromo>,private val jumlahDataTextView: TextView) :
    RecyclerView.Adapter<DataProdukPromoAdapter.DataViewHolder>() {

    // Inner class untuk merepresentasikan ViewHolder
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewItem: TextView = itemView.findViewById(R.id.textViewName)
        val buttonHapus = itemView.findViewById<Button>(R.id.buttonRemove)

        init {

            buttonHapus.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val data = dataList[position]
                    dataList.remove(data)
                    SharedPreferencesManager.removeProductItem(context, data)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, dataList.size)
                    updateJumlahDataTextView()
                }
            }
        }
    }

    private fun updateJumlahDataTextView() {
        jumlahDataTextView.text = "Jumlah Data : ${dataList.size}"
    }

    // Method untuk membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_selected_employee, parent, false)
        return DataViewHolder(view)
    }

    // Method untuk menghubungkan data dengan ViewHolder
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val dataItem = dataList[position]
        holder.textViewItem.text = dataItem.Nama
    }

    // Method untuk mengembalikan jumlah item dalam data set
    override fun getItemCount(): Int {
        return dataList.size
    }
}