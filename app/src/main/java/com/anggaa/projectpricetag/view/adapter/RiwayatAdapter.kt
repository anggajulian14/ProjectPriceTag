package com.anggaa.projectpricetag.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anggaa.projectpricetag.R
import com.anggaa.projectpricetag.data.entity.Pricetagnormal
import com.anggaa.projectpricetag.model.PricetagNormal
import com.anggaa.projectpricetag.model.PricetagPromo
import com.anggaa.projectpricetag.model.PrintLogLogPricetagPromo
import com.anggaa.projectpricetag.model.PrintLogPricetagNormal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class PrintLogItem {
    data class Normal(val data: PrintLogPricetagNormal) : PrintLogItem()
    data class Promo(val data: PrintLogLogPricetagPromo) : PrintLogItem()
}

interface OnItemClickListener {
    fun onItemClick(item: PrintLogItem)
}


class RiwayatAdapter(
    dataList: List<PrintLogPricetagNormal>,
    dataListPromo: List<PrintLogLogPricetagPromo>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    private val combinedList: List<PrintLogItem> = (dataList.map { PrintLogItem.Normal(it) } + dataListPromo.map { PrintLogItem.Promo(it) })
        .sortedByDescending { it.getDate() } // Mengurutkan secara terbalik berdasarkan tanggal

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggal: TextView = itemView.findViewById(R.id.textViewTanggal)
        val namaFile: TextView = itemView.findViewById(R.id.textViewNamaProduk)
        val status: TextView = itemView.findViewById(R.id.textViewStatus)

        fun bind(item: PrintLogItem, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat_produk, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = combinedList[position]
        holder.bind(item, itemClickListener)

        when (item) {
            is PrintLogItem.Normal -> {
                holder.tanggal.text = simpleDateFormat(item.data.tanggal)
                holder.namaFile.text = item.data.Nama
                holder.status.text = "Pricetag Normal"
            }
            is PrintLogItem.Promo -> {
                holder.tanggal.text = simpleDateFormat(item.data.tanggal)
                holder.namaFile.text = item.data.Nama
                holder.status.text = "Pricetag Promo"
            }
        }
    }

    override fun getItemCount(): Int {
        return combinedList.size
    }

    private fun simpleDateFormat(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        val dateParsed = inputFormat.parse(date)
        return outputFormat.format(dateParsed!!)
    }

    private fun PrintLogItem.getDate(): Date {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return when (this) {
            is PrintLogItem.Normal -> inputFormat.parse(data.tanggal)!!
            is PrintLogItem.Promo -> inputFormat.parse(data.tanggal)!!
        }
    }
}

