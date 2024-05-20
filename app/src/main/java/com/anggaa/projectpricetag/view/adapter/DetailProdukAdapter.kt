package com.anggaa.projectpricetag.view.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.anggaa.projectpricetag.R
import com.anggaa.projectpricetag.data.AppDatabase
import com.anggaa.projectpricetag.data.entity.Pricetagnormal
import com.anggaa.projectpricetag.view.activity.DetailProduk

class DetailProdukAdapter(private val context: Context, private var itemList: MutableList<Pricetagnormal>) : RecyclerView.Adapter<DetailProdukAdapter.MyViewHolder>() {

    private var itemListFull: List<Pricetagnormal> = ArrayList(itemList)
    private var currentQuery: String = ""
    private var currentCategory: String? = null
    private var currentBrand: String? = null
    private var currentSupplier: String? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaProduk: TextView = itemView.findViewById(R.id.NamaProduk)
        val brandProduk: TextView = itemView.findViewById(R.id.BrandProduk)
        val supplierProduk: TextView = itemView.findViewById(R.id.SupplierProduk)
        val kategoriProduk: TextView = itemView.findViewById(R.id.KategoriProduk)
        val barcodeProduk: TextView = itemView.findViewById(R.id.BarcodeProduk)

        val dialog = Dialog(context)

        init {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val data = itemList[position]

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.dialog_detail_produk)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    dialog.findViewById<TextView>(R.id.NamaProduk).text = data.Nama

                    dialog.findViewById<CardView>(R.id.ButtonEdit).setOnClickListener {
                        val database = AppDatabase.getInstance(context)
                        dialog.dismiss()

                        val dialog_edit = Dialog(context)
                        dialog_edit.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog_edit.setContentView(R.layout.dialog_edit_produk)
                        dialog_edit.setCancelable(true)
                        dialog_edit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                        val edtNamaProduk = dialog_edit.findViewById<EditText>(R.id.EDTNamaProduk)
                        val edtBarcodeProduk = dialog_edit.findViewById<EditText>(R.id.EDTBarcodeProduk)
                        val edtKategoriProduk = dialog_edit.findViewById<EditText>(R.id.EDTKategoriProduk)
                        val edtSupplierProduk = dialog_edit.findViewById<EditText>(R.id.EDTSupplierProduk)
                        val edtBrandProduk = dialog_edit.findViewById<EditText>(R.id.EDTBrandProduk)

                        edtNamaProduk.setText(data.Nama)
                        edtBarcodeProduk.setText(data.Barcode)
                        edtKategoriProduk.setText(data.Kategori)
                        edtSupplierProduk.setText(data.Supplier)
                        edtBrandProduk.setText(data.Brand)

                        dialog_edit.findViewById<Button>(R.id.ButtonBatal).setOnClickListener {
                            dialog_edit.dismiss()
                        }

                        dialog_edit.findViewById<Button>(R.id.ButtonKonfirmasi).setOnClickListener {
                            database.PricetagnormalDAO().updateAll(
                                edtBarcodeProduk.text.toString(),
                                edtNamaProduk.text.toString(),
                                edtKategoriProduk.text.toString(),
                                edtSupplierProduk.text.toString(),
                                edtBrandProduk.text.toString()
                            )
                            notifyDataSetChanged()
                            notifyItemChanged(position)
                            dialog_edit.dismiss()
                            val intent = Intent(context, DetailProduk::class.java)
                            intent.putExtra("Barcode", data.Barcode)
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }

                        dialog_edit.show()
                    }


                    dialog.findViewById<CardView>(R.id.ButtonHapus).setOnClickListener {
                        val database = AppDatabase.getInstance(context)
                        dialog.dismiss()
                        val validasi_hapus = Dialog(context)

                        validasi_hapus.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        validasi_hapus.setCancelable(true)
                        validasi_hapus.setContentView(R.layout.dialog_validasi_hapus)
                        validasi_hapus.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        validasi_hapus.show()

                        validasi_hapus.findViewById<Button>(R.id.ButtonBatal).setOnClickListener {
                            validasi_hapus.dismiss()
                        }

                        validasi_hapus.findViewById<Button>(R.id.ButtonHapus).setOnClickListener {
                            // Lakukan operasi penghapusan pada thread yang berbeda jika diperlukan
                            Thread {
                                database.PricetagnormalDAO().deleteByBarcode(data.Barcode)
                                (itemList as MutableList).removeAt(position) // Pastikan itemList adalah MutableList
                                (context as Activity).runOnUiThread {
                                    notifyItemRemoved(position)
                                    validasi_hapus.dismiss()
                                }
                            }.start()
                        }
                    }

                    dialog.findViewById<Button>(R.id.ButtonBatal).setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()

                }
                true
            }
        }
    }

    fun updateItems(newItems: List<Pricetagnormal>) {
        itemList.clear()
        itemList.addAll(newItems)
        itemListFull = ArrayList(newItems) // Update the full list as well
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_produk, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.namaProduk.text = currentItem.Nama
        holder.barcodeProduk.text = currentItem.Barcode
        if (currentItem.Kategori == "") {
            holder.kategoriProduk.text = "Belum diisi"
        }
        else {
            holder.kategoriProduk.text = currentItem.Kategori
        }
        if (currentItem.Brand == "") {
            holder.brandProduk.text = "Belum diisi"
        }
        else {
            holder.brandProduk.text = currentItem.Brand
        }
        if (currentItem.Supplier == "") {
            holder.supplierProduk.text = "Belum diisi"
        }
        else {
            holder.supplierProduk.text = currentItem.Supplier
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun filter(query: String) {
        currentQuery = query
        itemList = itemListFull.filter {
            (it.Nama.contains(query, ignoreCase = true))
        }.toMutableList()
        notifyDataSetChanged()
    }
}
